package com.agrp.contract_analyzer.dto;

import java.util.List;

public record LawDto(
        String number,
        String year,
        String title,
        List<String> relevantParagraphs
) {}