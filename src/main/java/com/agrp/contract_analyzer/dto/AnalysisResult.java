package com.agrp.contract_analyzer.dto;

import java.util.List;

public record AnalysisResult(
        String addedClauses,
        String overallRisk,
        String summary,
        List<AnalysisIssueResult> issues
) {
}