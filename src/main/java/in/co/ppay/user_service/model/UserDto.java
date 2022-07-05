package in.co.ppay.user_service.model;

import lombok.Data;

@Data
public class UserDto {
    private String firstName;
    private String lastName;
    private String mobile;
    private String role;
    private String merchantType;
    private double balance;
}
