package in.co.ppay.bank_service.repository;

import in.co.ppay.bank_service.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {

    List<BankTransaction> findAllBySid(String sid);

    @Query("select u from BankTransaction u where u.sid = ?1 OR u.rid = ?1")
    List<BankTransaction> findAllBySidOrRid(String sid);
}
