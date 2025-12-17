package vsu.cs.oop2.DTO;

import lombok.Builder;
import lombok.Data;

import java.security.Principal;


/**
 * ДАННЫЕ ОТВЕТА ДЛЯ ОПЕРАЦИИ ЛАЙКА/ДИЗЛАЙКА
 *
 * Используется в ApiResponse<LikeData> для эндпоинта /api/like/{trackId}
 * Содержит результат операции переключения лайка.
 *
 * Поля:
 * - liked: Текущее состояние после операции (true = лайк установлен)
 * - trackId: ID трека, для которого выполнена операция
 * - userId: ID пользователя, выполнившего операцию
 *
 * @see vsu.cs.oop2.Controllers.API#toggleLike(Long, Principal)
 */
@Data
@Builder
public class LikeData {
    private boolean liked;
    private Long trackId;
    private Long userId;
}
