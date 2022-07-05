package in.co.ppay.bank_service.service;

import in.co.ppay.bank_service.entity.Bank;
import in.co.ppay.bank_service.model.BankUserDto;
import in.co.ppay.bank_service.repository.BankRepository;
import in.co.ppay.user_service.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class BankServiceImpl implements BankService {

    @Autowired
    private BankRepository bankRepository;

    @Override
    public boolean createAccount(User user) {
        Bank bank = new Bank();
        bank.setFirstName(user.getFirstName());
        bank.setLastName(user.getLastName());
        bank.setMobile(user.getMobile());
        bank.setAccountNo(generateAccountNumber());

        bankRepository.save(bank);
        return true;
    }

    @Override
    public Bank findByMobile(String mobile) {
        return bankRepository.findByMobile(mobile);
    }

    @Override
    public BankUserDto getBankUserDetails(String mobile) {
        Bank bank = bankRepository.findByMobile(mobile);
        if(bank == null)
            return null;

        BankUserDto bankUserDto = new BankUserDto();

        bankUserDto.setFirstName(bank.getFirstName());
        bankUserDto.setLastName(bank.getLastName());
        bankUserDto.setActive(bank.isActive());
        bankUserDto.setBalance(bank.getBalance());
        bankUserDto.setMobile(bank.getMobile());
        bankUserDto.setAccountNo(bank.getAccountNo());

        return bankUserDto;
    }

    private String generateAccountNumber() {
        Random random = new Random();
        String account = "PPAYPB-";
        for (int i = 0; i < 6; i++)
        {
            int n = random.nextInt(10) + 0;
            account += Integer.toString(n);
        }
        return account;
    }
}
