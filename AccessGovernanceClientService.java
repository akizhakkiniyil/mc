package com.hackathon.accessguardian.mcp.client.service;

import com.example.accessgovernance.service.model.AccessRecommendation;
import com.example.accessgovernance.service.model.AnomalyExplanation;
import com.example.accessgovernance.service.model.PolicyDriftExplanation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux; // Import Flux for streaming
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@Service
@RequiredArgsConstructor
@Slf4j
public class AccessGovernanceClientService {
    private final ChatClient chatClient;
    private final McpClient mcpClient; // Injected MCP Client
    // Constructor to ensure ChatClient is built with tools
    public AccessGovernanceClientService(ChatClient.Builder chatClientBuilder, McpClient mcpClient) {
        this.chatClient = chatClientBuilder
                .defaultTools(mcpClient.getAvailableTools()) // MCP Client discovers and provides tools
                .build();
        this.mcpClient = mcpClient;
    }
    /**
     * Recommends group memberships for a new joiner or for a role change.
     * Uses Get_Employee_Context_Graph for inferential reasoning.
     */
    public AccessRecommendation recommendAccess(String employeeId, String employeeName, String department, String role, String lineManagerId) {
        log.info("Requesting access recommendation for new joiner: {} ({})", employeeName, employeeId);
        BeanOutputParser<AccessRecommendation> parser = new BeanOutputParser<>(AccessRecommendation.class);
        String format = parser.getFormat();
// Advanced Prompt Engineering for inferential task (Tree-of-Thoughts like)
        SystemMessage systemMessage = new SystemMessage("""
You are an expert Access Governance Advisor. Your goal is to provide precise and justified group membership recommendations,
strictly adhering to the principle of least privilege. You must use the provided tools to gather all necessary context
about the employee, their line manager, direct reports, and peers.
Follow these steps for your reasoning:
1. Use the 'Get_Employee_Context_Graph' tool for the new joiner's employee ID to understand their organizational context.
2. Analyze the 'employeeDetails', 'lineManagerDetails', 'directReports', and 'peerEmployeesInSameRoleAndDept' from the context graph.
3. Pay close attention to the 'currentGroupMemberships' of the line manager and peers.
4. Based on the new joiner's department, role, and the access patterns of their manager and peers,
suggest a minimal set of group memberships.
5. For each recommended group, provide a clear justification based on the employee's role, department, or peer/manager access.
6. If an employee already has access that seems excessive or unusual compared to peers, highlight it for review.
7. Your final output MUST be a JSON object conforming to the provided schema. Do NOT include any conversational text outside the JSON.
{format}
""");
        UserMessage userMessage = new UserMessage(Map.of(
                "employeeId", employeeId,
                "employeeName", employeeName,
                "department", department,
                "role", role,
                "lineManagerId", lineManagerId
        ), """
I need group membership recommendations for a new employee.
Employee ID: {employeeId}
Name: {employeeName}
Department: {department}
Role: {role}
Line Manager ID: {lineManagerId}
""");
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        String response = chatClient.prompt(prompt).call().content();
        return parser.parse(response);
    }
    /**
     * Explains a detected access anomaly in plain language.
     * Uses Detect_Access_Anomalies and Get_Employee_Details.
     */
    public AnomalyExplanation explainAnomaly(String employeeId) {
        log.info("Requesting anomaly explanation for employee: {}", employeeId);
        BeanOutputParser<AnomalyExplanation> parser = new BeanOutputParser<>(AnomalyExplanation.class);
        String format = parser.getFormat();
        SystemMessage systemMessage = new SystemMessage("""
You are an AI Security Analyst. Your task is to explain detected access anomalies in clear, concise, and non-technical language.
You must use the 'Detect_Access_Anomalies' tool to get the anomaly details and 'Get_Employee_Details' for employee context.
Follow these steps for your reasoning:
1. Use the 'Detect_Access_Anomalies' tool for the given employee ID.
2. If anomalies are found, use 'Get_Employee_Details' to get the employee's basic information.
3. For each anomaly, provide a plain language explanation of why it's unusual or risky.
4. List the key contributing factors that led to the anomaly detection.
5. Suggest a clear, actionable next step for IT or HR.
6. Your final output MUST be a JSON object conforming to the provided schema. Do NOT include any conversational text outside the JSON.
{format}
""");
        UserMessage userMessage = new UserMessage(Map.of("employeeId", employeeId),
                "Explain any access anomalies detected for employee ID: {employeeId}.");
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        String response = chatClient.prompt(prompt).call().content();
        return parser.parse(response);
    }
    /**
     * Explains policy drift for a group in plain language.
     * Uses Detect_Policy_Drift and Get_Group_Details.
     */
    public PolicyDriftExplanation explainPolicyDrift(String groupId, LocalDate baselineDate) {
        log.info("Requesting policy drift explanation for group: {} since {}", groupId, baselineDate);
        BeanOutputParser<PolicyDriftExplanation> parser = new BeanOutputParser<>(PolicyDriftExplanation.class);
        String format = parser.getFormat();
        SystemMessage systemMessage = new SystemMessage("""
You are an AI Compliance Oﬃcer. Your role is to analyze and explain policy drift for access groups.
You must use the 'Detect_Policy_Drift' tool to get the drift report and 'Get_Group_Details' for group context.
Follow these steps for your reasoning:
1. Use the 'Detect_Policy_Drift' tool for the given group ID and baseline date.
2. Use 'Get_Group_Details' to understand the group's purpose.
3. Explain in plain language what "policy drift" means for this specific group.
4. Clearly list who was added and who was removed since the baseline date.
5. Provide a brief assessment of the potential impact or risk of this drift.
6. Your final output MUST be a JSON object conforming to the provided schema. Do NOT include any conversational text outside the JSON.
{format}
""");
        UserMessage userMessage = new UserMessage(Map.of(
                "groupId", groupId,
                "baselineDate", baselineDate.toString()
        ), """
Explain the policy drift for group ID: {groupId} since {baselineDate}.
""");
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        String response = chatClient.prompt(prompt).call().content();
        return parser.parse(response);
    }
    /**
     * Provides a general chat response, potentially using tools for basic retrieval.
     */
    public String generalChat(String userQuery) {
        log.info("General chat query: {}", userQuery);
        SystemMessage systemMessage = new SystemMessage("""
You are a helpful AI assistant for access governance. You can answer questions about employees, groups,
and access patterns by using the available tools. If you need more information, ask clarifying questions.
Be concise and helpful.
""");
        UserMessage userMessage = new UserMessage(userQuery);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        return chatClient.prompt(prompt).call().content();
    }
    /**
     * Provides a general chat response with streaming, potentially using tools for basic retrieval.
     */
    public Flux<String> streamGeneralChat(String userQuery) {
        log.info("Streaming general chat query: {}", userQuery);
        SystemMessage systemMessage = new SystemMessage("""
You are a helpful AI assistant for access governance. You can answer questions about employees, groups,
and access patterns by using the available tools. If you need more information, ask clarifying questions.
Be concise and helpful.
""");
        UserMessage userMessage = new UserMessage(userQuery);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        return chatClient.prompt(prompt).stream().content();
    }
}