package vsu.cs.oop2.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vsu.cs.oop2.DTO.ApiResponse;
import vsu.cs.oop2.DTO.UserData;
import vsu.cs.oop2.DTO.UserSearchData;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Services.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/admin")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAPIController {
    private final UserService userService;


    @GetMapping("/users/search")
    public ApiResponse<UserSearchData> searchUsers(@RequestParam(required = false) String search, Principal principal) {

        User admin = userService.getUserByEmail(principal.getName());

        List<User> users;
        if (search != null && !search.trim().isEmpty()) {
            users = userService.searchUsers(search);
        } else {
            users = userService.getAllUsers();
        }

        List<UserData> userData = users.stream()
                .map(user -> UserData.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .emailVerified(user.isEmailVerified())
                        .username(user.getUsername())
                        .build())
                .toList();

        UserSearchData searchData = UserSearchData.builder()
                .users(userData)
                .totalUsers(userService.countAllUsers())
                .verifiedUsers(userService.countVerifiedUsers())
                .currentUserId(admin.getId())
                .build();

        log.info("ADMIN USER SEARCH - user: {}, search: '{}', results: {}",
                admin.getId(), search, users.size());

        return ApiResponse.success(searchData);
    }

    @PostMapping("/users/{id}/role")
    public ApiResponse<Void> changeUserRole(@PathVariable Long id, @RequestParam String newRole,
                                            Principal principal) {
        User admin  = userService.getUserByEmail(principal.getName());

        userService.changeUserRole(id, newRole, admin.getId());

        log.info("USER ROLE HAS BEEN CHANGED - user: {}", id);

        return ApiResponse.success("Роль успешно изменена");
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id, Principal principal) {
        User admin = userService.getUserByEmail(principal.getName());

        userService.deleteUser(id, admin.getId());

        log.info("USER HAS BEEN DELETED - user: {}", id);

        return ApiResponse.success("Пользователь успешно удален");
    }

}
