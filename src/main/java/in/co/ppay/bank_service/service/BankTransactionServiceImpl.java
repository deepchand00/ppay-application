package in.co.ppay.bank_service.service;

import in.co.ppay.bank_service.model.BankTransactionDto;
import in.co.ppay.bank_service.repository.BankRepository;
import in.co.ppay.bank_service.repository.BankTransactionRepository;
import in.co.ppay.bank_service.entity.BankTransaction;
import in.co.ppay.wallet_service.entity.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
public class BankTransactionServiceImpl implements BankTransactionService {

    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @Autowired
    private BankRepository bankRepository;

    // Here sid means Bank account number
    @Override
    public ArrayList<BankTransactionDto> getTransactionHistory(String uid) {
        ArrayList<BankTransaction> transactions =
                (ArrayList<BankTransaction>)bankTransactionRepository.findAllBySidOrRid(uid);

        ArrayList<BankTransactionDto> list = new ArrayList<>();
        for(int i = 0; i < transactions.size(); i++){
            BankTransaction transaction = transactions.get(i);
            BankTransactionDto transactionDto = new BankTransactionDto();
            transactionDto.setId(transaction.getId());
            transactionDto.setDate(transaction.getDate());
            transactionDto.setAmount(transaction.getAmount());
            transactionDto.setRid(transaction.getRid());
            transactionDto.setSid(transaction.getSid());

            if(uid.equalsIgnoreCase(transaction.getSid())) {
                transactionDto.setUpdatedBalance(transaction.getSenderUpdatedBalance());
                transactionDto.setTransactionType("(-) Debit");
            }
            else if(uid.equalsIgnoreCase(transaction.getRid())) {
                transactionDto.setUpdatedBalance(transaction.getReceiverUpdatedBalance());
                transactionDto.setTransactionType("(+) Credit");
            }

            transactionDto.setStatus(transaction.getStatus());
            list.add(transactionDto);
        }
        return list;
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        BankTransaction bankTransaction = new BankTransaction();

        bankTransaction.setAmount(transaction.getAmount());
        bankTransaction.setSid(transaction.getSid());
        bankTransaction.setRid(transaction.getRid());
        bankTransaction.setDate(transaction.getDate());
        bankTransaction.setStatus(transaction.getStatus());
        bankTransaction.setSenderUpdatedBalance(transaction.getSenderUpdatedBalance());
        bankTransaction.setReceiverUpdatedBalance(transaction.getReceiverUpdatedBalance());

        bankTransactionRepository.save(bankTransaction);
    }

}
