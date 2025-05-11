package id.ac.ui.cs.advprog.berating.dto;

import id.ac.ui.cs.advprog.berating.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class UserDTO {
    private String id;
    private String fullname;
    private String email;
    private String gender;
    private String username;
    private String password;
    private Role role;
}
