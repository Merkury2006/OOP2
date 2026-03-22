package vsu.cs.oop2.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * КОНФИГУРАЦИЯ БЕЗОПАСНОСТИ SPRING SECURITY
 *
 * Основные функции:
 * 1. Настройка правил доступа к URL
 * 2. Конфигурация формы входа/выхода
 * 3. Настройка обработки ошибок аутентификации
 * 4. Определение кодировщика паролей
 *
 * @Configuration Помечает класс как источник конфигурационных бинов
 * @EnableWebSecurity Активирует настройку веб-безопасности
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {
    /**
     * КОДИРОВЩИК ПАРОЛЕЙ
     *
     * Использует алгоритм BCrypt для хеширования паролей.
     * Применяется при:
     * - Регистрации новых пользователей
     * - Проверке пароля при входе
     * - Смене пароля
     *
     * @return BCryptPasswordEncoder для безопасного хранения паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ОСНОВНАЯ ЦЕПОЧКА ФИЛЬТРОВ БЕЗОПАСНОСТИ
     *
     * Определяет политику безопасности для всех HTTP-запросов:
     * - Разграничение доступа по URL
     * - Настройка формы аутентификации
     * - Конфигурация выхода из системы
     * - Защита от CSRF-атак
     *
     * Правила доступа:
     * - /admin/** : только для пользователей с ролью ADMIN
     * - Все остальные URL : доступны без аутентификации
     *
     * @param http Объект для настройки веб-безопасности
     * @return Настроенная цепочка фильтров безопасности
     * @throws Exception При ошибках конфигурации
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
                authorizeHttpRequests(auth -> auth
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().permitAll()
                ).
                formLogin(form -> form
                    .loginPage("/login")
                    .defaultSuccessUrl("/")
                    .usernameParameter("username")
                    .failureHandler(authenticationFailureHandler())
                    .permitAll()
                ).
                logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                    .permitAll()
                ).
                csrf(Customizer.withDefaults());

        return http.build();
    }


    /**
     * ОБРАБОТЧИК ОШИБОК АУТЕНТИФИКАЦИИ
     *
     * Кастомный обработчик для перенаправления пользователя
     * на страницу входа с соответствующим сообщением об ошибке.
     *
     * Обрабатываемые ошибки:
     * - Неподтвержденный email (DisabledException)
     * - Неверные учетные данные (BadCredentialsException)
     * - Пользователь не найден (UsernameNotFoundException)
     *
     * @return AuthenticationFailureHandler с логикой обработки ошибок
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return ((request, response, exception) -> {
            String email = request.getParameter("username");
            String redirectUrl = "/login?error";

            Throwable realCause = getRootCause(exception);


            if (realCause.getMessage().contains("Email не подтвержден") || exception instanceof DisabledException) {
                log.warn("Unverified email attempt: {}", email);
                redirectUrl = "/login?error=notVerified&email=" + URLEncoder.encode(email, StandardCharsets.UTF_8);

            } else if (exception instanceof BadCredentialsException || exception instanceof UsernameNotFoundException) {
                log.warn("Bad credentials for email: {}", email);
                redirectUrl = "/login?error=badCredentials";
            }
            response.sendRedirect(redirectUrl);
        });
    }


    /**
     * ПОИСК КОРНЕВОЙ ПРИЧИНЫ ИСКЛЮЧЕНИЯ
     *
     * Вспомогательный метод для получения исходной причины исключения.
     * Позволяет обрабатывать вложенные исключения, которые могут быть
     * обернуты в другие исключения Spring Security.
     *
     * @param throwable Исходное исключение
     * @return Корневое исключение (самая глубокая причина)
     */
    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while(cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }
}