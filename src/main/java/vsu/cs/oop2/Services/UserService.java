package vsu.cs.oop2.Services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vsu.cs.oop2.DTO.UserLoginDTO;
import vsu.cs.oop2.DTO.UserRegistrationDTO;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Repository.UserRepository;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    public User registerUser(UserRegistrationDTO userDTO) {
        if (userRepository.getUserByEmail(userDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Электронная почта уже существует");
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        return userRepository.save(user);
    }

    public User loginUser(UserLoginDTO userDTO) {
        Optional<User> user = userRepository.getUserByEmail(userDTO.getEmail());

        if (user.isEmpty()) {
            throw new RuntimeException("Пользователя с таким email не существует");
        }

        if (!passwordEncoder.matches(userDTO.getPassword(), user.get().getPassword())) {
            throw new RuntimeException("Неверный пароль");
        }

        return user.get();
    }
}
