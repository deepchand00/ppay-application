package in.co.ppay;

import in.co.ppay.user_service.entity.User;
import in.co.ppay.user_service.repository.UserRepository;
import in.co.ppay.wallet_service.entity.Wallet;
import in.co.ppay.wallet_service.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@Slf4j
public class PpayApplication implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(PpayApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		createUser();
	}

	// create a user
	private void createUser() {
		User user = new User();
		user.setFirstName("Admin");
		user.setLastName("Deepchand");
		user.setRole("ADMIN");
		user.setMobile("1234567890");
		user.setEnabled(true);
		user.setMerchantType("TRANSFER");
		user.setId(1l);
		user.setPassword(passwordEncoder.encode("12345678"));

		User save = userRepository.save(user);
		log.info("\nWallet : " + save + "\n");
		createWallet(save);
	}

	private void createWallet(User user) {
		Wallet wallet = new Wallet();
		wallet.setBalance(100);
		wallet.setId(1l);
		wallet.setMerchantType(user.getMerchantType());
		wallet.setMobile(user.getMobile());
		wallet.setActive(true);
		Wallet save = walletRepository.save(wallet);
		log.info("\nWallet : " + save + "\n");
	}
}