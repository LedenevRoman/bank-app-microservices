package com.training.rledenev.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.rledenev.dto.ErrorData;
import com.training.rledenev.dto.ProductDto;
import com.training.rledenev.enums.CurrencyCode;
import com.training.rledenev.enums.ProductType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/database/schema-cleanup.sql")
@Sql("/database/create_tables.sql")
@Sql("/database/add_test_data.sql")
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithUserDetails(value = "isabella.white@yopmail.com")
    void findSuitableProductPositiveCase() throws Exception {
        // given
        String productType = ProductType.LOAN.toString();
        String amount = "50000.0";
        String currencyCode = CurrencyCode.EUR.toString();

        // when
        MvcResult productGetSuitableResult = mockMvc.perform(MockMvcRequestBuilders.get("/product/suitable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("productType", productType)
                        .param("amount", amount)
                        .param("currencyCode", currencyCode))
                .andReturn();

        // then
        Assertions.assertEquals(200, productGetSuitableResult.getResponse().getStatus());

        String productGetSuitableResultJson = productGetSuitableResult.getResponse().getContentAsString();
        ProductDto receivedSuitableProduct = objectMapper.readValue(productGetSuitableResultJson, ProductDto.class);

        Assertions.assertEquals("Auto Loan", receivedSuitableProduct.getName());
    }

    @Test
    @WithUserDetails(value = "isabella.white@yopmail.com")
    void findSuitableProductGetOutOfLimitMessageNegativeCase() throws Exception {
        // given
        String productType = ProductType.LOAN.toString();
        String amount = "0";
        String currencyCode = CurrencyCode.EUR.toString();

        // when
        MvcResult productGetSuitableResult = mockMvc.perform(MockMvcRequestBuilders.get("/product/suitable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("productType", productType)
                        .param("amount", amount)
                        .param("currencyCode", currencyCode))
                .andReturn();

        // then
        Assertions.assertEquals(404, productGetSuitableResult.getResponse().getStatus());

        String productGetSuitableResultJson = productGetSuitableResult.getResponse().getContentAsString();
        ErrorData errorData = objectMapper.readValue(productGetSuitableResultJson, ErrorData.class);

        Assertions.assertEquals("Amount or period is out of limit", errorData.message());
    }

    @Test
    @WithUserDetails(value = "isabella.white@yopmail.com")
    void findSuitableCardTypePositiveCase() throws Exception {
        // given
        String productType = ProductType.DEBIT_CARD.toString();
        String amount = "0";
        String currencyCode = CurrencyCode.EUR.toString();

        // when
        MvcResult productGetSuitableResult = mockMvc.perform(MockMvcRequestBuilders.get("/product/suitable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .param("productType", productType)
                        .param("amount", amount)
                        .param("currencyCode", currencyCode))
                .andReturn();

        // then
        Assertions.assertEquals(200, productGetSuitableResult.getResponse().getStatus());

        String productGetSuitableResultJson = productGetSuitableResult.getResponse().getContentAsString();
        ProductDto receivedSuitableProduct = objectMapper.readValue(productGetSuitableResultJson, ProductDto.class);

        Assertions.assertEquals("Debit card", receivedSuitableProduct.getName());
        Assertions.assertSame(ProductType.DEBIT_CARD, receivedSuitableProduct.getType());
    }

    @Test
    @WithUserDetails(value = "isabella.white@yopmail.com")
    void shouldFindAllActiveProduct() throws Exception {
        //given
        List<ProductDto> expected = getAllActiveProductDtos();

        // when
        MvcResult productsGetAllActiveResult = mockMvc.perform(MockMvcRequestBuilders.get("/product/all-active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andReturn();

        // then
        Assertions.assertEquals(200, productsGetAllActiveResult.getResponse().getStatus());

        String productsGetAllActiveJson = productsGetAllActiveResult.getResponse().getContentAsString();
        List<ProductDto> receivedProductsAllActive = objectMapper.readValue(productsGetAllActiveJson,
                new TypeReference<>() {
                });

        Assertions.assertEquals(expected, receivedProductsAllActive);

    }

    private static List<ProductDto> getAllActiveProductDtos() {
        ProductDto productDto1 = new ProductDto();
        productDto1.setName("Auto Loan");
        productDto1.setType(ProductType.LOAN);
        productDto1.setMinLimit(60000);
        productDto1.setInterestRate(BigDecimal.valueOf(4.5)
                .setScale(4, RoundingMode.UNNECESSARY));
        productDto1.setPeriodMonths(60);

        ProductDto productDto2 = new ProductDto();
        productDto2.setName("Mortgage Loan");
        productDto2.setType(ProductType.LOAN);
        productDto2.setMinLimit(250000);
        productDto2.setInterestRate(BigDecimal.valueOf(3.2).setScale(4, RoundingMode.UNNECESSARY));
        productDto2.setPeriodMonths(240);

        ProductDto productDto3 = new ProductDto();
        productDto3.setName("Travel Loan");
        productDto3.setType(ProductType.LOAN);
        productDto3.setMinLimit(8000);
        productDto3.setInterestRate(BigDecimal.valueOf(8.2).setScale(4, RoundingMode.UNNECESSARY));
        productDto3.setPeriodMonths(12);

        ProductDto productDto4 = new ProductDto();
        productDto4.setName("Pension Savings Deposit");
        productDto4.setType(ProductType.DEPOSIT);
        productDto4.setMinLimit(30000);
        productDto4.setInterestRate(BigDecimal.valueOf(3.8).setScale(4, RoundingMode.UNNECESSARY));
        productDto4.setPeriodMonths(120);

        ProductDto productDto5 = new ProductDto();
        productDto5.setName("Children's Savings Deposit");
        productDto5.setType(ProductType.DEPOSIT);
        productDto5.setMinLimit(5000);
        productDto5.setInterestRate(BigDecimal.valueOf(4.5).setScale(4, RoundingMode.UNNECESSARY));
        productDto5.setPeriodMonths(60);

        ProductDto productDto6 = new ProductDto();
        productDto6.setName("VIP Deposit");
        productDto6.setType(ProductType.DEPOSIT);
        productDto6.setMinLimit(100000);
        productDto6.setInterestRate(BigDecimal.valueOf(4.8).setScale(4, RoundingMode.UNNECESSARY));
        productDto6.setPeriodMonths(24);

        ProductDto productDto7 = new ProductDto();
        productDto7.setName("Credit card");
        productDto7.setType(ProductType.CREDIT_CARD);
        productDto7.setMinLimit(10000);
        productDto7.setInterestRate(BigDecimal.valueOf(18.0).setScale(4, RoundingMode.UNNECESSARY));
        productDto7.setPeriodMonths(60);

        ProductDto productDto8 = new ProductDto();
        productDto8.setName("Debit card");
        productDto8.setType(ProductType.DEBIT_CARD);
        productDto8.setMinLimit(0);
        productDto8.setInterestRate(BigDecimal.ZERO.setScale(4, RoundingMode.UNNECESSARY));
        productDto8.setPeriodMonths(60);

        return List.of(productDto1, productDto2, productDto3, productDto4,
                productDto5, productDto6, productDto7, productDto8);
    }

    @Test
    @WithUserDetails(value = "isabella.white@yopmail.com")
    void shouldFindAllActiveByTypeProduct() throws Exception {
        // given
        String type = ProductType.LOAN.toString();
        List<ProductDto> expected = getProductDtos();

        // when
        MvcResult productsGetAllActiveByTypeResult = mockMvc
                .perform(MockMvcRequestBuilders.get("/product/all-active/" + type)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andReturn();

        // then
        Assertions.assertEquals(200, productsGetAllActiveByTypeResult.getResponse().getStatus());

        String productGetAllActiveByTypeResultJson = productsGetAllActiveByTypeResult.getResponse().getContentAsString();
        List<ProductDto> receivedProductsAllActiveByType = objectMapper.readValue(productGetAllActiveByTypeResultJson,
                new TypeReference<>() {
                });

        Assertions.assertEquals(expected, receivedProductsAllActiveByType);
    }

    private static List<ProductDto> getProductDtos() {
        ProductDto productDto1 = new ProductDto();
        productDto1.setName("Auto Loan");
        productDto1.setType(ProductType.LOAN);
        productDto1.setMinLimit(60000);
        productDto1.setInterestRate(BigDecimal.valueOf(4.5).setScale(4, RoundingMode.UNNECESSARY));
        productDto1.setPeriodMonths(60);

        ProductDto productDto2 = new ProductDto();
        productDto2.setName("Mortgage Loan");
        productDto2.setType(ProductType.LOAN);
        productDto2.setMinLimit(250000);
        productDto2.setInterestRate(BigDecimal.valueOf(3.2).setScale(4, RoundingMode.UNNECESSARY));
        productDto2.setPeriodMonths(240);

        ProductDto productDto3 = new ProductDto();
        productDto3.setName("Travel Loan");
        productDto3.setType(ProductType.LOAN);
        productDto3.setMinLimit(8000);
        productDto3.setInterestRate(BigDecimal.valueOf(8.2).setScale(4, RoundingMode.UNNECESSARY));
        productDto3.setPeriodMonths(12);

        return List.of(productDto1, productDto2, productDto3);
    }
}