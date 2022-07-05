package in.co.ppay.wallet_service.service;

import in.co.ppay.bank_service.service.BankTransactionService;
import in.co.ppay.bank_service.entity.Bank;
import in.co.ppay.bank_service.repository.BankRepository;
import in.co.ppay.wallet_service.entity.Transaction;
import in.co.ppay.wallet_service.entity.Wallet;
import in.co.ppay.wallet_service.model.ExpenseTrackerModel;
import in.co.ppay.wallet_service.model.TransactionDto;
import in.co.ppay.wallet_service.repository.TransactionRepository;
import in.co.ppay.wallet_service.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService{

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BankTransactionService bankTransactionService;
    @Transactional
    @Override
    public ResponseEntity<?> sendMoney(Transaction transaction) throws Exception{

        transaction.setDate(new Date(Calendar.getInstance().getTime().getTime()));
        log.info("Transaction date : " + transaction.getDate().toString());

        Wallet senderWallet = walletRepository.findByMobile(transaction.getSid());
        Wallet receiverWallet = walletRepository.findByMobile(transaction.getRid());

        if(senderWallet==null){
            log.info("No wallet exist for your account");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("No wallet exist for your account");
        }
        if(receiverWallet == null){
            log.info("No wallet exist for mobile number : " + transaction.getRid());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("No wallet exist for mobile number : " + transaction.getRid());
        }

        double amt = transaction.getAmount();

        if (senderWallet.getBalance() < amt) {
            log.info("Insufficient Balance");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Insufficient balance in your wallet");
        }

        senderWallet.setBalance(senderWallet.getBalance()-amt);

        receiverWallet.setBalance(receiverWallet.getBalance()+amt);

        transaction.setSenderUpdatedBalance(senderWallet.getBalance());
        transaction.setReceiverUpdatedBalance(receiverWallet.getBalance());
        transaction.setStatus("SUCCESS");
        log.info(String.format("$$ -> Producing Transaction --> %s", transaction));
        walletRepository.save(receiverWallet);
        walletRepository.save(senderWallet);
        transaction.setReceiverMerchantType(receiverWallet.getMerchantType());
        transactionRepository.save(transaction);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("SUCCESS");
    }

    @Override
    public ArrayList<TransactionDto> getTransactionHistory(String uid) {
        ArrayList<Transaction> transactions =
                (ArrayList<Transaction>)transactionRepository.findAllBySidOrRid(uid);
        ArrayList<TransactionDto> list = new ArrayList<>();
        for(int i = 0; i < transactions.size(); i++){
            Transaction transaction = transactions.get(i);
            TransactionDto transactionDto = new TransactionDto();
            transactionDto.setId(transaction.getId());
            transactionDto.setDate(transaction.getDate());
            transactionDto.setAmount(transaction.getAmount());
            transactionDto.setRid(transaction.getRid());
            transactionDto.setSid(transaction.getSid());
            transactionDto.setReceiverMerchantType(transaction.getReceiverMerchantType());

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
    public boolean validateTransaction(Transaction transaction) {
        if(transaction.getAmount() <= 0)
            return false;
        return true;
    }

    @Override
    @Transactional
    public String addMoneyFromBankAccount(String mobile, double amount, String pin) {

        Transaction transaction = new Transaction();

        Bank bank = bankRepository.findByMobile(mobile);
        if(bank == null)
            return "Bank Account does not exist";
        else if(!bank.isActive())
            return "Bank is not Active, Please set your pin to activate your bank account";
        else if(bank.getBalance() - amount < 0)
            return "Insufficient Balance";
        else if(!passwordEncoder.matches(pin,bank.getPin()))
            return "Security Pin does not match";
        else {
            log.info("Adding money to account : " + mobile  + " from : " + bank.getAccountNo());
            transaction.setAmount(amount);
            transaction.setSid(bank.getAccountNo());
            transaction.setRid(mobile);
            transaction.setDate(new Date(Calendar.getInstance().getTime().getTime()));

            Wallet wallet = walletRepository.findByMobile(mobile);

            bank.setBalance(bank.getBalance() - amount);
            wallet.setBalance(wallet.getBalance() + amount);

            bankRepository.save(bank);
            walletRepository.save(wallet);

            transaction.setReceiverMerchantType(wallet.getMerchantType());
            transaction.setStatus("SUCCESS");
            transaction.setReceiverUpdatedBalance(wallet.getBalance());
            transaction.setSenderUpdatedBalance(bank.getBalance());
            bankTransactionService.saveTransaction(transaction);
            transactionRepository.save(transaction);
            return "ok";
        }
    }

    @Override
    public String sendMoneyToBankAccount(String mobile, double amount) {

        Transaction transaction = new Transaction();

        Bank bank = bankRepository.findByMobile(mobile);
        Wallet wallet = walletRepository.findByMobile(mobile);
        if(bank == null)
            return "Bank Account does not exist";
        else if(!bank.isActive())
            return "Bank is not Active, Please set your pin to activate your bank account";
        else if(wallet.getBalance() - amount < 0)
            return "Insufficient Balance";
        else {
            log.info("Adding money to Bank account : " + bank.getAccountNo()  + " from wallet : " + mobile);
            transaction.setAmount(amount);
            transaction.setSid(mobile);
            transaction.setRid(bank.getAccountNo());
            transaction.setDate(new Date(Calendar.getInstance().getTime().getTime()));

            wallet.setBalance(wallet.getBalance() - amount);
            bank.setBalance(bank.getBalance() + amount);

            bankRepository.save(bank);
            walletRepository.save(wallet);

            transaction.setSenderUpdatedBalance(wallet.getBalance());
            transaction.setReceiverUpdatedBalance(bank.getBalance());
            transaction.setReceiverMerchantType(bank.getMerchantType());
            transaction.setStatus("SUCCESS");
            bankTransactionService.saveTransaction(transaction);
            transactionRepository.save(transaction);
            return "ok";
        }
    }

    @Override
    public ResponseEntity<ExpenseTrackerModel> getExpenseAnalytics(String mobile) {

        final DecimalFormat df = new DecimalFormat("0.00");

        // For Amount spent
        double transferAmount = 0;    // Normal Money Transfer
        double entAmount = 0;         // Entertainment >> Movies/Mall/Games
        double fbAmount = 0;          // Food and Beverages
        double hfAmount = 0;          // Health and Fitness
        double gsAmount = 0;          // Grocery Store
        double othAmount = 0;         // Others

        // For %age
        String TRANSFER;    // Normal Money Transfer
        String ENT;         // Entertainment >> Movies/Mall/Games
        String FB;          // Food and Beverages
        String HF;          // Health and Fitness
        String GS;          // Grocery Store
        String OTH;         // Others

        // For total amount spent
        double totalAmount;

        ExpenseTrackerModel expenseTrackerModel = new ExpenseTrackerModel();
        List<Transaction> transactions = transactionRepository.findAllBySid(mobile);

        if(transactions.size() == 0)
            return new ResponseEntity<>(expenseTrackerModel, HttpStatus.OK);

        try {
            totalAmount = 0;
            for (int i = 0; i < transactions.size(); i++) {
                String merchant = transactions.get(i).getReceiverMerchantType();
                if (merchant == null)
                    continue;

                double amount = transactions.get(i).getAmount();
                totalAmount += amount;

                if (merchant.equalsIgnoreCase("TRANSFER"))
                    transferAmount += amount;
                else if (merchant.equalsIgnoreCase("ENT"))
                    entAmount += amount;
                else if (merchant.equalsIgnoreCase("FB"))
                    fbAmount += amount;
                else if (merchant.equalsIgnoreCase("HF"))
                    hfAmount += amount;
                else if (merchant.equalsIgnoreCase("GS"))
                    gsAmount += amount;
                else if (merchant.equalsIgnoreCase("OTH"))
                    entAmount += amount;
            }

            // calculating percentage
            TRANSFER = df.format(transferAmount * 100 / totalAmount) + "%";
            ENT = df.format(entAmount * 100 / totalAmount) + "%";
            FB = df.format(fbAmount * 100 / totalAmount) + "%";
            HF = df.format(hfAmount * 100 / totalAmount) + "%";
            GS = df.format(gsAmount * 100 / totalAmount) + "%";
            OTH = df.format(othAmount * 100 / totalAmount) + "%";
        }catch(Exception e){
            e.getMessage();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // attaching the percentage to Expense Tracker model  OR
        // Populating Expense Tracker Model
        // Setting %age
        expenseTrackerModel.setTRANSFER(TRANSFER);
        expenseTrackerModel.setENT(ENT);
        expenseTrackerModel.setFB(FB);
        expenseTrackerModel.setHF(HF);
        expenseTrackerModel.setGS(GS);
        expenseTrackerModel.setOTH(OTH);

        // Setting amount spent
        expenseTrackerModel.setTransferAmount(transferAmount);
        expenseTrackerModel.setFbAmount(fbAmount);
        expenseTrackerModel.setEntAmount(entAmount);
        expenseTrackerModel.setGsAmount(gsAmount);
        expenseTrackerModel.setHfAmount(hfAmount);
        expenseTrackerModel.setOthAmount(othAmount);

        // setting total amount spent
        expenseTrackerModel.setTotalAmountSpent(totalAmount);

        return new ResponseEntity<>(expenseTrackerModel, HttpStatus.OK);
    }
}
