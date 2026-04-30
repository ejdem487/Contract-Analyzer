package com.agrp.contract_analyzer.dto;

public record LegalIssueDto(
        String severity,
        String clause,
        String problem,
        String recommendation,
        String legalReference
) {}