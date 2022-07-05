package in.co.ppay.wallet_service.exception;

public class OtpFailedException extends RuntimeException{

    public OtpFailedException(){
        super("Otp Sending Failed");
    }
}