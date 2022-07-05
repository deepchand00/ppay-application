package in.co.ppay.bank_service.controller;

import in.co.ppay.bank_service.entity.Bank;
import in.co.ppay.bank_service.model.BankUserDto;
import in.co.ppay.bank_service.model.PinModel;
import in.co.ppay.bank_service.service.BankService;
import in.co.ppay.user_service.entity.User;
import in.co.ppay.user_service.service.UserService;
import in.co.ppay.bank_service.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.SchemaOutputResolver;
import java.security.Principal;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BankController {

    @Autowired
    private UserService userService;

    @Autowired
    private BankService bankService;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // create bank account

    @PostMapping("/user/createBankAccount")
    public ResponseEntity<String> createAccount(Principal principal) {
        if (principal == null)
            return new ResponseEntity<>("Please Register/login first", HttpStatus.BAD_REQUEST);
        String mobile = principal.getName();
        User user = userService.findUserByMobile(mobile);
        if (user == null)
            return new ResponseEntity<>
                    ("User does not exist", HttpStatus.BAD_REQUEST);
        if(bankRepository.findByMobile(mobile) != null)
            return new ResponseEntity<>
                    ("Bank Account already exist for this number",HttpStatus.BAD_REQUEST);

        boolean isCreated = bankService.createAccount(user);

        if (!isCreated)
            return new ResponseEntity<>("Account Creation failed, try later", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>("Bank Account created successfully", HttpStatus.CREATED);
    }

    @PostMapping("/user/setPin")
    public ResponseEntity<String> setPin(@RequestBody PinModel pinModel, Principal principal) {
        if (principal == null)
            return new ResponseEntity<>("Please login first", HttpStatus.BAD_REQUEST);

        System.out.println("\n" + "New Pin :" + pinModel.getNewPin() + ":" + "\n"
                                 + "Old Pin : " + pinModel.getOldPin());

        if(pinModel.getNewPin() == null || pinModel.getNewPin().isEmpty())
            return new ResponseEntity<>("Pin cannot be empty", HttpStatus.BAD_REQUEST);
        if(pinModel.getNewPin().length() != 4)
            return new ResponseEntity<>("Pin length should be four", HttpStatus.BAD_REQUEST);

        String mobile = principal.getName();

        Bank bank = bankService.findByMobile(mobile);
        if (bank == null)
            return new ResponseEntity<>("Account doesn't exist", HttpStatus.BAD_REQUEST);

        if (bank.getPin() == null) {
            bank.setPin(passwordEncoder.encode(pinModel.getNewPin()));
            bank.setActive(true);
            bankRepository.save(bank);
            return new ResponseEntity<>("Pin Setting done successfully", HttpStatus.CREATED);
        }
        else{
            if(pinModel.getOldPin() == null || pinModel.getNewPin() == null)
                return new ResponseEntity<>
                        ("Please provide old Pin and new Pin correctly to change Pin",
                                HttpStatus.BAD_REQUEST);

            if(!passwordEncoder.matches(pinModel.getOldPin(),bank.getPin()))
                return new ResponseEntity<>("Old pin does not match", HttpStatus.BAD_REQUEST);
            else{
                bank.setPin(passwordEncoder.encode(pinModel.getNewPin()));
                bankRepository.save(bank);
                return new ResponseEntity<>("Pin Changed successfully", HttpStatus.OK);
            }
        }
    }

    // Admin can Get Bank User Details by mobile number
    @GetMapping("/admin/getBankDetails/{mobile}")
    public ResponseEntity<BankUserDto> bankDetails(@PathVariable String mobile){
        BankUserDto bankUserDto = bankService.getBankUserDetails(mobile);

        if(bankUserDto == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(bankUserDto, HttpStatus.OK);

    }

    // Get Logged-in bank user details
    @GetMapping("/user/getBankDetails")
    public ResponseEntity<?> bankDetails(Principal principal) throws Exception{
        if(principal == null)
            throw new Exception("Please login first");

        String mobile = principal.getName();
        BankUserDto bankUserDto = bankService.getBankUserDetails(mobile);

        if(bankUserDto == null)
            return new ResponseEntity<>("Bank account does not exist", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(bankUserDto, HttpStatus.OK);
    }
}