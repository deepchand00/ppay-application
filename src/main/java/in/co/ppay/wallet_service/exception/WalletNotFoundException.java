package in.co.ppay.wallet_service.exception;

public class WalletNotFoundException extends RuntimeException {

    public WalletNotFoundException(String mobile) {

        super("Wallet id not found : " + mobile);
    }

}
