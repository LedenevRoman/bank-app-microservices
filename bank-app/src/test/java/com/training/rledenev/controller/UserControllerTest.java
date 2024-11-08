package com.training.rledenev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.rledenev.config.KafkaConfig;
import com.training.rledenev.dto.ErrorData;
import com.training.rledenev.dto.UserDto;
import com.training.rledenev.kafka.KafkaProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/database/schema-cleanup.sql")
@Sql("/database/create_tables.sql")
@Sql("/database/add_test_data.sql")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    KafkaProducer kafkaProducer;

    @MockBean
    KafkaConfig kafkaConfig;

    @Test
    @WithMockUser
    void shouldCreateUser() throws Exception {
        //given
        UserDto userDto = getUserDto();
        String userDtoJson = objectMapper.writeValueAsString(userDto);

        //when
        String createdUserJson = mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(userDtoJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto createdUser = objectMapper.readValue(createdUserJson, UserDto.class);

        //then
        userDto.setPassword(null);
        Assertions.assertEquals(userDto, createdUser);
    }

    @Test
    @WithMockUser
    void shouldCatchValidationExceptionOnCreateUser() throws Exception {
        //given
        UserDto userDto = getUserDto();
        userDto.setFirstName("");
        String userDtoJson = objectMapper.writeValueAsString(userDto);

        //when
        String errorDataJson = mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(userDtoJson))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //then
        ErrorData errorData = objectMapper.readValue(errorDataJson, ErrorData.class);

        Assertions.assertEquals("First name can not be empty", errorData.message());
    }

    @Test
    @WithMockUser
    void shouldCatchUserAlreadyExistsExceptionOnCreateUser() throws Exception {
        //given
        UserDto userDto = getUserDto();
        userDto.setEmail("isabella.white@yopmail.com");
        String userDtoJson = objectMapper.writeValueAsString(userDto);

        //when
        String errorDataJson = mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(userDtoJson))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //then
        ErrorData errorData = objectMapper.readValue(errorDataJson, ErrorData.class);

        Assertions.assertEquals(String.format("User with email %s already exists.", userDto.getEmail()),
                errorData.message());
    }

    private UserDto getUserDto() {
        UserDto userDto = new UserDto();
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@gmail.com");
        userDto.setAddress("test, address");
        userDto.setPhone("+123456789");
        userDto.setPassword("P@ssword1");
        return userDto;
    }
}