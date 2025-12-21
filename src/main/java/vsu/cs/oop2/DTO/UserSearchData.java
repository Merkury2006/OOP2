package vsu.cs.oop2.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserSearchData {
    private List<UserData> users;
    private long totalUsers;
    private long verifiedUsers;
    private Long currentUserId;
}
