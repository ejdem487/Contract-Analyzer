package com.agrp.contract_analyzer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ContractAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String contractName;

    @Column(length = 50)
    private String contractType;

    @Column(length = 10)
    private String overallRisk;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String originalContractText;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String revisedContractText;

    @Column(columnDefinition = "TEXT")
    private String addedClauses;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "contractAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LegalIssue> issues = new ArrayList<>();
}