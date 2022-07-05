package in.co.ppay.bank_service.service;

import in.co.ppay.bank_service.entity.Bank;
import in.co.ppay.bank_service.model.BankUserDto;
import in.co.ppay.user_service.entity.User;

public interface BankService {
    boolean createAccount(User user);

    Bank findByMobile(String mobile);

    BankUserDto getBankUserDetails(String mobile);
}
