package com.training.rledenev.repository;

import com.training.rledenev.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByAgreement_Id(Long agreementId);
}
