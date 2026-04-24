package com.gdut.sms.order.service;

import feign.FeignException;
import com.gdut.sms.common.dto.*;
import jakarta.annotation.Nullable;
import com.gdut.sms.common.entity.*;
import lombok.RequiredArgsConstructor;
import com.gdut.sms.common.utils.RandomUUID;
import org.springframework.stereotype.Service;
import com.gdut.sms.common.utils.DateTimeUtils;
import org.springframework.http.ResponseEntity;
import com.gdut.sms.common.feign.EmployeeClient;
import org.springframework.cache.annotation.Caching;
import com.gdut.sms.order.repository.OrderRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import com.gdut.sms.order.repository.ScheduleRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 后端订单管理核心业务
 * @author ckx ly
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final ScheduleRepository scheduleRepository;

    private final EmployeeClient employeeClient;

    @Cacheable(value = "order_list", key = "#username", unless = "#result == null || #result.empty")
    public List<OrderDTO> list(String username, String deptNo, List<String> authorities) {
        Set<String> roles = new HashSet<>(authorities);

        //查询权限控制
        Specification<Order> spec = (root, query, cb) -> {
            //仅【系统管理员】和【企业负责人】可查看所有订单数据
            if (roles.contains("ADMIN") || roles.contains("OWNER")) {
                return null;
            }
            //【部门经理】只能看本部门数据
            else if (roles.contains("MANAGER")) {
                return cb.equal(root.get("dept").get("deptNo"), deptNo);
            }
            //【业务人员】只能看自己创建的订单
            else {
                return cb.equal(root.get("createdBy").get("username"), username);
            }
        };

        return orderRepository.findAll(spec).stream()
                .map(OrderDTO::new)
                .toList();
    }

    public Long count(@Nullable Integer year) {
        return year == null
                ? orderRepository.count()
                : orderRepository.countByCreateTimeBetween(
                    DateTimeUtils.firstTimeOfTheYear(year),
                    DateTimeUtils.lastTimeOfTheYear(year)
        );
    }

    public Long countFinishedOrders(@Nullable Integer year) {
        return countByStatusAndYear(2, year);
    }

    public Long countPendingOrders(@Nullable Integer year) {
        return countByStatusAndYear(3, year);
    }

    public Long countOrdersToBeAudited(@Nullable Integer year) {
        return countByStatusAndYear(1, year);
    }

    public Long countCanceledOrders(@Nullable Integer year) {
        return countByStatusAndYear(5, year);
    }

    private Long countByStatusAndYear(Integer status, @Nullable Integer year) {
        return year == null
                ? orderRepository.countByOrderStatus(status)
                : orderRepository.countByOrderStatusAndCreateTimeBetween(
                    status,
                    DateTimeUtils.firstTimeOfTheYear(year),
                    DateTimeUtils.lastTimeOfTheYear(year)
        );
    }

    @Cacheable(value = "order_amount", key = "T(com.gdut.sms.common.utils.DateTimeUtils).toTimestamp(#start)" +
            "+ '::' +" +
            "T(com.gdut.sms.common.utils.DateTimeUtils).toTimestamp(#end)",
            unless = "#result == null")
    public BigDecimal sumAmountBetween(LocalDateTime start, LocalDateTime end) {
        return start == null
                ? (end == null
                    ? orderRepository.sumAmount()
                    : orderRepository.sumAmountByCreateTimeBefore(end))
                : (end == null
                    ? orderRepository.sumAmountByCreateTimeAfter(start)
                    : orderRepository.sumAmountByCreateTimeBetween(start, end)
        );
    }

    /**
     * 统计服务模块调用
     */
    public List<Object[]> getMonthlyStatistics(Integer year) {
        return orderRepository.getMonthlyStatistics(year);
    }

    /**
     * 统计服务模块调用
     */
    public List<Object[]> getAnnuallyStatistics(Integer start, Integer end) {
        return orderRepository.getAnnuallyStatistics(start, end);
    }


    @Cacheable(value = "order", key = "#no", unless = "#result == null")
    public OrderDTO get(String no) {
        return new OrderDTO(orderRepository.findByOrderNoWithCreatedBy(no)
                .orElseThrow(() -> new RuntimeException("订单不存在"))
        );
    }

    @Cacheable(value = "order", key = "#deptNo", unless = "#result == null || #result.empty")
    public List<OrderDTO> getByDept(String deptNo) {
        Department dept = new Department();
        dept.setDeptNo(deptNo);
        return orderRepository.findAllByDept(dept).stream()
                .map(OrderDTO::new)
                .toList();
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "order_list", allEntries = true),
                    @CacheEvict(value = "order_amount", allEntries = true)
            }
    )
    public OrderDTO create(OrderDTO dto, String username) {
        String no;
        do {
            no = "O" + RandomUUID.generate(19, RandomUUID.CharType.DIGIT);
        } while (orderRepository.findByOrderNo(no).isPresent());

        User user = new User();
        user.setUsername(username);

        Customer customer = new Customer();
        customer.setCustomerNo(dto.getCustomerNo());

        Department dept = new Department();
        dept.setDeptNo(dto.getDeptNo());

        Order order = new Order(dto);

        order.setOrderStatus(1);
        order.setOrderNo(no);
        order.setCustomer(customer);
        order.setDept(dept);
        order.setCreatedBy(user);
        order.setCreateTime(LocalDateTime.now());

        return new OrderDTO(orderRepository.save(order));
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "order", key = "#dto.orderNo"),
            evict = {
                    @CacheEvict(value = "order_list", allEntries = true),
                    @CacheEvict(value = "order_amount", allEntries = true)
            }
    )
    public OrderDTO update(OrderDTO dto) {
        Order order = orderRepository.findByOrderNo(dto.getOrderNo())
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        //允许在待审核(1)/待分派(7)/待接单(8)阶段修改，服务中及之后不允许直接改
        Integer status = order.getOrderStatus();
        if (status != null && status != 1 && status != 7 && status != 8) {
            throw new RuntimeException("当前订单状态不允许修改");
        }

        order.copyFrom(dto);

        return new OrderDTO(orderRepository.save(order));
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "order", key = "#orderNo"),
            evict = {
                    @CacheEvict(value = "order_list", allEntries = true),
                    @CacheEvict(value = "order_amount", allEntries = true)
            }
    )
    public OrderDTO audit(String orderNo, Integer status, String remarks, String username) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (order.getOrderStatus() != 1) {
            throw new RuntimeException("当前订单状态不允许审核");
        }

        User user = new User();
        user.setUsername(username);

        //审核通过后进入待分派(7)状态
        order.setOrderStatus(status != null && status == 2 ? Integer.valueOf(7) : status);
        order.setAuditedBy(user);
        order.setAuditTime(LocalDateTime.now());
        order.setRemarks(remarks);

        return new OrderDTO(orderRepository.save(order));
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "order", key = "#no"),
                    @CacheEvict(value = "order_list", allEntries = true),
                    @CacheEvict(value = "order_amount", allEntries = true)
            }
    )
    public void delete(String no) {
        orderRepository.deleteByOrderNo(no);
    }

    @Cacheable(value = "recommend_staff_list", key = "#orderNo", unless = "#result == null || #result.empty")
    public List<RecommendEmployeeDTO> recommendStaff(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        Integer status = order.getOrderStatus();
        if (status == null || (status != 7 && status != 8)) {
            throw new RuntimeException("当前订单状态不允许分派");
        }

        List<EmployeeDTO> list;
        try {
            ResponseEntity<List<EmployeeDTO>> res = employeeClient.list();
            list = res.hasBody() ? res.getBody() : Collections.emptyList();
        } catch (FeignException e) {
            throw new RuntimeException("员工服务不可用：" + e.status());
        }

        String content = ((order.getServiceType() == null ? "" : order.getServiceType()) + " " +
                (order.getServiceContent() == null ? "" : order.getServiceContent())).trim();
        List<String> keywords = extractKeywords(content);

        List<RecommendEmployeeDTO> out = new ArrayList<>();
        if (list != null) {
            list.forEach(e -> {
                int score = 0;
                StringBuilder reason = new StringBuilder();

                Integer workStatus = e.getWorkStatus();
                if (workStatus != null) {
                    if (workStatus == 1) {
                        score += 10;
                        reason.append("空闲 +10; ");
                    } else if (workStatus == 2) {
                        score += 3;
                        reason.append("忙碌 +3; ");
                    } else {
                        score -= 100;
                        reason.append("休假 -100; ");
                    }
                }

                String skills = e.getSkills() == null ? "" : e.getSkills();
                int hit = 0;
                for (String k : keywords) {
                    if (!k.isBlank() && skills.contains(k)) {
                        hit++;
                    }
                }
                if (hit > 0) {
                    score += hit * 8;
                    reason.append("技能命中 ").append(hit).append(" 个 +").append(hit * 8).append("; ");
                }

                // 若已有未完成调度，降分
                if (e.getEmployeeNo() != null) {
                    try {
                        Employee emp = new Employee();
                        emp.setEmployeeNo(e.getEmployeeNo());
                        boolean busyBySchedule = scheduleRepository.existsByStaffAndScheduleStatusIn(
                                emp,
                                List.of((byte) 1, (byte) 2)
                        );
                        if (busyBySchedule) {
                            score -= 20;
                            reason.append("已有任务 -20; ");
                        }
                    } catch (Exception ignored) {
                    }
                }

                out.add(new RecommendEmployeeDTO(
                        e.getEmployeeNo(),
                        e.getEmployeeName(),
                        e.getWorkStatus(),
                        e.getSkills(),
                        score,
                        reason.toString().trim()
                ));
            });
        }

        return out.stream()
                .sorted(Comparator.comparingInt(RecommendEmployeeDTO::score).reversed())
                .limit(20)
                .toList();
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "order", key = "#req.orderNo"),
            evict = {
                    @CacheEvict(value = "order_list", allEntries = true),
                    @CacheEvict(value = "order_amount", allEntries = true)
            }
    )
    public ScheduleDTO assign(AssignRequest req) {
        if (req == null || req.orderNo() == null || req.orderNo().isBlank()) {
            throw new RuntimeException("订单编号不能为空");
        }
        if (req.employeeNo() == null || req.employeeNo().isBlank()) {
            throw new RuntimeException("员工编号不能为空");
        }

        Order order = orderRepository.findByOrderNo(req.orderNo())
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (order.getOrderStatus() == null || order.getOrderStatus() != 7) {
            throw new RuntimeException("当前订单不在待分派状态");
        }

        ResponseEntity<EmployeeDTO> empRes = employeeClient.getEmployeeByNo(req.employeeNo());
        EmployeeDTO emp = empRes.getBody();
        if (emp == null || emp.getEmployeeNo() == null) {
            throw new RuntimeException("员工不存在");
        }

        //创建调度单
        ServiceSchedule schedule = new ServiceSchedule();
        String no;
        do {
            no = "S" + RandomUUID.generate(19, RandomUUID.CharType.DIGIT);
        } while (scheduleRepository.findByScheduleNo(no).isPresent());

        schedule.setScheduleNo(no);
        schedule.setOrder(order);
        schedule.setStaff(new Employee(emp));
        schedule.setScheduleStatus((byte) 1);//待执行/待接单
        schedule.setScheduleTime(LocalDateTime.now());
        schedule.setRemarks(req.remarks());

        ServiceSchedule saved = scheduleRepository.save(schedule);

        //订单进入「待接单」状态（8）
        order.setOrderStatus(8);
        orderRepository.save(order);

        return new ScheduleDTO(saved);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "order", allEntries = true),
                    @CacheEvict(value = "order_list", allEntries = true),
                    @CacheEvict(value = "order_amount", allEntries = true)
            }
    )
    public ScheduleDTO accept(String scheduleNo) {
        ServiceSchedule schedule = scheduleRepository.findByScheduleNo(scheduleNo)
                .orElseThrow(() -> new RuntimeException("调度单不存在"));

        if (schedule.getScheduleStatus() == null || schedule.getScheduleStatus() != 1) {
            throw new RuntimeException("当前调度单状态不允许接单");
        }

        schedule.setScheduleStatus((byte) 2);
        schedule.setAcceptTime(LocalDateTime.now());
        ServiceSchedule saved = scheduleRepository.save(schedule);

        String orderNo = schedule.getOrder().getOrderNo();
        //更新订单进入服务中
        if (orderNo != null) {
            Order order = orderRepository.findById(orderNo).orElse(null);
            if (order != null) {
                order.setOrderStatus(3);
                orderRepository.save(order);
            }
        }

        String staffNo = schedule.getStaff().getEmployeeNo();
        //服务人员确认接单后才将其置为忙碌（2）
        if (staffNo != null) {
            try {
                employeeClient.updateWorkStatus(staffNo, 2);
            } catch (FeignException ignored) {
                //不阻塞接单主流程
            }
        }

        return new ScheduleDTO(saved);
    }

    public ScheduleDTO getScheduleByOrderNo(String orderNo) {
        if (orderNo == null || orderNo.isBlank()) {
            throw new RuntimeException("订单编号不能为空");
        }
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        return new ScheduleDTO(
                scheduleRepository.findByOrder(order)
                        .orElseThrow(() -> new RuntimeException("不存在该订单的调度信息"))
        );
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "order", allEntries = true),
                    @CacheEvict(value = "order_list", allEntries = true),
                    @CacheEvict(value = "order_amount", allEntries = true)
            }
    )
    public ScheduleDTO complete(String scheduleNo) {
        ServiceSchedule schedule = scheduleRepository.findByScheduleNo(scheduleNo)
                .orElseThrow(() -> new RuntimeException("调度单不存在"));

        Byte status = schedule.getScheduleStatus();
        if (status == null) {
            throw new RuntimeException("当前调度单状态不允许完成");
        }

        //兼容：前端可能未先调用/accept就直接点“完成”，此处将「待接单(1)」自动推进为「执行中(2)」
        //正常流程应为：assign(1)→accept(2)→complete(3)
        if (status == 1) {
            schedule.setScheduleStatus((byte) 2);
            if (schedule.getAcceptTime() == null) {
                schedule.setAcceptTime(LocalDateTime.now());
            }
            scheduleRepository.save(schedule);

            Order order = schedule.getOrder();
            //同步订单进入服务中（3）
            if (order != null) {
                order.setOrderStatus(3);
                orderRepository.save(order);
            }

            Employee staff = schedule.getStaff();
            //服务人员确认接单后才将其置为忙碌（2）
            if (staff != null) {
                try {
                    employeeClient.updateWorkStatus(staff.getEmployeeNo(), 2);
                } catch (FeignException ignored) {
                }
            }

            status = 2;
        }

        if (status != 2) {
            throw new RuntimeException("当前调度单状态不允许完成");
        }

        schedule.setScheduleStatus((byte) 3);
        schedule.setCompleteTime(LocalDateTime.now());
        ServiceSchedule saved = scheduleRepository.save(schedule);

        Order order = schedule.getOrder();
        //订单进入「已完成」
        if (order != null) {
            order.setOrderStatus(4);
            orderRepository.save(order);
        }

        Employee staff = schedule.getStaff();
        //员工回到空闲（1）
        if (staff != null) {
            try {
                employeeClient.updateWorkStatus(staff.getEmployeeNo(), 1);
            } catch (FeignException ignored) {
            }
        }

        return new ScheduleDTO(saved);
    }

    private static List<String> extractKeywords(String text) {
        if (text == null || text.isBlank()) return List.of();
        String norm = text.replaceAll("[\\p{Punct}\\s]+", " ").trim();
        if (norm.isBlank()) return List.of();
        String[] parts = norm.split("\\s+");
        HashSet<String> set = new HashSet<>();
        for (String p : parts) {
            if (p == null) continue;
            String s = p.trim();
            if (s.length() < 2) continue;
            set.add(s);
        }
        return new ArrayList<>(set);
    }

}
