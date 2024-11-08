package com.training.rledenev.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.rledenev.config.KafkaConfig;
import com.training.rledenev.dto.AccountDto;
import com.training.rledenev.dto.AgreementDto;
import com.training.rledenev.entity.User;
import com.training.rledenev.enums.CurrencyCode;
import com.training.rledenev.enums.ProductType;
import com.training.rledenev.enums.Role;
import com.training.rledenev.enums.Status;
import com.training.rledenev.kafka.KafkaProducer;
import com.training.rledenev.security.CustomUserDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Sql("/database/schema-cleanup.sql")
@Sql("/database/create_tables.sql")
@Sql("/database/add_test_data.sql")
@AutoConfigureMockMvc
class AgreementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    KafkaProducer kafkaProducer;

    @MockBean
    KafkaConfig kafkaConfig;

    @Test
    @WithUserDetails(value = "isabella.white@yopmail.com")
    void shouldCreateAgreement() throws Exception {
        // given
        AgreementDto agreementDto = new AgreementDto();
        agreementDto.setProductType(ProductType.LOAN);
        agreementDto.setCurrencyCode(CurrencyCode.EUR);
        agreementDto.setSum(BigDecimal.valueOf(50000.0).setScale(2, RoundingMode.UNNECESSARY));
        agreementDto.setProductName("Auto Loan");

        String agreementStr = objectMapper.writeValueAsString(agreementDto);

        // when
        String createdAgreementJson = mockMvc.perform(MockMvcRequestBuilders.post("/agreement/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(agreementStr))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AgreementDto createdAgreement = objectMapper.readValue(createdAgreementJson, AgreementDto.class);
        Long id = createdAgreement.getId();

        String agreementGetStringJson = mockMvc
                .perform(MockMvcRequestBuilders.get("/agreement/" + id))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        AgreementDto receivedAgreementJson = objectMapper.readValue(agreementGetStringJson, AgreementDto.class);

        Assertions.assertEquals(agreementDto.getProductType(), receivedAgreementJson.getProductType());
        Assertions.assertEquals(agreementDto.getCurrencyCode(), receivedAgreementJson.getCurrencyCode());
        Assertions.assertEquals(agreementDto.getSum(), receivedAgreementJson.getSum());
    }

    @Test
    @WithUserDetails(value = "mia.clark@yopmail.com")
    void shouldGetAllNewAgreements() throws Exception {
        // given
        List<AgreementDto> expected = getNewAgreements();

        // when
        MvcResult newAgreementsResult = mockMvc.perform(MockMvcRequestBuilders.get("/agreement/all/new"))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String newAgreementDtosJson = newAgreementsResult.getResponse().getContentAsString();
        List<AgreementDto> newAgreementDtos = objectMapper.readValue(newAgreementDtosJson, new TypeReference<>() {
        });

        Assertions.assertEquals(expected, newAgreementDtos);
    }

    @Test
    void shouldConfirmAgreement() throws Exception {
        // given
        AgreementDto agreementDto = new AgreementDto();
        agreementDto.setId(3L);
        agreementDto.setSum(BigDecimal.valueOf(11000).setScale(2, RoundingMode.UNNECESSARY));
        agreementDto.setInterestRate(BigDecimal.valueOf(3.8).setScale(4, RoundingMode.UNNECESSARY));

        UsernamePasswordAuthenticationToken authClient = getAuthenticationToken(getClient());
        UsernamePasswordAuthenticationToken authManager = getAuthenticationToken(getManager());

        // when
        SecurityContextHolder.getContext().setAuthentication(authClient);
        MvcResult getAllAccountsBeforeConfirmationResult = mockMvc
                .perform(MockMvcRequestBuilders.get("/account/all/client"))
                .andExpect(status().isOk())
                .andReturn();


        SecurityContextHolder.getContext().setAuthentication(authManager);
        mockMvc.perform(MockMvcRequestBuilders.put("/agreement/confirm/" + agreementDto.getId())
                        .with(securityContext(SecurityContextHolder.getContext()))
                        .with(csrf()))
                .andExpect(status().isOk());

        SecurityContextHolder.getContext().setAuthentication(authClient);
        MvcResult getAllAccountAfterConfirmationResult = mockMvc
                .perform(MockMvcRequestBuilders.get("/account/all/client"))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String accountNotConfirmedDtosJson = getAllAccountsBeforeConfirmationResult.getResponse().getContentAsString();
        List<AccountDto> allAccountsNotConfirmedDtoOfUser = objectMapper.readValue(accountNotConfirmedDtosJson,
                new TypeReference<>() {});

        String confirmedAccountDtosJson = getAllAccountAfterConfirmationResult.getResponse().getContentAsString();
        List<AccountDto> allAccountsConfirmedDtoOfUser = objectMapper.readValue(confirmedAccountDtosJson,
                new TypeReference<>() {});
        AccountDto confirmedAccountDto = allAccountsConfirmedDtoOfUser.getFirst();

        Assertions.assertEquals(allAccountsNotConfirmedDtoOfUser.size() + 1, allAccountsConfirmedDtoOfUser.size());
        Assertions.assertEquals(agreementDto.getSum(), confirmedAccountDto.getBalance());
        Assertions.assertEquals("Mia Clark", confirmedAccountDto.getManagerFullName());
        Assertions.assertSame(Status.ACTIVE, confirmedAccountDto.getStatus());
        Assertions.assertNotNull(confirmedAccountDto.getStartDate());
        Assertions.assertNotNull(confirmedAccountDto.getPaymentTerm());

    }

    @Test
    @WithUserDetails(value = "mia.clark@yopmail.com")
    void shouldBlockAgreement() throws Exception {
        // given
        String agreementId = "3";

        // when
        String agreementGetStringBeforeBlockJson = mockMvc
                .perform(MockMvcRequestBuilders.get("/agreement/" + agreementId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(MockMvcRequestBuilders.put("/agreement/block/" + agreementId)
                        .with(csrf()))
                .andExpect(status().isOk());

        String agreementGetStringAfterBlockJson = mockMvc
                .perform(MockMvcRequestBuilders.get("/agreement/" + agreementId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        AgreementDto agreementGetStringBeforeBlock = objectMapper.readValue(agreementGetStringBeforeBlockJson,
                AgreementDto.class);
        AgreementDto agreementGetStringAfterBlock = objectMapper.readValue(agreementGetStringAfterBlockJson,
                AgreementDto.class);

        Assertions.assertSame(Status.NEW, agreementGetStringBeforeBlock.getStatus());
        Assertions.assertSame(Status.BLOCKED, agreementGetStringAfterBlock.getStatus());
    }

    private static UsernamePasswordAuthenticationToken getAuthenticationToken(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(userDetails,
                null, userDetails.getAuthorities());
    }

    private static User getManager() {
        User manager = new User();
        manager.setId(3L);
        manager.setRole(Role.MANAGER);
        manager.setFirstName("Mia");
        manager.setLastName("Clark");
        manager.setEmail("mia.clark@yopmail.com");
        return manager;
    }

    private static User getClient() {
        User client = new User();
        client.setId(2L);
        client.setRole(Role.CLIENT);
        client.setFirstName("James");
        client.setLastName("Harris");
        client.setEmail("james.harris@yopmail.com");
        return client;
    }

    private static List<AgreementDto> getNewAgreements() {
        AgreementDto agreementDto1 = new AgreementDto();
        agreementDto1.setId(3L);
        agreementDto1.setClientEmail("james.harris@yopmail.com");
        agreementDto1.setProductType(ProductType.LOAN);
        agreementDto1.setProductName("Auto Loan");
        agreementDto1.setCurrencyCode(CurrencyCode.EUR);
        agreementDto1.setStatus(Status.NEW);
        agreementDto1.setSum(BigDecimal.valueOf(11000).setScale(2, RoundingMode.UNNECESSARY));
        agreementDto1.setPeriodMonths(60);
        agreementDto1.setInterestRate(BigDecimal.valueOf(4.5).setScale(4, RoundingMode.UNNECESSARY));

        AgreementDto agreementDto2 = new AgreementDto();
        agreementDto2.setId(4L);
        agreementDto2.setClientEmail("james.harris@yopmail.com");
        agreementDto2.setProductType(ProductType.DEBIT_CARD);
        agreementDto2.setProductName("Debit card");
        agreementDto2.setCurrencyCode(CurrencyCode.EUR);
        agreementDto2.setStatus(Status.NEW);
        agreementDto2.setSum(BigDecimal.valueOf(11000).setScale(2, RoundingMode.UNNECESSARY));
        agreementDto2.setPeriodMonths(60);
        agreementDto2.setInterestRate(BigDecimal.ZERO.setScale(4, RoundingMode.UNNECESSARY));

        return List.of(agreementDto1, agreementDto2);
    }
}