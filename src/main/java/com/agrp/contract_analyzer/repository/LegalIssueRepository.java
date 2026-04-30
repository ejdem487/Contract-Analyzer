package com.agrp.contract_analyzer.repository;

import com.agrp.contract_analyzer.model.LegalIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LegalIssueRepository extends JpaRepository<LegalIssue, Long> {

    List<LegalIssue> findByContractAnalysisId(Long contractAnalysisId);
    List<LegalIssue> findBySeverity(String severity);
}