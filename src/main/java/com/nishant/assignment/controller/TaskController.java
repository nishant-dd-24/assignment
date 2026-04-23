package com.nishant.assignment.controller;

import com.nishant.assignment.dto.*;
import com.nishant.assignment.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "Get all tasks (admin=all, user=own)")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TaskResponse>> getAll(@AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.ok(taskService.getAllTasks(u.getUsername()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TaskResponse> getOne(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.ok(taskService.getTask(id, u.getUsername()));
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest req,
                                               @AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(req, u.getUsername()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TaskResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody TaskRequest req,
                                               @AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.ok(taskService.updateTask(id, req, u.getUsername()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails u) {
        taskService.deleteTask(id, u.getUsername());
        return ResponseEntity.noContent().build();
    }
}