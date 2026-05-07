package com.agrp.contract_analyzer.dto;

public record AnalysisIssueResult(
        String severity,
        String clause,
        String problem,
        String recommendation,
        String legalReference
) {
}