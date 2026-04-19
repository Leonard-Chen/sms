package com.gdut.sms.statistics.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import com.gdut.sms.common.feign.OrderClient;
import org.springframework.stereotype.Service;
import com.gdut.sms.common.feign.CustomerClient;
import org.springframework.cache.annotation.Cacheable;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 后端营业数据统计核心业务
 *
 * @author ckx
 */
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final CustomerClient customerClient;

    private final OrderClient orderClient;

    /**
     * 获取核心指标
     *
     * @return 累计客户数、订单数、当月营收、订单完成率
     */
    @Cacheable(value = "stats", key = "0", unless = "#result == null || #result.empty")
    public Map<String, Object> getStatistics() {
        Map<String, Object> data = new HashMap<>();

        long customerCount = customerClient.count().getBody();
        long totalOrder = orderClient.count().getBody();
        long finishedOrder = orderClient.countFinished().getBody();

        //累计客户数
        data.put("totalCustomer", customerCount);
        //累计订单数
        data.put("totalOrder", totalOrder);
        //本月营收
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().atTime(23, 59, 59);
        data.put("monthAmount", orderClient.sumAmountBetween(startOfMonth, endOfMonth).getBody());

        //订单完成率
        data.put("accomplishedRate", totalOrder == 0
                ? 0
                : String.format("%.2f", (double) finishedOrder / totalOrder * 100) + "%");

        return data;
    }

    /**
     * 获取月度订单和营收趋势
     *
     * @param year 年份
     * @return 当年各月的累计订单数和营业额
     */
    @Cacheable(value = "monthly_stats", key = "#year", unless = "#result == null || #result.empty")
    public List<?> getMonthlyStatistics(Integer year) throws RuntimeException {
        LocalDate now = LocalDate.now();
        if (year > now.getYear()) {
            throw new IllegalArgumentException("参数错误：你是否在尝试获取未来的数据？");
        }

        List<Object[]> list = orderClient.monthly(year).getBody();
        if (list == null) {
            throw new RuntimeException("错误：无法获取月度数据");
        }

        //当年的最大月数，若为今年则取至本月份，若为往年则取全年12个月
        int monthOfTheYear = year < now.getYear() ? 12 : now.getMonthValue();

        List<MonthlyData> result = new ArrayList<>();

        Object[] row;
        for (int i = 0, j = 0; i < monthOfTheYear && j < list.size(); i++) {
            MonthlyData data = new MonthlyData();
            long orderCount = 0L;
            BigDecimal amount = BigDecimal.ZERO;

            row = list.get(j);
            int month = row[0] instanceof Integer ? (int) row[0] : ((Number) row[0]).intValue();
            if (month > monthOfTheYear) {
                throw new RuntimeException(
                        String.format("错误：今年是%d年，现在是%d月，后台查询到的结果却出现了%d月的数据。",
                                year,
                                monthOfTheYear,
                                month)
                );
            }
            if (month == i + 1) {
                orderCount = row[1] instanceof Long ? (long) row[1] : ((Number) row[1]).longValue();
                amount = new BigDecimal(row[2].toString());
                j++;
            }
            data.setOrderCount(orderCount);
            data.setAmount(amount);
            result.add(data);
        }

        return result;
    }

    /**
     * 统计主要类型订单
     *
     * @return 已完成、服务中、待审核、已取消的订单数量
     */
    public Long[] getOrderStatistics() {
        return orderClient.countForStatistics().getBody();
    }

    @Data
    private static class MonthlyData {
        private Long orderCount;
        private BigDecimal amount;
    }

}
