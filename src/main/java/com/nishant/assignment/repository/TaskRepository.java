// TaskRepository.java
package com.nishant.assignment.repository;

import com.nishant.assignment.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwner(User owner);
}