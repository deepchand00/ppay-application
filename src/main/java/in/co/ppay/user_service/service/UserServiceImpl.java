package in.co.ppay.user_service.service;

import in.co.ppay.user_service.entity.PasswordResetOtp;
import in.co.ppay.user_service.entity.User;
import in.co.ppay.user_service.model.UserDto;
import in.co.ppay.user_service.model.UserModel;
import in.co.ppay.user_service.repository.PasswordResetOtpRepository;
import in.co.ppay.user_service.repository.UserRepository;
import in.co.ppay.user_service.entity.VerificationOtp;
import in.co.ppay.user_service.event.ResendVerificationOtpEvent;
import in.co.ppay.user_service.repository.VerificationOtpRepository;
import in.co.ppay.wallet_service.entity.Wallet;
import in.co.ppay.wallet_service.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private WalletService walletService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationOtpRepository verificationOtpRepository;

    @Autowired
    private PasswordResetOtpRepository passwordResetOtpRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    @Autowired
    private ApplicationEventPublisher publisher;


    @Override
    public User registerUser(UserModel userModel) {
        User user = new User();
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setMobile(userModel.getMobile());
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        user.setRole(userModel.getRole());
        if(userModel.getRole().equalsIgnoreCase("USER"))
           user.setMerchantType("TRANSFER");
        else {
            if(userModel.getRole().equalsIgnoreCase("MERCHANT") &&
                    (userModel.getMerchantType() == null || userModel.getMerchantType().isEmpty()))
                user.setMerchantType("OTH");
            else
                user.setMerchantType(userModel.getMerchantType());
        }

        userRepository.save(user);
        return user;
    }

    @Override
    public User findUserByMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }


    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword((passwordEncoder.encode(newPassword)));
        userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public boolean checkMatchingPassword(UserModel userModel) {
        return userModel.getPassword().equals(userModel.getMatchingPassword());
    }

    @Override
    public void saveVerificationOtpForUser(String otp, User user) {
        VerificationOtp verificationOtp= new VerificationOtp(user, otp);
        verificationOtpRepository.save(verificationOtp);
    }

    @Override
    public String validateVerificationOtp(String otp) {
        VerificationOtp verificationOtp = verificationOtpRepository.findByOtp(otp);

        // Checking verification otp exists in database or not
        // verification otp does not exist
        if(verificationOtp == null)
            return "invalid";

        // verification otp exist, checking for expiration time
        User user = verificationOtp.getUser();
        Calendar calender = Calendar.getInstance();

        // if token expired
        if(verificationOtp.getExpirationTime().getTime() - calender.getTime().getTime() <= 0) {
            verificationOtpRepository.delete(verificationOtp);
            return "expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        verificationOtpRepository.delete(verificationOtp);

        // call to create wallet
        walletService.createWallet(user);

        return "valid";
    }

    @Override
    public void generateNewVerificationOtp(User user) {
        publisher.publishEvent(new ResendVerificationOtpEvent(user));
    }

    @Override
    public void createPasswordResetOtpForUser(User user, String otp) {
        PasswordResetOtp passwordResetOtp = new PasswordResetOtp(user, otp);
        passwordResetOtpRepository.save(passwordResetOtp);
    }

    @Override
    public String validatePasswordResetOtp(String otp) {
        PasswordResetOtp passwordResetOtp = passwordResetOtpRepository.findByOtp(otp);

        // Checking verification otp exists in database or not
        // verification otp does not exist
        if(passwordResetOtp == null)
            return "invalid";

        // Password Reset otp exist, checking for expiration time
        User user = passwordResetOtp.getUser();
        Calendar calender = Calendar.getInstance();

        // if otp expired
        if(passwordResetOtp.getExpirationTime().getTime() - calender.getTime().getTime() <= 0) {
            passwordResetOtpRepository.delete(passwordResetOtp);
            return "expired";
        }
        return "valid";
    }
    @Override
    public Optional<User> getUserByPasswordResetOtp(String otp) {
        PasswordResetOtp passwordResetOtp =  passwordResetOtpRepository.findByOtp(otp);
        Optional<User> user = Optional.ofNullable(passwordResetOtp.getUser());
        passwordResetOtpRepository.delete(passwordResetOtp);
        return user;
    }

    @Override
    public boolean checkUserAvailability(String mobile) {
        return null != userRepository.findByMobile(mobile);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //Get user Details
    public UserDto getUserDto(User user, Wallet wallet) {
        UserDto userDto = new UserDto();

        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setMobile(user.getMobile());
        userDto.setRole(user.getRole());
        userDto.setMerchantType(user.getMerchantType());
        userDto.setBalance(wallet.getBalance());

        return userDto;
    }
}