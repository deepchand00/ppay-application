package in.co.ppay.wallet_service.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddBalanceDetails {
    private double amount;
    private String pin;
}