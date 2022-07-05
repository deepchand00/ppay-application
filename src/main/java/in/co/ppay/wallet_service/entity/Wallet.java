package in.co.ppay.wallet_service.entity;

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
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;

    @NotNull
    @NotEmpty
    @Column(unique = true)
    private String mobile;

    private double balance = 100;
    private String merchantType;
    private boolean isActive = false;
}
