package in.co.ppay.user_service.event.listener;

import in.co.ppay.user_service.entity.User;
import in.co.ppay.user_service.event.RegistrationCompleteEvent;
import in.co.ppay.user_service.service.OtpService;
import in.co.ppay.user_service.service.UserService;
import in.co.ppay.wallet_service.exception.OtpFailedException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @SneakyThrows
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // create the verification otp for the user
        User user = event.getUser();
        String otp = otpService.generateOtp();
        log.info("Received Otp : " + otp);
        userService.saveVerificationOtpForUser(otp, user);

        // send otp
            boolean isSent = otpService.sendOtp(otp, user.getMobile(), "register");
            if (isSent)
                log.info("Otp sent successfully");
            else {
                log.info("Otp sending failed, try again");
                throw new OtpFailedException();
            }
    }
}
