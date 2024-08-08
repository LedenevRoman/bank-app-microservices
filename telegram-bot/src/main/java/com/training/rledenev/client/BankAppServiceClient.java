package com.training.rledenev.client;

import com.training.rledenev.config.FeignConfig;
import com.training.rledenev.dto.*;
import com.training.rledenev.enums.ProductType;
import com.training.rledenev.enums.Role;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@FeignClient(name = "bank-app", url = "${bank-app-service.url}", configuration = FeignConfig.class)
public interface BankAppServiceClient {

    @GetMapping("/users/role")
    Role getRole();

    @PostMapping("/auth")
    String auth(@RequestBody UserDto userDto);

    @GetMapping("/users")
    UserDto getCurrentUser();

    @PostMapping("/users/register")
    UserDto saveNewClient(@RequestBody UserDto userDto);

    @GetMapping("/account/all/client")
    List<AccountDto> getAccountsForClient();

    @GetMapping("/transaction/all")
    List<TransactionDto> getAllTransactionsOfAccount(@RequestParam String accountNumber);

    @GetMapping("/agreement/all/new")
    List<AgreementDto> getAgreementsForManager();

    @PutMapping("/agreement/confirm/{id}")
    void confirmAgreementByManager(@PathVariable(name = "id") Long agreementId);

    @PutMapping("/agreement/block/{id}")
    void blockAgreementByManager(@PathVariable(name = "id") Long agreementId);

    @GetMapping("/currency/{currencyCode}")
    BigDecimal getRateOfCurrency(@PathVariable String currencyCode);

    @GetMapping("/product/all-active")
    List<ProductDto> getAllActiveProductDtos();

    @GetMapping("/product/all-active/{type}")
    List<ProductDto> getActiveProductsWithType(@PathVariable(name = "type") ProductType productType);

    @GetMapping("/product/suitable")
    ProductDto getSuitableProduct(@RequestBody AgreementDto agreementDto);

    @PostMapping("/agreement/create")
    AgreementDto createNewAgreement(@RequestBody AgreementDto agreementDto);

    @PostMapping("/transaction/create")
    void createTransaction(@RequestBody TransactionDto transactionDto);
}
