package vsu.cs.oop2.DTO;

import lombok.Builder;
import lombok.Data;

import java.security.Principal;


/**
 * Data Transfer Object (DTO) для передачи результата операции лайка/дизлайка трека.
 * Используется в ответах API при переключении состояния "лайк" для трека.
 *
 * Содержит информацию о текущем состоянии лайка после выполнения операции
 * и идентификаторы связанных сущностей для логирования и отладки.
 *
 * @see vsu.cs.oop2.Controllers.API#toggleLike(Long, Principal)
 * @see vsu.cs.oop2.Entity.Like
 * @see vsu.cs.oop2.Services.LikeService
 */
@Data
@Builder
public class LikeData {
    /**
     * Текущее состояние лайка после выполнения операции.
     * - {@code true}: лайк установлен (пользователь лайкнул трек)
     * - {@code false}: лайк снят (пользователь убрал лайк или дизлайкнул)
     */
    private boolean liked;

    /**
     * Уникальный идентификатор трека, для которого выполнена операция.
     * Используется для:
     * - Связи с сущностью Track
     * - Валидации существования трека
     * - Обновления счетчика лайков трека
     */
    private Long trackId;

    /**
     * Уникальный идентификатор пользователя, выполнившего операцию.
     * Используется для:
     * - Связи с сущностью User
     */
    private Long userId;
}
