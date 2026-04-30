package com.agrp.contract_analyzer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContractAnalysisRequest(

        @NotBlank
        @Size(max = 100)
        String contractName,

        @NotBlank
        String originalContractText,

        @NotBlank
        String revisedContractText
) {}