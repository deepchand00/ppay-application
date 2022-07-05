package in.co.ppay.user_service.repository;

import in.co.ppay.user_service.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    PasswordResetOtp findByOtp(String otp);
}
