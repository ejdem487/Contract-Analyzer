package com.agrp.contract_analyzer.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ContractAnalysisResponse(
        Long id,
        String contractName,
        String contractType,
        String overallRisk,
        String addedClauses,
        String summary,
        List<LegalIssueDto> issues,
        LocalDateTime createdAt
) {}