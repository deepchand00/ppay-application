package in.co.ppay.bank_service.controller;

import in.co.ppay.bank_service.entity.Bank;
import in.co.ppay.bank_service.model.BankTransactionDto;
import in.co.ppay.bank_service.service.BankService;
import in.co.ppay.bank_service.service.BankTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BankTransactionController {

    @Autowired
    private BankService bankService;
    @Autowired
    private BankTransactionService bankTransactionService;

    // Get Balance by mobile number
    @GetMapping("/admin/getBankBalance/{mobile}")
    String getBal(@PathVariable String mobile) {
        Bank bank = bankService.findByMobile(mobile);
        String msg = "Either bank does account doesn't exist or bank account is not active";
        if(bank == null || bank.isActive() == false)
            return msg;
        log.info(msg);

        String bal = ""+bank.getBalance();
        log.info("Balance sent successfully");
        return bal;
    }

    // Get Balance of logged-in User
    @GetMapping("/user/getBankBalance")
    String getBalance(Principal principal){
        String mobile = principal.getName();

        Bank bank = bankService.findByMobile(mobile);
        String msg = "Either bank does account doesn't exist or bank account is not active";
        if(bank == null || bank.isActive() == false)
            return msg;
        log.info(msg);

        String bal = ""+bank.getBalance();
        log.info("Balance sent successfully");
        return bal;
    }

    // Get Bank User transaction history by mobile number
    @GetMapping("/admin/bankTransactionHistory/{mobile}")
    ResponseEntity<List<BankTransactionDto>> getTransactionHistory(@PathVariable String mobile) {
        log.info(String.format("$$ -> Producing Transaction --> %s", mobile));
        ArrayList<BankTransactionDto> list = bankTransactionService.getTransactionHistory(mobile);
        System.out.println();
        System.out.println("Transaction history");
        System.out.println(list);
        log.info("Transaction History sent");
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // Bank Transaction api for logged-in user
    @GetMapping("/user/bankTransactionHistory/getBankTxnHistory")
    ResponseEntity<List<BankTransactionDto>> getTransactionHistory(Principal principal){
        String mobile = principal.getName();
        log.info(String.format("$$ -> Producing Transaction --> %s", mobile));
        Bank bank = bankService.findByMobile(mobile);
        String msg = "Either bank does account doesn't exist or bank account is not active";
        if(bank == null || bank.isActive() == false) {
            log.info(msg);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String accountNo = bank.getAccountNo();
        List<BankTransactionDto> list = bankTransactionService.getTransactionHistory(accountNo);

        System.out.println();
        System.out.println("Transaction history");
        System.out.println(list);
        log.info("Transaction History sent");
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
