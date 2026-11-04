package com.neuedu.HospitalBackend.dto;

public record AccountProfileRequest(
        String displayName, String jobTitle, String department,
        String phone, String email, Boolean emailNotifications
) {
}
