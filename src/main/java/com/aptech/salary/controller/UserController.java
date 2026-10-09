package com.aptech.salary.controller;

import com.aptech.salary.model.User;
import com.aptech.salary.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // ===== READ + SEARCH =====
    @GetMapping("/")
    public String index(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("user", new User());
        return populate(model, keyword);
    }

    // ===== CREATE =====
    @PostMapping("/users/add")
    public String add(@Valid @ModelAttribute("user") User user,
                      BindingResult result,
                      Model model,
                      RedirectAttributes redirect) {
        user.setId(null);

        if (result.hasErrors()) {
            return populate(model, null);
        }
        if (service.nameExists(user.getName(), null)) {
            model.addAttribute("error", "Error while creating User: Unable to create. "
                    + "A User with name " + user.getName().trim() + " already exist.");
            return populate(model, null);
        }

        service.save(user);
        redirect.addFlashAttribute("success", "User created successfully");
        return "redirect:/";
    }

    // ===== EDIT (nạp dữ liệu vào form) =====
    @GetMapping("/users/edit/{id}")
    public String edit(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<User> user = service.findById(id);
        if (user.isEmpty()) {
            redirect.addFlashAttribute("error", "Error: User with id " + id + " not found.");
            return "redirect:/";
        }
        model.addAttribute("user", user.get());
        return populate(model, null);
    }

    // ===== UPDATE =====
    @PostMapping("/users/update")
    public String update(@Valid @ModelAttribute("user") User user,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return populate(model, null);
        }
        if (user.getId() == null || service.findById(user.getId()).isEmpty()) {
            redirect.addFlashAttribute("error", "Error while updating User: User not found.");
            return "redirect:/";
        }
        if (service.nameExists(user.getName(), user.getId())) {
            model.addAttribute("error", "Error while updating User: Unable to update. "
                    + "A User with name " + user.getName().trim() + " already exist.");
            return populate(model, null);
        }

        service.save(user);
        redirect.addFlashAttribute("success", "User updated successfully");
        return "redirect:/";
    }

    // ===== DELETE =====
    @PostMapping("/users/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        if (service.findById(id).isPresent()) {
            service.delete(id);
            redirect.addFlashAttribute("success", "User deleted successfully");
        } else {
            redirect.addFlashAttribute("error", "Error while deleting User: User not found.");
        }
        return "redirect:/";
    }

    private String populate(Model model, String keyword) {
        model.addAttribute("users", service.search(keyword));
        model.addAttribute("keyword", keyword);
        return "index";
    }
}
