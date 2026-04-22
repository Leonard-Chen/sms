package com.gdut.sms.statistics.service;

import lombok.Data;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import com.gdut.sms.common.feign.OrderClient;
import org.springframework.stereotype.Service;
import com.gdut.sms.common.utils.DateTimeUtils;
import com.gdut.sms.common.feign.CustomerClient;

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
     * @param year 年份，为null则读取所有数据
     * @return 累计客户数、订单数、当月营收、订单完成率
     */
    public Map<String, Object> getStatistics(@Nullable Integer year) {
        Map<String, Object> data = new HashMap<>();

        long customerCount = customerClient.count(year).getBody();
        long totalOrder = orderClient.count(year).getBody();
        long finishedOrder = orderClient.countFinished(year).getBody();

        //累计客户数
        data.put("totalCustomer", customerCount);
        //累计订单数
        data.put("totalOrder", totalOrder);
        //本月营收
        data.put("amount", orderClient.sumAmountBetween(
                year == null ? null : DateTimeUtils.firstTimeOfTheYear(year),
                year == null ? LocalDateTime.now() : DateTimeUtils.lastTimeOfTheYear(year)
        ).getBody());

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
    public List<?> getMonthlyStatistics(Integer year) throws RuntimeException {
        @Data
        class A {
            private Long orderCount;
            private BigDecimal amount;
        }

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

        List<A> result = new ArrayList<>();

        Object[] row;
        for (int i = 0, j = 0; i < monthOfTheYear && j < list.size(); i++) {
            A data = new A();
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
     * 获取年度订单和营收趋势
     *
     * @return 每年的累计订单数和营业额
     */
    public List<?> getAnnuallyStatistics(Integer start, Integer end) throws RuntimeException {
        if (start > end) {
            throw new IllegalArgumentException("参数错误：起始年份不能大于终止年份，你在尝试“时光倒流”？");
        }

        @Data
        class A {
            private Long orderCount;
            private BigDecimal amount;
        }

        List<Object[]> list = orderClient.annually(start, end).getBody();
        if (list == null) {
            throw new RuntimeException("错误：无法获取月度数据");
        }

        List<A> result = new ArrayList<>();

        list.forEach(row -> {
            A data = new A();

            long orderCount = row[0] instanceof Long ? (long) row[0] : ((Number) row[0]).longValue();
            BigDecimal amount = new BigDecimal(row[1].toString());

            data.setOrderCount(orderCount);
            data.setAmount(amount);

            result.add(data);
        });

        return result;
    }

    /**
     * 统计主要类型订单
     *
     * @param year 年份，为null则读取所有订单数据
     * @return 已完成、服务中、待审核、已取消的订单数量
     */
    public Long[] getOrderStatistics(@Nullable Integer year) {
        return orderClient.countForStatistics(year).getBody();
    }

}
