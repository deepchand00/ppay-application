package in.co.ppay.wallet_service.exception;

public class WalletBadRequest extends RuntimeException {
    public WalletBadRequest() {

        super("WalletBadRequest : Wallet already exist with this mobile number");
    }
}