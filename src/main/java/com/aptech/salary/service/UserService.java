package com.aptech.salary.service;

import com.aptech.salary.model.User;
import com.aptech.salary.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /** Search theo tên; keyword rỗng thì trả về tất cả. */
    public List<User> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return repository.findAllByOrderByIdAsc();
        }
        return repository.findByNameContainingIgnoreCaseOrderByIdAsc(keyword.trim());
    }

    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    /** Kiểm tra trùng tên (bỏ qua chính bản ghi đang sửa nếu có id). */
    public boolean nameExists(String name, Long excludeId) {
        if (name == null) {
            return false;
        }
        return repository.findByNameIgnoreCase(name.trim())
                .map(found -> excludeId == null || !found.getId().equals(excludeId))
                .orElse(false);
    }

    public User save(User user) {
        user.setName(user.getName().trim());
        return repository.save(user);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
