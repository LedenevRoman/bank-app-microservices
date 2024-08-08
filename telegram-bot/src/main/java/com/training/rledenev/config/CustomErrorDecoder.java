package com.training.rledenev.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.training.rledenev.dto.ErrorData;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    public CustomErrorDecoder() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public ResponseStatusException decode(String methodKey, Response response) {
        try {
            ErrorData errorData = objectMapper.readValue(response.body().asInputStream(), ErrorData.class);
            return new ResponseStatusException(HttpStatus.valueOf(response.status()), errorData.message());
        } catch (IOException e) {
            return new ResponseStatusException(HttpStatus.valueOf(response.status()), response.reason());
        }
    }
}
