package in.co.ppay.user_service.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;
    private String firstName;
    private String lastName;

    @NotNull
    @NotEmpty
    @Column(unique = true)
    private String mobile;

    @NotNull
    @Column(length = 60)
    private String password;

    private String role;
    private String merchantType;
    private boolean enabled = false;
}
