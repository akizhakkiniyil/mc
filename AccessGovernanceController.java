package com.hackathon.accessguardian.mcp.client.controller;

import com.example.accessgovernance.service.AccessGovernanceClientService;
import com.example.accessgovernance.service.model.AccessRecommendation;
import com.example.accessgovernance.service.model.AnomalyExplanation;
import com.example.accessgovernance.service.model.PolicyDriftExplanation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import java.time.LocalDate;
@RestController
@RequestMapping("/access-ai-api") # Changed base path to avoid conflict with Vaadin routes
@RequiredArgsConstructor
@Slf4j
public class AccessGovernanceController {
    private final AccessGovernanceClientService clientService;
    /**
     * Endpoint to get authenticated user details for external API consumers.
     */
    @GetMapping("/user-info")
    public UserInfo getUserInfo(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser!= null) {
            String employeeId = oidcUser.getEmployeeId();
            String employeeName = oidcUser.getFullName();
            String email = oidcUser.getEmail();
            log.info("Authenticated OIDC User: {} (ID: {})", employeeName, employeeId);
            String department = oidcUser.getClaimAsString("department");
            String role = oidcUser.getClaimAsString("jobTitle");
            return new UserInfo(employeeId, employeeName, email, department, role);
        }
        log.warn("No authenticated user found for /user-info endpoint.");
        return new UserInfo(null, null, null, null, null);
    }
    public record UserInfo(String employeeId, String employeeName, String email, String department, String role) {}
    /**
     * General chat endpoint, can use tools for basic data retrieval (synchronous).
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String query) {
        return clientService.generalChat(query);
    }
    /**
     * General chat endpoint with streaming responses, can use tools for basic data retrieval.
     */
    @GetMapping(value = "/stream-chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestParam String query) {
        return clientService.streamGeneralChat(query);
    }
    /**
     * Recommends group memberships for a new joiner or role change.
     */
    @GetMapping("/recommend-access")
    public AccessRecommendation recommendAccess(
            @RequestParam String targetEmployeeId,
            @RequestParam String targetEmployeeName,
            @RequestParam String department,
            @RequestParam String role,
            @RequestParam String lineManagerId) {
        return clientService.recommendAccess(targetEmployeeId, targetEmployeeName, department, role, lineManagerId);
    }
    /**
     * Explains a detected access anomaly for an employee.
     */
    @GetMapping("/explain-anomaly")
    public AnomalyExplanation explainAnomaly(@RequestParam String employeeId) {
        return clientService.explainAnomaly(employeeId);
    }
    /**
     * Explains policy drift for a specific group.
     */
    @GetMapping("/explain-policy-drift")
    public PolicyDriftExplanation explainPolicyDrift(
            @RequestParam String groupId,
            @RequestParam String baselineDate) {
        return clientService.explainPolicyDrift(groupId, LocalDate.parse(baselineDate));
    }
}