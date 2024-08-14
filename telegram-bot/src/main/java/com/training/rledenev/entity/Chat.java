package com.training.rledenev.entity;

import com.training.rledenev.dto.AccountDto;
import com.training.rledenev.dto.AgreementDto;
import com.training.rledenev.dto.TransactionDto;
import com.training.rledenev.dto.UserDto;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("Chat")
public class Chat {
    private Long id;
    private boolean isInRegistration;
    private boolean isInLogin;
    private String email;
    private String securityToken;
    private UserDto userDto;
    private String actionName;
    private AccountDto accountDto;
    private AgreementDto agreementDto;
    private Long chosenAgreementId;
    private boolean isMakingTransaction;
    private TransactionDto transactionDto;
}
