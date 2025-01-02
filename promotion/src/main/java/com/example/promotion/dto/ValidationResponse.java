 package com.example.promotion.dto;

import com.example.promotion.enums.ValidationErrorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

 @Data
@NoArgsConstructor
 @AllArgsConstructor
public class ValidationResponse {
    private String code;
    private boolean isValid;
    private String message;
    private double discountAmount;
    private ValidationErrorType errorType;


    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }


    public void setCode(String code) {
        this.code = code;
    }



    public void setMessage(String message) {
        this.message = message;
    }


    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }



    public void setErrorType(ValidationErrorType errorType) {
        this.errorType = errorType;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }
}