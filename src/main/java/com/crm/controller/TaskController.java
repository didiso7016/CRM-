package com.crm.controller;

import com.crm.dto.TaskForm;
import com.crm.service.CustomerService;
import com.crm.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 待辦事項控制器。
 */
@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final CustomerService customerService;

    public TaskController(TaskService taskService, CustomerService customerService) {
        this.taskService = taskService;
        this.customerService = customerService;
    }

    /** 待辦列表(show=open 只看未完成、all 全部) */
    @GetMapping
    public String list(@RequestParam(defaultValue = "open") String show, Model model) {
        model.addAttribute("activeMenu", "tasks");
        model.addAttribute("show", show);
        model.addAttribute("tasks", "all".equals(show) ? taskService.listAll() : taskService.listOpen());
        model.addAttribute("activeCustomers", customerService.listActiveForSelect());
        if (!model.containsAttribute("taskForm")) {
            model.addAttribute("taskForm", new TaskForm());
        }
        return "tasks/list";
    }

    /** 新增待辦 */
    @PostMapping
    public String create(@Valid @ModelAttribute("taskForm") TaskForm form,
                         BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("flashError", "請填寫事項內容");
            return "redirect:/tasks";
        }
        taskService.create(form);
        ra.addFlashAttribute("flashSuccess", "已新增待辦事項");
        return "redirect:/tasks";
    }

    /** 切換完成狀態 */
    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id,
                         @RequestParam(required = false) String redirect, RedirectAttributes ra) {
        taskService.toggleDone(id);
        return "redirect:" + (redirect != null && !redirect.isBlank() ? redirect : "/tasks");
    }

    /** 刪除待辦 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        taskService.delete(id);
        ra.addFlashAttribute("flashSuccess", "已刪除待辦事項");
        return "redirect:/tasks";
    }
}
