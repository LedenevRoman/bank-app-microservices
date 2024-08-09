package com.training.rledenev.controller;

import com.training.rledenev.dto.AgreementDto;
import com.training.rledenev.service.AgreementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/agreement")
public class AgreementController {
    private final AgreementService agreementService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public AgreementDto createNewAgreement(@RequestBody AgreementDto agreementDto) {
        return agreementService.createNewAgreement(agreementDto);
    }

    @GetMapping("/all/new")
    @PreAuthorize("hasAuthority('MANAGER')")
    public List<AgreementDto> getNewAgreements() {
        return agreementService.getAgreementsForManager();
    }

    @PutMapping("/confirm/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public void confirmAgreement(@PathVariable(name = "id") Long id) {
        agreementService.confirmAgreementByManager(id);
    }

    @PutMapping("/block/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public void blockAgreement(@PathVariable(name = "id") Long id) {
        agreementService.blockAgreementByManager(id);
    }

    @GetMapping("/{id}")
    public AgreementDto getAgreementDtoById(@PathVariable(name = "id") Long id) {
        return agreementService.getAgreementDtoById(id);
    }
}
