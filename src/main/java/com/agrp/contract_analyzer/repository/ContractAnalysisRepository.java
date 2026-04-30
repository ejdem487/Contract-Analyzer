package com.agrp.contract_analyzer.repository;

import com.agrp.contract_analyzer.model.ContractAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractAnalysisRepository extends JpaRepository<ContractAnalysis, Long> {

}