package com.hackathon.accessguardian.mcp.client.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDriftExplanation {
    private String groupName;
    private String baselineDate; // Using String for date to match JSON output from LLM
    private String explanation; // Plain language explanation of the drift
    private List<String> addedMembers;
    private List<String> removedMembers;
    private String impactAssessment; // e.g., "Low risk", "High risk - requires review"
}
