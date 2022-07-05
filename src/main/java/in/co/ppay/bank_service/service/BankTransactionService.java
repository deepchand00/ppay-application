package in.co.ppay.bank_service.service;

import in.co.ppay.bank_service.entity.BankTransaction;
import in.co.ppay.bank_service.model.BankTransactionDto;
import in.co.ppay.wallet_service.entity.Transaction;

import java.util.ArrayList;

public interface BankTransactionService {

    ArrayList<BankTransactionDto> getTransactionHistory(String sid);

    void saveTransaction(Transaction bankTransaction);
}
