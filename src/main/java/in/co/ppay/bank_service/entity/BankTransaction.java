package in.co.ppay.bank_service.entity;

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

public class BankTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;

    private String sid;
    private String rid;
    private double amount;
    private double senderUpdatedBalance;
    private double receiverUpdatedBalance;
    private Date date;
    private String status;
}
