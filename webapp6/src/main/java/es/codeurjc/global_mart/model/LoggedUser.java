package es.codeurjc.global_mart.model;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class LoggedUser {
    private User user;
    private boolean isAdmin;
    private boolean isCompany;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void setIsAdmin() {
        if (user.getRole().equals("Admin")) {
            isAdmin = true;
        } else {
            isAdmin = false;
        }
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsCompany() {
        if (user.getRole().equals("Role[Company]")) {
            isCompany = true;
        } else {
            isCompany = false;
        }
    }

    public boolean isCompany() {
        return isCompany;
    }
}
