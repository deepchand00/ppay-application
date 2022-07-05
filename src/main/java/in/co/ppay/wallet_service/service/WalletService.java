package in.co.ppay.wallet_service.service;

import in.co.ppay.user_service.entity.User;
import in.co.ppay.wallet_service.entity.Wallet;

import java.util.List;

public interface WalletService {
    List<Wallet> findAll();

    Wallet findByMobile(String mobile);

    Wallet createWallet(User user);;

    boolean checkAvailability(String mobile);

}
