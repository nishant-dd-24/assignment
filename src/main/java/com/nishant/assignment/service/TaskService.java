package com.nishant.assignment.service;

import com.nishant.assignment.dto.*;
import com.nishant.assignment.entity.*;
import com.nishant.assignment.exception.ExceptionUtil;
import com.nishant.assignment.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ExceptionUtil exceptionUtil;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> exceptionUtil.notFound("User not found"));
    }

    public List<TaskResponse> getAllTasks(String email) {
        User user = getUser(email);
        List<Task> tasks = user.getRole() == Role.ADMIN
                ? taskRepository.findAll()
                : taskRepository.findByOwner(user);
        return tasks.stream().map(TaskResponse::from).toList();
    }

    public TaskResponse getTask(Long id, String email) {
        User user = getUser(email);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> exceptionUtil.notFound("Task not found"));
        if (user.getRole() != Role.ADMIN && !task.getOwner().getId().equals(user.getId()))
            throw exceptionUtil.accessDenied("Access denied");
        return TaskResponse.from(task);
    }

    public TaskResponse createTask(TaskRequest req, String email) {
        User user = getUser(email);
        Task task = Task.builder()
                .title(req.title())
                .description(req.description())
                .status(req.status() != null ? req.status() : TaskStatus.TODO)
                .owner(user)
                .build();
        return TaskResponse.from(taskRepository.save(task));
    }

    public TaskResponse updateTask(Long id, TaskRequest req, String email) {
        User user = getUser(email);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> exceptionUtil.notFound("Task not found"));
        if (user.getRole() != Role.ADMIN && !task.getOwner().getId().equals(user.getId()))
            throw exceptionUtil.accessDenied("Access denied");
        task.setTitle(req.title());
        task.setDescription(req.description());
        if (req.status() != null) task.setStatus(req.status());
        return TaskResponse.from(taskRepository.save(task));
    }

    public void deleteTask(Long id, String email) {
        User user = getUser(email);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> exceptionUtil.notFound("Task not found"));
        if (user.getRole() != Role.ADMIN && !task.getOwner().getId().equals(user.getId()))
            throw exceptionUtil.accessDenied("Access denied");
        taskRepository.delete(task);
    }
}