package com.hackathon.accessguardian.mcp.client.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessRecommendation {
    private String employeeId;
    private String employeeName;
    private String recommendationType; // e.g., "New_Joiner_Groups", "Excessive_Access_Highlight"
    private List<RecommendedGroup> recommendedGroups;
    private String overallJustification;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedGroup {
        private String groupId;
        private String groupName;
        private String justification; // Why this group is recommended/flagged
        private String action; // e.g., "ADD", "REVIEW", "REMOVE"
    }
}
