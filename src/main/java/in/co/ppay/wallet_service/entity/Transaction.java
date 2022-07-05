package in.co.ppay.wallet_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String sid;
    private String rid;
    private double amount;
    private double senderUpdatedBalance;
    private double receiverUpdatedBalance;
    private String receiverMerchantType;
    private Date date;
    private String status;
}
