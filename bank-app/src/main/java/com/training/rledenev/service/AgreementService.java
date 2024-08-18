package com.training.rledenev.service;

import com.training.rledenev.dto.AgreementDto;

import java.util.List;

public interface AgreementService {
    AgreementDto createAgreementWithNotification(AgreementDto agreementDto);

    List<AgreementDto> getAgreementsForManager();

    void confirmAgreementWithNotification(Long agreementId);

    AgreementDto confirmAgreement(Long agreementId);

    void blockAgreementWithNotification(Long agreementId);

    AgreementDto blockAgreement(Long agreementId);

    AgreementDto getAgreementDtoById(Long id);

    AgreementDto createAgreement(AgreementDto agreementDto);
}
