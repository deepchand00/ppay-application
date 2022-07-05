package in.co.ppay.bank_service.model;

import lombok.Data;

@Data
public class BankUserDto {
    private String firstName;
    private String lastName;
    private String mobile;
    private double balance;
    private boolean isActive;
    private String accountNo;
    private String ifscCode = "PIET132103";
    private String accountType = "Savings";
}
