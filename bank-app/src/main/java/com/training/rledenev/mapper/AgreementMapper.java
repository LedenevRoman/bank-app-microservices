package com.training.rledenev.mapper;

import com.training.rledenev.dto.AgreementDto;
import com.training.rledenev.entity.Agreement;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AgreementMapper {

    @Mapping(source = "sum", target = "sum",
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    Agreement mapToEntity(AgreementDto agreementDto);

    @Named("toAgreementDto")
    @Mapping(source = "client.email", target = "clientEmail")
    @Mapping(source = "sum", target = "sum")
    @Mapping(source = "product.interestRate", target = "interestRate")
    @Mapping(source = "product.type", target = "productType")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.periodMonths", target = "periodMonths")
    AgreementDto mapToDto(Agreement agreement);

    @IterableMapping(qualifiedByName = "toAgreementDto")
    List<AgreementDto> mapToListDtos(List<Agreement> agreements);
}
