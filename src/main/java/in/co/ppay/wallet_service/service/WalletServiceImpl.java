package in.co.ppay.wallet_service.service;

import in.co.ppay.user_service.entity.User;
import in.co.ppay.wallet_service.entity.Wallet;
import in.co.ppay.wallet_service.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;
    @Override
    public List<Wallet> findAll() {
        return walletRepository.findAll();
    }

    @Override
    public Wallet findByMobile(String mobile) {
        return walletRepository.findByMobile(mobile);
    }

    @Override
    public Wallet createWallet(User user) {
        Wallet wallet = new Wallet();

        wallet.setMobile(user.getMobile());
        wallet.setMerchantType(user.getMerchantType());
        wallet.setActive(true);

        return walletRepository.save(wallet);
    }

    @Override
    public boolean checkAvailability(String mobile) {
        return null != walletRepository.findByMobile(mobile);
    }
}
