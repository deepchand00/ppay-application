package in.co.ppay.user_service.model;

import lombok.Data;

@Data
public class PasswordModel {
    private String mobile;
    private String oldPassword;
    private String newPassword;
    private String otp;
}
