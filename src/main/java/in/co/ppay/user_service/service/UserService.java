package in.co.ppay.user_service.service;

import in.co.ppay.user_service.entity.User;
import in.co.ppay.user_service.model.UserDto;
import in.co.ppay.user_service.model.UserModel;
import in.co.ppay.wallet_service.entity.Wallet;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(UserModel userModel);
    User findUserByMobile(String mobile);
    void changePassword(User user, String newPassword);

    boolean checkIfValidOldPassword(User user, String oldPassword);

    boolean checkMatchingPassword(UserModel userModel);

    void saveVerificationOtpForUser(String otp, User user);

    String validateVerificationOtp(String otp);

    void generateNewVerificationOtp(User user);

    void createPasswordResetOtpForUser(User user, String otp);

    String validatePasswordResetOtp(String otp);

    Optional<User> getUserByPasswordResetOtp(String otp);

    boolean checkUserAvailability(String mobile);

    List<User> getAllUsers();

    UserDto getUserDto(User user, Wallet wallet);
}
