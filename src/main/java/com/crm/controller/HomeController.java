package com.crm.controller;

import com.crm.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首頁 Dashboard 控制器。
 */
@Controller
public class HomeController {

    private final DashboardService dashboardService;

    public HomeController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("activeMenu", "home");
        model.addAttribute("stats", dashboardService.load());
        return "index";
    }
}
