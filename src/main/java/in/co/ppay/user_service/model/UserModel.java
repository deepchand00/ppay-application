package in.co.ppay.user_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    private String firstName;
    private String lastName;
    private String mobile;
    private String password;
    private String matchingPassword;
    private String role;
    private String merchantType;
}
