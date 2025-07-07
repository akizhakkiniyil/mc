package com.hackathon.accessguardian.mcp.client.ui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
@Route("login")
@PageTitle("Login | Access Governance AI")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(new H1("Access Governance AI Assistant"));
// This anchor links to Spring Security's OAuth2 login endpoint
        Anchor loginLink = new Anchor("/oauth2/authorization/azure-ad", "Login with Azure AD");
        add(loginLink);
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
// Check if there's an error parameter in the URL (e.g., from failed login)
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
// You could add a Notification here to show login error
// Notification.show("Login failed. Please try again.");
        }
    }
}
