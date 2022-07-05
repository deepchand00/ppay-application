package in.co.ppay.user_service.controller;

import in.co.ppay.user_service.entity.User;
import in.co.ppay.user_service.event.RegistrationCompleteEvent;
import in.co.ppay.user_service.model.PasswordModel;
import in.co.ppay.user_service.model.UserModel;
import in.co.ppay.user_service.repository.PasswordResetOtpRepository;
import in.co.ppay.user_service.service.OtpService;
import in.co.ppay.user_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/public/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserModel userModel) {
        System.out.println("\n" + userModel + "\n");
        boolean isMatched = userService.checkMatchingPassword(userModel);
        if (!isMatched)
            return new ResponseEntity<>
                    ("Password and Confirm Password does not match", HttpStatus.BAD_REQUEST);
        boolean isExist = userService.checkUserAvailability(userModel.getMobile());
        if(isExist)
            return new ResponseEntity<>
                    ("User already exist with given mobile number", HttpStatus.BAD_REQUEST);

        if(userModel.getRole().equalsIgnoreCase("ADMIN"))
            return new ResponseEntity<>("You cannot become a user with Admin privileges", HttpStatus.BAD_REQUEST);


        User user = userService.registerUser(userModel);
        try {
            publisher.publishEvent(new RegistrationCompleteEvent(user));
        }catch(Exception e){
            return new ResponseEntity<>("Otp Sending Failed, try again later", HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>("Otp sent successfully", HttpStatus.OK);
    }

    // otp verification
    @GetMapping("/public/verifyRegistration")
    public ResponseEntity<String> verifyRegistration(@RequestParam("otp") String otp) {
        String result = userService.validateVerificationOtp(otp);
        if (result.equalsIgnoreCase("valid"))
            return new ResponseEntity<>("User verified successfully", HttpStatus.CREATED);
        else
            return new ResponseEntity<>("Invalid Credentials", HttpStatus.BAD_REQUEST);
    }

    // otp resend
    @GetMapping("/public/resendVerifyOtp")
    public ResponseEntity<String> resendVerificationOtp(@RequestParam("mobile") String mobile) {
        User user = userService.findUserByMobile(mobile);
        if(user == null)
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        userService.generateNewVerificationOtp(user);
        return new ResponseEntity<>("OTP sent successfully", HttpStatus.OK);
    }

    // reset otp
    @PostMapping("/public/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordModel passwordModel) {
        User user = userService.findUserByMobile(passwordModel.getMobile());
        if (user != null) {
            String otp = otpService.generateOtp();
            userService.createPasswordResetOtpForUser(user, otp);
            try {
                if(!otpService.sendOtp(otp, passwordModel.getMobile(), "reset"))
                    throw new Exception("Otp sending failed");
            }catch(Exception e){
                log.info(e.getMessage());
                return new ResponseEntity<>("OTP sending failed, try again later", HttpStatus.SERVICE_UNAVAILABLE);
            }
            log.info("OTP sent successfully");
           return new ResponseEntity<>("Reset OTP sent successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
    }

    // savePassword otp
    @PostMapping("/public/savePassword")
    public ResponseEntity<String> savePassword(@RequestBody PasswordModel passwordModel) {
        String result = userService.validatePasswordResetOtp(passwordModel.getOtp());
        if (!result.equalsIgnoreCase("valid"))
            return new ResponseEntity<>("Invalid otp", HttpStatus.BAD_REQUEST);

        if(passwordModel.getNewPassword() == null || passwordModel.getNewPassword().length() < 8)
            return new ResponseEntity<>("Password length must be of at least 8 characters",
                    HttpStatus.BAD_REQUEST);

        Optional<User> user = userService.getUserByPasswordResetOtp(passwordModel.getOtp());

        if (user.isPresent()) {
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            return new ResponseEntity<>("Password Reset successfully", HttpStatus.OK);
        } else
            return new ResponseEntity<>("Expired Otp", HttpStatus.BAD_REQUEST);
    }

    //Change Password
    @PostMapping("/user/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody PasswordModel passwordModel, Principal principal) {

        if(principal == null)
            return new ResponseEntity<>("Please login first", HttpStatus.BAD_REQUEST);

        User user = userService.findUserByMobile(principal.getName());
        if(user == null)
            return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
        if (!userService.checkIfValidOldPassword(user, passwordModel.getOldPassword()))
            return new ResponseEntity<>("Invalid Old Password", HttpStatus.BAD_REQUEST);

        // save new Password
        if(passwordModel.getNewPassword().length() < 8)
            return new ResponseEntity<>("Passqword length must be of at least 8 characters", HttpStatus.BAD_REQUEST);
        userService.changePassword(user, passwordModel.getNewPassword());
        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }
}