package com.example.omi.user;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserRepository repo;

  public UserController(UserRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<UserDto> getAll() {
    return repo.findAll();
  }

  @PostMapping
  public void create(@Valid @RequestBody CreateUserRequest req) {
    if (!repo.roleExists(req.getRoleId())) {
      throw new IllegalArgumentException("Role " + req.getRoleId() + " does not exist");
    }

    if (req.getManagerId() != null && !repo.userExists(req.getManagerId())) {
      throw new IllegalArgumentException("Manager " + req.getManagerId() + " does not exist");
    }

    if (repo.emailExists(req.getEmail())) {
      throw new IllegalArgumentException("Email is already registered");
    }

    repo.create(req);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable Long userId) {
    repo.delete(userId);
    return ResponseEntity.noContent().build();
  }
}
