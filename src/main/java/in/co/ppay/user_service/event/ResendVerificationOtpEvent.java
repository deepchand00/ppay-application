package in.co.ppay.user_service.event;

import in.co.ppay.user_service.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class ResendVerificationOtpEvent extends ApplicationEvent {
    private User user;
    public ResendVerificationOtpEvent(User user) {
        super(user);
        this.user = user;
    }
}
