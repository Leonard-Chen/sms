package com.gdut.sms.order.repository;

import com.gdut.sms.common.entity.Order;
import com.gdut.sms.common.entity.Employee;
import com.gdut.sms.common.entity.ServiceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Collection;

public interface ScheduleRepository extends JpaRepository<ServiceSchedule, String> {

    Optional<ServiceSchedule> findByScheduleNo(String scheduleNo);

    boolean existsByStaffAndScheduleStatusIn(Employee staff, Collection<Byte> scheduleStatus);

    Optional<ServiceSchedule> findByOrder(Order order);
}
