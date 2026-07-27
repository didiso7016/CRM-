package com.crm.service;

import com.crm.dto.TaskForm;
import com.crm.entity.Task;
import com.crm.repository.CustomerRepository;
import com.crm.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 待辦事項商業邏輯。
 */
@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final CustomerRepository customerRepository;

    public TaskService(TaskRepository taskRepository, CustomerRepository customerRepository) {
        this.taskRepository = taskRepository;
        this.customerRepository = customerRepository;
    }

    public Task create(TaskForm form) {
        Task t = new Task();
        t.setTitle(form.getTitle().trim());
        t.setDueDate(form.getDueDate());
        t.setNotes(form.getNotes());
        if (form.getCustomerId() != null) {
            customerRepository.findById(form.getCustomerId()).ifPresent(t::setCustomer);
        }
        return taskRepository.save(t);
    }

    /** 切換完成/未完成 */
    public void toggleDone(Long id) {
        Task t = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到待辦事項"));
        t.setDone(!t.isDone());
        taskRepository.save(t);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Task> listAll() {
        return taskRepository.findAllOrdered();
    }

    @Transactional(readOnly = true)
    public List<Task> listOpen() {
        return taskRepository.findOpen();
    }

    @Transactional(readOnly = true)
    public List<Task> byCustomer(Long customerId) {
        return taskRepository.findByCustomer(customerId);
    }

    @Transactional(readOnly = true)
    public long countOverdue() {
        return taskRepository.countOverdue(LocalDate.now());
    }
}
