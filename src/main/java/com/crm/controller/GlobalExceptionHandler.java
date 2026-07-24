package com.crm.controller;

import com.crm.service.ResourceNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全域例外處理:將常見錯誤轉為友善的錯誤畫面,不外洩堆疊細節。
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorTitle", "找不到資料");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/custom";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBadRequest(IllegalArgumentException ex, Model model) {
        model.addAttribute("errorTitle", "操作無效");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/custom";
    }
}
