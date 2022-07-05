package in.co.ppay.wallet_service.model;

import lombok.Data;

import java.util.Date;
@Data
public class TransactionDto {
    private Long Id;
    private String sid;
    private String rid;
    private double amount;
    private double updatedBalance;
    private String receiverMerchantType;
    private Date date;
    private String status;
    private String transactionType;
}
