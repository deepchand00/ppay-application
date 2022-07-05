package in.co.ppay.bank_service.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;
    private String firstName;
    private String lastName;

    @NotNull
    @NotEmpty
    @Column(unique = true)
    private String mobile;

    private String accountNo;
    private double balance = 5000;
    private String merchantType="TRANSFER";
    private String pin;
    private boolean isActive=false;
}
