package com.gdut.sms.common.dto;

/**
 * @author ly
 */

public record RecommendEmployeeDTO(String employeeNo, String employeeName, Integer workStatus, String skills,
                                   Integer score, String reason) {
}

