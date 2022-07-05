package in.co.ppay.user_service.service;

import org.springframework.stereotype.Service;

@Service
public interface OtpService {
    public String generateOtp();
    public boolean sendOtp(String otp, String mobile, String type);
}
