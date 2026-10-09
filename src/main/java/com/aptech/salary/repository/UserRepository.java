package com.aptech.salary.repository;

import com.aptech.salary.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNameIgnoreCase(String name);

    List<User> findByNameContainingIgnoreCaseOrderByIdAsc(String keyword);

    List<User> findAllByOrderByIdAsc();
}
