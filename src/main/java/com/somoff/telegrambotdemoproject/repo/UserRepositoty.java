package com.somoff.telegrambotdemoproject.repo;

import com.somoff.telegrambotdemoproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositoty extends JpaRepository<User, Long> {
}
