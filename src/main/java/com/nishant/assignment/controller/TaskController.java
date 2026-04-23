package com.nishant.assignment.controller;

import com.nishant.assignment.dto.*;
import com.nishant.assignment.exception.response.ErrorResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.nishant.assignment.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Tasks", description = "Task management endpoints with role-based authorization for ADMIN and USER accounts.")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(
            summary = "Get all accessible tasks",
            description = "Returns tasks based on caller role: ADMIN receives all tasks, while USER receives only tasks they own."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks fetched successfully", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Authenticated user not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TaskResponse>> getAll(@AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.ok(taskService.getAllTasks(u.getUsername()));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get task by ID",
            description = "Fetches task details by ID. USER can access only their own tasks; ADMIN can access any task."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task fetched successfully", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TaskResponse> getOne(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.ok(taskService.getTask(id, u.getUsername()));
    }

    @PostMapping
    @Operation(
            summary = "Create a new task",
            description = "Creates a task owned by the currently authenticated user. If status is omitted, default status TODO is applied."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created successfully", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Authenticated user not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest req,
                                               @AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(req, u.getUsername()));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a task",
            description = "Updates an existing task. Only the task owner or an ADMIN can update task details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated successfully", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TaskResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody TaskRequest req,
                                               @AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.ok(taskService.updateTask(id, req, u.getUsername()));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a task",
            description = "Deletes a task by ID. Only the task owner or an ADMIN can delete the task."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails u) {
        taskService.deleteTask(id, u.getUsername());
        return ResponseEntity.noContent().build();
    }
}