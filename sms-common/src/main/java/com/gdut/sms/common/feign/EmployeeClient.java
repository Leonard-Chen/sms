package com.gdut.sms.common.feign;

import com.gdut.sms.common.dto.DeptDTO;
import com.gdut.sms.common.dto.EmployeeDTO;
import com.gdut.sms.common.config.FeignConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "sms-employee", path = "/employee", configuration = FeignConfig.class)
public interface EmployeeClient {
    @GetMapping("/")
    ResponseEntity<List<EmployeeDTO>> list();

    @GetMapping("/{no}")
    ResponseEntity<EmployeeDTO> getEmployeeByNo(@PathVariable String no);

    @GetMapping("/dept/{no}")
    ResponseEntity<DeptDTO> getDeptByNo(@PathVariable String no);

    @PutMapping("/{no}")
    ResponseEntity<EmployeeDTO> updateWorkStatus(@PathVariable String no,
                                                 @RequestParam Integer status);
}
