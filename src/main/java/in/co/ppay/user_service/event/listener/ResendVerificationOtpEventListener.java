package in.co.ppay.user_service.event.listener;

import in.co.ppay.user_service.entity.User;
import in.co.ppay.user_service.service.OtpService;
import in.co.ppay.user_service.event.ResendVerificationOtpEvent;
import in.co.ppay.user_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResendVerificationOtpEventListener
        implements ApplicationListener<ResendVerificationOtpEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Override
    public void onApplicationEvent(ResendVerificationOtpEvent event) {
        // create the verification otp for the user
        User user = event.getUser();
        String otp = otpService.generateOtp();
        userService.saveVerificationOtpForUser(otp, user);

        // send otp
        boolean isSent = otpService.sendOtp(otp, user.getMobile(), "register");
        if (isSent)
            log.info("Otp sent successfully");
        else
            log.info("Otp sending failed, try again");
    }
}
