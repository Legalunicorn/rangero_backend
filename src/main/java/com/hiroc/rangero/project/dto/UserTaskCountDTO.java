package com.hiroc.rangero.project.dto;


import com.hiroc.rangero.user.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class UserTaskCountDTO {
    private UserDTO user;
    private long assigned;

    public UserTaskCountDTO(long userId, String email, String username, long count){
        user = new UserDTO(userId,email,username);
        assigned = count;
    }
}
