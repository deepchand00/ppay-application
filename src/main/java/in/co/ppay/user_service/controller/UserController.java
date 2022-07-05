package in.co.ppay.user_service.controller;

import in.co.ppay.user_service.entity.User;
import in.co.ppay.user_service.service.UserService;
import in.co.ppay.user_service.model.UserDto;
import in.co.ppay.wallet_service.entity.Wallet;
import in.co.ppay.wallet_service.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

//    principal get activated after adding spring security dependency
//     get User Details through current logged-in username using Principal
    @GetMapping("/user/getUser")
    public ResponseEntity<UserDto> getUser(Principal principal){
        String username = principal.getName();
        User user =  userService.findUserByMobile(username);
        Wallet wallet = walletService.findByMobile(username);
        UserDto userDto = userService.getUserDto(user, wallet);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    // Get all the users
    @GetMapping("/admin/users")
    public ResponseEntity<List<User>> getUsers(){
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }
}