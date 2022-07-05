package in.co.ppay.bank_service.repository;

import in.co.ppay.bank_service.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    Bank findByMobile(String mobile);

    Bank findByAccountNo(String sid);
}
