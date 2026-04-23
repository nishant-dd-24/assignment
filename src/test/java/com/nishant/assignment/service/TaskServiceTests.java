package com.nishant.assignment.service;

import com.nishant.assignment.dto.TaskRequest;
import com.nishant.assignment.entity.*;
import com.nishant.assignment.exception.ExceptionUtil;
import com.nishant.assignment.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TaskServiceTests {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ExceptionUtil exceptionUtil;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getAllTasks_adminShouldReturnAllTasks() {
        User admin = User.builder().id(1L).email("admin@mail.com").role(Role.ADMIN).build();
        User owner1 = User.builder().id(2L).name("Owner One").build();
        User owner2 = User.builder().id(3L).name("Owner Two").build();
        Task task1 = Task.builder().id(11L).owner(owner1).build();
        Task task2 = Task.builder().id(12L).owner(owner2).build();

        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(taskRepository.findAll()).thenReturn(List.of(task1, task2));

        var result = taskService.getAllTasks(admin.getEmail());

        assertEquals(2, result.size());
        verify(taskRepository).findAll();
    }

    @Test
    void getAllTasks_userShouldReturnOwnTasks() {
        User user = User.builder().id(2L).email("user@mail.com").role(Role.USER).build();
        User owner = User.builder().id(2L).name("User Owner").build();
        Task task = Task.builder().id(21L).owner(owner).build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(taskRepository.findByOwner(user)).thenReturn(List.of(task));

        var result = taskService.getAllTasks(user.getEmail());

        assertEquals(1, result.size());
        verify(taskRepository).findByOwner(user);
    }

    @Test
    void getTask_shouldDenyAccessForDifferentUser() {
        User user = User.builder().id(1L).email("user@mail.com").role(Role.USER).build();
        User owner = User.builder().id(2L).build();

        Task task = Task.builder().id(10L).owner(owner).build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(exceptionUtil.accessDenied(anyString())).thenThrow(new RuntimeException("denied"));

        assertThrows(RuntimeException.class,
                () -> taskService.getTask(10L, user.getEmail()));
    }

    @Test
    void createTask_shouldAssignOwnerAndDefaultStatus() {
        User user = User.builder().id(1L).email("user@mail.com").role(Role.USER).build();

        TaskRequest req = new TaskRequest("Test", "desc", null);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        var res = taskService.createTask(req, user.getEmail());

        assertEquals("Test", res.title());
        assertEquals(TaskStatus.TODO, res.status());
    }

    @Test
    void updateTask_shouldDenyIfNotOwnerOrAdmin() {
        User user = User.builder().id(1L).email("user@mail.com").role(Role.USER).build();
        User owner = User.builder().id(2L).build();

        Task task = Task.builder().id(10L).owner(owner).build();

        TaskRequest req = new TaskRequest("New", "desc", TaskStatus.DONE);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(exceptionUtil.accessDenied(anyString())).thenThrow(new RuntimeException("denied"));

        assertThrows(RuntimeException.class,
                () -> taskService.updateTask(10L, req, user.getEmail()));
    }


    @Test
    void deleteTask_adminCanDeleteAnyTask() {
        User admin = User.builder().id(1L).email("admin@mail.com").role(Role.ADMIN).build();
        Task task = Task.builder().id(10L).owner(new User()).build();

        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        taskService.deleteTask(10L, admin.getEmail());

        verify(taskRepository).delete(task);
    }


}