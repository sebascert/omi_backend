package com.example.omi.feature;

public record FeatureDto(
    Long id, String title, String description, Long sprintId, String priority, String status) {}
