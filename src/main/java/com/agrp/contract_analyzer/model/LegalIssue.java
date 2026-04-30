package com.agrp.contract_analyzer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class LegalIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String severity;

    @Column(length = 100)
    private String clause;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String problem;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @Column(length = 300)
    private String legalReference;

    @ManyToOne
    @JoinColumn(name = "contract_analysis_id", nullable = false)
    private ContractAnalysis contractAnalysis;
}