package vsu.cs.oop2.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import vsu.cs.oop2.Exceptions.UserNotFoundException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * КОНФИГУРАЦИЯ БЕЗОПАСНОСТИ ПРИЛОЖЕНИЯ
 * Основной класс конфигурации Spring Security, определяющий:
 * 1. Правила доступа к HTTP-эндпоинтам
 * 2. Механизм аутентификации (форма входа)
 * 3. Механизм выхода (logout)
 * 4. Защиту от CSRF-атак
 * 5. Кодировщик паролей
 * @Configuration - помечает класс как источник конфигурационных бинов Spring
 * @EnableWebSecurity - активирует настройку веб-безопасности Spring Security
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {
    /**
     * БИН ДЛЯ КОДИРОВАНИЯ ПАРОЛЕЙ
     * Создает и регистрирует компонент PasswordEncoder, который используется для:
     * - Хеширования паролей при регистрации пользователей
     * - Сравнения введенного пароля с хешем из базы данных при аутентификации
     * @return Экземпляр BCryptPasswordEncoder для хеширования паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ОСНОВНАЯ ЦЕПОЧКА ФИЛЬТРОВ БЕЗОПАСНОСТИ
     * Конфигурирует поведение безопасности для HTTP-запросов.
     * Определяет, какие URL доступны анонимно, а какие требуют аутентификации,
     * настраивает форму входа и выхода, защиту от CSRF.
     * ТЕКУЩАЯ КОНФИГУРАЦИЯ:
     * - Все запросы разрешены без аутентификации (anyRequest().permitAll()), так как отрабатывают темплейты
     * - Включена кастомная форма входа (/login)
     * - Настроен механизм выхода (/logout)
     * - Включена защита от CSRF-атак с настройками по умолчанию
     * @param http Объект для настройки веб-безопасности
     * @return Сконфигурированная цепочка фильтров SecurityFilterChain
     * @throws Exception При ошибках конфигурации
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
                authorizeHttpRequests(auth ->
                    auth.anyRequest().permitAll()
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

    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while(cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }
}