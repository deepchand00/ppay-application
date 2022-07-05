package in.co.ppay.bank_service.model;

import lombok.Data;

import java.util.Date;

@Data
public class BankTransactionDto {
    private Long Id;
    private String sid;
    private String rid;
    private double amount;
    private double updatedBalance;
    private Date date;
    private String status;
    private String transactionType;
}
