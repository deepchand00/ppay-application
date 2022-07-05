package in.co.ppay.wallet_service.exception;

public class TransactionBadRequest extends RuntimeException {

    public TransactionBadRequest(String msg){
        super(msg);
    }
}