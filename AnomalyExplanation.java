package com.hackathon.accessguardian.mcp.client.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyExplanation {
    private String employeeId;
    private String employeeName;
    private String anomalyType;
    private String detectedDate; // Using String for date to match JSON output from LLM
    private String explanation; // Plain language explanation of why it's an anomaly
    private List<String> contributingFactors; // e.g., "No peers have this access", "Role mismatch"
    private String suggestedAction; // e.g., "Review access", "Confirm role"
}
