package in.co.ppay.bank_service.model;

import lombok.Data;

@Data
public class PinModel {
    private String oldPin;
    private String newPin;
}
