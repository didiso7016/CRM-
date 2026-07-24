package com.crm.service;

/**
 * 查無資料時拋出,由全域例外處理轉為友善訊息。
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
