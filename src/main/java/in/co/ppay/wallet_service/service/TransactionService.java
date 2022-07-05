package in.co.ppay.wallet_service.service;

import in.co.ppay.wallet_service.entity.Transaction;
import in.co.ppay.wallet_service.model.ExpenseTrackerModel;
import in.co.ppay.wallet_service.model.TransactionDto;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

public interface TransactionService {
    ResponseEntity<?> sendMoney(Transaction transaction) throws Exception;

    ArrayList<TransactionDto> getTransactionHistory(String uid);

    boolean validateTransaction(Transaction transaction);

    String addMoneyFromBankAccount(String mobile, double amount, String pin);

    String sendMoneyToBankAccount(String mobile, double amount);

    ResponseEntity<ExpenseTrackerModel> getExpenseAnalytics(String mobile);
}
