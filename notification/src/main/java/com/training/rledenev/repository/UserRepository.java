package com.training.rledenev.repository;

import com.training.rledenev.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.email FROM User u WHERE u.role = 'MANAGER'")
    String[] findManagersEmails();

    @Query("SELECT CONCAT(u.firstName, COALESCE(CONCAT(' ', u.lastName) , '')) FROM User u WHERE u.email = :email")
    String findFullNameByEmail(String email);

    @Query("SELECT CONCAT(u.firstName, COALESCE(CONCAT(' ', u.lastName) , '')) FROM User u " +
            "inner join Agreement a on a.manager = u where a.id = :agreementId")
    String findManagerFullNameByAgreementId(Long agreementId);

    @Query("SELECT u FROM User u INNER JOIN Account a on a.client = u " +
            "INNER JOIN Transaction t on t.debitAccount = a WHERE t.id = :transactionId")
    User findDebitAccountOwner(Long transactionId);

    @Query("SELECT u FROM User u INNER JOIN Account a on a.client = u " +
            "INNER JOIN Transaction t on t.creditAccount = a WHERE t.id = :transactionId")
    User findCreditAccountOwner(Long transactionId);
}
