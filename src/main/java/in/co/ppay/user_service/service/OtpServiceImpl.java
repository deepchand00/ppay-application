package in.co.ppay.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;

@Service
@Slf4j
public class OtpServiceImpl implements OtpService {

    @Override
    public String generateOtp() {
        int len = 4;

        // Using numeric values
        String numbers = "0123456789";

        // Using random method
        Random random = new Random();

        char[] otp = new char[len];

        for (int i = 0; i < len; i++) {
            // Use of charAt() method : to get character value
            // Use of nextInt() as it is scanning the value as int
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }
        log.info("Generated OTP is : " + String.valueOf(otp));
        return String.valueOf(otp);
    }

    @Override
    public boolean sendOtp(String otp, String mobile, String type) {

        try {
            String apiKey = "Your Api Key";
//			String sendId = "FTWSMS";

            if(type.equalsIgnoreCase("register"))
                type = "registration";
            else type = "password reset";

            String message = "Your 4 digit OTP for " + type + " on PPAY Application is " + otp
                    + ". Please proceed with " + type + ".\n"
                    + "Regards:\n"
                    + "Team PPAY";
            message= URLEncoder.encode(message, "UTF-8");

//			String route="otp";
            String myUrl = "https://www.fast2sms.com/dev/bulkV2?authorization=" + apiKey
                    + "&route=v3&sender_id=FTWSMS&message="+message+"&language=english&flash=0&numbers="+mobile;

            // sending get request using java...
            log.info("Sending sms....");

            URL url = new URL(myUrl);

            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setRequestMethod("GET");

            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("cache-control", "no-cache");
            System.out.println("Wait..............");

            int code = con.getResponseCode();

            if(code == 200)
                return true;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return false;
    }
}
