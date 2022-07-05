package in.co.ppay.user_service.service;

import in.co.ppay.user_service.entity.User;
import in.co.ppay.user_service.repository.UserRepository;
import in.co.ppay.user_service.model.CustomUserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByMobile(username);
        if(user == null || !user.isEnabled()) {
            log.info("No user found");
            throw new UsernameNotFoundException("No User Found");
        }
        log.info("user found" + user + "\n");
        return new CustomUserDetail(user);
    }
}
