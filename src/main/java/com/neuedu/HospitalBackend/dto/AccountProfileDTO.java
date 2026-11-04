package com.neuedu.HospitalBackend.dto;

public record AccountProfileDTO (
        Long id, String displayName, String jobTitle, String department,
        String phone, String email, Boolean emailNotifications
){
}
