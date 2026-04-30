package com.agrp.contract_analyzer.dto;

import java.util.List;

public record LawIdentificationResult(
        String contractType,
        List<LawDto> relevantLaws,
        List<String> keyTopics
) {}