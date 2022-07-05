package in.co.ppay.wallet_service.repository;

import in.co.ppay.wallet_service.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findByMobile(String mobile);
}
