package in.co.ppay.wallet_service.controller;

import in.co.ppay.wallet_service.entity.Transaction;
import in.co.ppay.wallet_service.exception.TransactionBadRequest;
import in.co.ppay.wallet_service.model.AddBalanceDetails;
import in.co.ppay.wallet_service.model.TransactionDto;
import in.co.ppay.wallet_service.repository.TransactionRepository;
import in.co.ppay.wallet_service.service.TransactionService;
import in.co.ppay.wallet_service.entity.Wallet;
import in.co.ppay.wallet_service.repository.WalletRepository;
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
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // Admin can get balance of any user by using his/her mobile
    @GetMapping("/admin/getBalance/{mobile}")
    double getBal(@PathVariable String mobile) throws Exception {
        Wallet wallet = walletRepository.findByMobile(mobile);
        if (wallet == null) throw new Exception("Wallet Not Found");
        else {
            return wallet.getBalance();
        }
    }

    // Get Balance of logged-in User
    @GetMapping("/user/getBalance")
    double getBalance(Principal principal)throws Exception{
        String username = principal.getName();
        Wallet wallet = walletRepository.findByMobile(username);
        return wallet.getBalance();
    }

    // Send money from one Wallet to other
    @PostMapping("/user/sendMoney")
    //return 201 instead of 200
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<?> sendMoney(@RequestBody Transaction transaction, Principal principal ) throws Exception {
        if(principal == null)
            return new ResponseEntity<>("Please login first", HttpStatus.BAD_REQUEST);

        boolean isValid = transactionService.validateTransaction(transaction);
        if(!isValid)
            return new ResponseEntity<>("Amount should be greater than 0", HttpStatus.BAD_REQUEST);
        transaction.setSid(principal.getName());
        if(transaction.getRid().equalsIgnoreCase(principal.getName()))
            return new ResponseEntity<>("Sender and Receiver cannot be same", HttpStatus.BAD_REQUEST);
        return transactionService.sendMoney(transaction);
    }

    //Send Money to Bank
    @PostMapping("/user/sendMoneyToBank")
    public ResponseEntity<String> sendToBank(@RequestBody AddBalanceDetails addBalanceDetails, Principal principal){
        if(principal == null)
            return new ResponseEntity<>("Please login first", HttpStatus.UNAUTHORIZED);

        double amount = addBalanceDetails.getAmount();
        if(amount <= 0)
            return new ResponseEntity<>("Amount should be greater than 0", HttpStatus.BAD_REQUEST);

        String mobile = principal.getName();
        String msg = transactionService.sendMoneyToBankAccount(mobile, amount);
        if(!msg.equalsIgnoreCase("ok"))
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>("Money Transferred successfully", HttpStatus.OK);
    }

    // Add Money From Bank of logged-in user
    @PostMapping("/user/addMoneyFromBank")
    public ResponseEntity<String> addBalance (@RequestBody AddBalanceDetails addBalanceDetails, Principal principal){
        if(principal == null)
            return new ResponseEntity<>("Please login first", HttpStatus.UNAUTHORIZED);

        double amount = addBalanceDetails.getAmount();
        String pin = addBalanceDetails.getPin();

        if(amount <= 0)
            return new ResponseEntity<>("Amount should be greater than 0", HttpStatus.BAD_REQUEST);

       String mobile = principal.getName();
       String  msg = transactionService.addMoneyFromBankAccount(mobile, amount, pin);
       if(!msg.equalsIgnoreCase("ok"))
           return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);

       return new ResponseEntity<>("Money Received successfully", HttpStatus.OK);
    }

    // Admin can get transaction history of any user using their mobile number (sid)
    @GetMapping("/admin/transactionHistory/{sid}")
    ResponseEntity<?> getTransactionHistory(@PathVariable String sid) {
        log.info(String.format("$$ -> Producing Transaction --> %s", sid));
        ArrayList<TransactionDto> list = transactionService.getTransactionHistory(sid);
        System.out.println();
        System.out.println("Transaction history");
        System.out.println(list);
        log.info("Transaction History sent");
//        if(list.isEmpty())
//            return new ResponseEntity<>("No Transactions performed", HttpStatus.OK);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // Transaction History api for logged-in user
    @GetMapping("/user/transactionHistory/getTxnHistory")
    ResponseEntity<?> getTransactionHistory(Principal principal){
        String username = principal.getName();
        log.info(String.format("$$ -> Producing Transaction --> %s", username));
        ArrayList<TransactionDto> list = transactionService.getTransactionHistory(username);
        System.out.println();
        System.out.println("Transaction history");
        System.out.println(list);
        log.info("Transaction History sent");
//        if(list.isEmpty())
//            return new ResponseEntity<>("No Transactions performed", HttpStatus.OK);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}