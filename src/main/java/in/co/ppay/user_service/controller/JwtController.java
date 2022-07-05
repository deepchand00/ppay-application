package in.co.ppay.user_service.controller;

import in.co.ppay.user_service.entity.User;
import in.co.ppay.user_service.helper.JwtUtil;
import in.co.ppay.user_service.model.JwtRequest;
import in.co.ppay.user_service.model.JwtResponse;
import in.co.ppay.user_service.model.UserDto;
import in.co.ppay.user_service.service.CustomUserDetailService;
import in.co.ppay.user_service.service.UserService;
import in.co.ppay.wallet_service.entity.Wallet;
import in.co.ppay.wallet_service.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class JwtController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailService customUserDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private JwtUtil jwtUtil;

    @RequestMapping(value = "/getToken", method = RequestMethod.POST)
    public ResponseEntity<?> generateToken(@RequestBody JwtRequest jwtRequest) throws Exception {

        System.out.println("Inside Controller");
        System.out.println(jwtRequest);
        try {
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>("Invalid Credentials", HttpStatus.BAD_REQUEST);
        }catch (BadCredentialsException e)
        {
            if(null == userService.findUserByMobile(jwtRequest.getUsername()))
                return new ResponseEntity<>("User does not exist, Please register first", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>("Invalid Credentials", HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(jwtRequest.getUsername());

        String token = this.jwtUtil.generateToken(userDetails);
        System.out.println("JWT " + token);

        User user =  userService.findUserByMobile(jwtRequest.getUsername());
        Wallet wallet = walletService.findByMobile(jwtRequest.getUsername());
        UserDto userDto = userService.getUserDto(user, wallet);
        return ResponseEntity.ok(new JwtResponse(token,userDto));
    }
}
