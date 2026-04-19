package com.gdut.sms.system.repository;

import com.gdut.sms.common.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationLogRepository extends JpaRepository<OperationLog, String> {
}
