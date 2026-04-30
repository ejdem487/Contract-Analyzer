package com.agrp.contract_analyzer.controller;

import com.agrp.contract_analyzer.dto.ContractAnalysisRequest;
import com.agrp.contract_analyzer.dto.ContractAnalysisResponse;
import com.agrp.contract_analyzer.service.ContractAnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractAnalysisController {

    private final ContractAnalysisService contractAnalysisService;

    public ContractAnalysisController(ContractAnalysisService contractAnalysisService) {
        this.contractAnalysisService = contractAnalysisService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<ContractAnalysisResponse> analyze(
            @Valid @RequestBody ContractAnalysisRequest request) {
        return ResponseEntity.ok(contractAnalysisService.analyze(request));
    }

    @GetMapping
    public ResponseEntity<List<ContractAnalysisResponse>> findAll() {
        return ResponseEntity.ok(contractAnalysisService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractAnalysisResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(contractAnalysisService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        contractAnalysisService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}