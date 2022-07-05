package in.co.ppay.user_service.repository;

import in.co.ppay.user_service.entity.VerificationOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationOtpRepository extends JpaRepository<VerificationOtp, Long> {
    VerificationOtp findByOtp(String otp);

//    VerificationOtp findByUser_id(Long id);
}
