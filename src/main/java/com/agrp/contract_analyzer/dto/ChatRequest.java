package com.agrp.contract_analyzer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank @Size(max = 120)  //just for now bcs of api ai credit (free plan rn)
        String ask
) {
}
