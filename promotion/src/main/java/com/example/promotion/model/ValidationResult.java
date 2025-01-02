package com.example.promotion.model;

import lombok.Data;

import java.util.List;

@Data
public class ValidationResult {
    private boolean valid;
    private List<String> errors;
}
