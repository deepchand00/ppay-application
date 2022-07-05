package in.co.ppay.wallet_service.controller;

import in.co.ppay.wallet_service.model.ExpenseTrackerModel;
import in.co.ppay.wallet_service.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class ExpenseTrackerController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/user/getExpenseAnalytics")
    public ResponseEntity<ExpenseTrackerModel> getExpenseAnalytics(Principal principal){
        if(principal == null)
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        String mobile = principal.getName();

        return transactionService.getExpenseAnalytics(mobile);
    }

}
