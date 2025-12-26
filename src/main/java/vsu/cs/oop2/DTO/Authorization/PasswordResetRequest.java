package vsu.cs.oop2.DTO.Authorization;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
/**
 * DTO для запроса восстановления пароля.
 * Используется при инициации процесса сброса пароля по email.
 *
 * Содержит минимальный набор данных, необходимый для отправки
 * инструкций по восстановлению пароля на указанный email.
 *
 * @see vsu.cs.oop2.Controllers.PasswordResetController#forgotPassword
 */
@Data
public class PasswordResetRequest {
    /**
     * Email адрес пользователя, для которого требуется восстановить пароль.
     * Используется для:
     * - Поиска пользователя в системе
     * - Отправки письма с инструкциями по восстановлению
     * - Генерации и отправки токена для сброса пароля
     *
     * Валидационные требования:
     * - Не может быть пустым или состоять только из пробелов
     * - Должен соответствовать формату email адреса
     * @see vsu.cs.oop2.Services.PasswordResetService#sendPasswordResetEmail(String)
     */
    @NotBlank(message ="Email обязателен")
    @Email(message = "Некорретный формат email")
    private String email;
}
