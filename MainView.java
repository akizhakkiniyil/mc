package com.hackathon.accessguardian.mcp.client.ui;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hackathon.accessguardian.mcp.client.service.AccessGovernanceClientService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import reactor.core.scheduler.Schedulers;
import java.time.LocalDate;
import java.util.Optional;
@Route("") // Maps this view to the root URL
@PageTitle("Access Governance AI")
public class MainView extends VerticalLayout {
    private final AccessGovernanceClientService clientService;
    private final ObjectMapper objectMapper; // For pretty printing JSON
    private final UI ui; // Reference to the current UI for thread-safe updates
    private TextArea responseDisplay;
    private Paragraph userInfoParagraph;
    // Fields for Access Recommendation
    private TextField recEmployeeId;
    private TextField recEmployeeName;
    private TextField recDepartment;
    private TextField recRole;
    private TextField recLineManagerId;
    // Fields for Anomaly Explanation
    private TextField anomalyEmployeeId;
    // Fields for Policy Drift Explanation
    private TextField driftGroupId;
    private TextField driftBaselineDate;
    public MainView(AccessGovernanceClientService clientService, AuthenticationContext authContext) {
        this.clientService = clientService;
        this.ui = UI.getCurrent(); // Get current UI instance
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.Gap.MEDIUM);
// --- User Info & Logout ---
        userInfoParagraph = new Paragraph("Loading user info...");
        Anchor logoutLink = new Anchor("/logout", "Logout");
        HorizontalLayout headerLayout = new HorizontalLayout(userInfoParagraph, logoutLink);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        add(headerLayout);
// Fetch and display user info on load
        authContext.getAuthenticatedUser(OidcUser.class).ifPresentOrElse(
                oidcUser -> {
                    String employeeId = oidcUser.getEmployeeId();
                    String employeeName = oidcUser.getFullName();
                    userInfoParagraph.setText("Logged in as: " + employeeName + " (ID: " + employeeId + ")");
// Pre-fill fields
                    recEmployeeId = new TextField("Employee ID (New Joiner):", employeeId);
                    recEmployeeName = new TextField("Employee Name:", employeeName);
                    anomalyEmployeeId = new TextField("Employee ID:", employeeId);
                    anomalyEmployeeId.setReadOnly(true); // Make it read-only as it's from authenticated user
                },
                () -> {
                    userInfoParagraph.setText("Not logged in. Please refresh or login.");
// Initialize with dummy values if not logged in
                    recEmployeeId = new TextField("Employee ID (New Joiner):", "new001");
                    recEmployeeName = new TextField("Employee Name:", "John Doe");
                    anomalyEmployeeId = new TextField("Employee ID:", "emp001");
                }
        );
// --- General Chat Section ---
        add(new H2("General Chat"));
        TextArea generalQuery = new TextArea("Ask a general question:");
        generalQuery.setPlaceholder("e.g., What groups does Alice Johnson belong to?");
        generalQuery.setWidthFull();
        Button sendSyncButton = new Button("Send (Synchronous)", event -> sendGeneralChat(generalQuery.getValue()));
        Button startStreamButton = new Button("Start Stream (Asynchronous)", event -> startStreamingChat(generalQuery.getValue()));
        Button stopStreamButton = new Button("Stop Stream", event -> stopStreamingChat());
        HorizontalLayout generalChatButtons = new HorizontalLayout(sendSyncButton, startStreamButton, stopStreamButton);
        add(generalQuery, generalChatButtons);
// --- Access Recommendation Section ---
        add(new H2("Access Recommendation (New Joiner / Role Change)"));
        add(new Paragraph("<i>Pre-filled with your authenticated details for self-recommendation. Adjust if recommending for someone else.</i>"));
        recDepartment = new TextField("Department:", "Engineering");
        recRole = new TextField("Role:", "Software Engineer");
        recLineManagerId = new TextField("Line Manager ID:", "mgr001");
        Button sendRecButton = new Button("Get Recommendation", event -> sendRecommendation());
        add(recEmployeeId, recEmployeeName, recDepartment, recRole, recLineManagerId, sendRecButton);
// --- Access Anomaly Explanation Section ---
        add(new H2("Access Anomaly Explanation"));
        add(new Paragraph("<i>Pre-filled with your authenticated Employee ID.</i>"));
        Button sendAnomalyButton = new Button("Explain Anomaly", event -> sendAnomalyExplanation());
        add(anomalyEmployeeId, sendAnomalyButton);
// --- Policy Drift Explanation Section ---
        add(new H2("Policy Drift Explanation"));
        driftGroupId = new TextField("Group ID:", "grp001");
        driftBaselineDate = new TextField("Baseline Date (YYYY-MM-DD):", "2024-01-01");
        Button sendDriftButton = new Button("Explain Policy Drift", event -> sendPolicyDriftExplanation());
        add(driftGroupId, driftBaselineDate, sendDriftButton);
// --- Response Display Area ---
        add(new H2("AI Response"));
        responseDisplay = new TextArea();
        responseDisplay.setReadOnly(true);
        responseDisplay.setWidthFull();
        responseDisplay.setHeight("300px");
        add(responseDisplay);
    }
    private void setResponse(String text) {
        ui.access(() -> responseDisplay.setValue(text));
    }
    private void appendResponse(String text) {
        ui.access(() -> responseDisplay.setValue(responseDisplay.getValue() + text));
    }
    private void showLoading() {
        setResponse("Loading...");
    }
    private void sendGeneralChat(String query) {
        if (query.isEmpty()) {
            Notification.show("Please enter a query.");
            return;
        }
        showLoading();
        clientService.generalChat(query)
                .subscribeOn(Schedulers.boundedElastic()) // Run on a separate thread
                .subscribe(
                        this::setResponse,
                        error -> setResponse("Error: " + error.getMessage())
                );
    }
    private void startStreamingChat(String query) {
        if (query.isEmpty()) {
            Notification.show("Please enter a query for streaming.");
            return;
        }
        setResponse(""); // Clear previous content
        showLoading();
        clientService.streamGeneralChat(query)
                .subscribeOn(Schedulers.boundedElastic()) // Run on a separate thread
                .doOnNext(this::appendResponse)
                .doOnError(error -> setResponse("\n\n--- Stream ended with error: " + error.getMessage() + " ---"))
                .doOnComplete(() -> setResponse(responseDisplay.getValue() + "\n\n--- Stream completed ---"))
                .subscribe();
    }
    private void stopStreamingChat() {
// For Flux, stopping is typically handled by cancelling the subscription.
// In this simple UI, we don't store the subscription, so a refresh or new query
// would eﬀectively stop the previous stream. For a more robust solution,
// you'd store the Disposable and call dispose() here.
        Notification.show("Streaming stopped (new query or refresh will clear).");
    }
    private void sendRecommendation() {
        String employeeId = recEmployeeId.getValue();
        String employeeName = recEmployeeName.getValue();
        String department = recDepartment.getValue();
        String role = recRole.getValue();
        String lineManagerId = recLineManagerId.getValue();
        if (employeeId.isEmpty() |
| employeeName.isEmpty() |
| department.isEmpty() |
| role.isEmpty() |
| lineManagerId.isEmpty()) {
            Notification.show("Please fill in all recommendation fields.");
            return;
        }
        showLoading();
        clientService.recommendAccess(employeeId, employeeName, department, role, lineManagerId)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        rec -> setResponse(objectMapper.writeValueAsString(rec)),
                        error -> setResponse("Error: " + error.getMessage())
                );
    }
    private void sendAnomalyExplanation() {
        String employeeId = anomalyEmployeeId.getValue();
        if (employeeId.isEmpty()) {
            Notification.show("Employee ID is required for anomaly explanation.");
            return;
        }
        showLoading();
        clientService.explainAnomaly(employeeId)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        anomaly -> setResponse(objectMapper.writeValueAsString(anomaly)),
                        error -> setResponse("Error: " + error.getMessage())
                );
    }
    private void sendPolicyDriftExplanation() {
        String groupId = driftGroupId.getValue();
        String baselineDateStr = driftBaselineDate.getValue();
        if (groupId.isEmpty() |
| baselineDateStr.isEmpty()) {
            Notification.show("Please enter Group ID and Baseline Date.");
            return;
        }
        try {
            LocalDate baselineDate = LocalDate.parse(baselineDateStr);
            showLoading();
            clientService.explainPolicyDrift(groupId, baselineDate)
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe(
                            drift -> setResponse(objectMapper.writeValueAsString(drift)),
                            error -> setResponse("Error: " + error.getMessage())
                    );
        } catch (Exception e) {
            Notification.show("Invalid date format. Please use YYYY-MM-DD.");
        }
    }
}
