package com.example.shelldemo;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.Availability;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@SpringBootApplication
public class ShellDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShellDemoApplication.class, args);
    }

}

@Service
class LoginService {

    private AtomicReference<String> user = new AtomicReference<>();

    void login(String username, String password) {
        this.user.set(username);
    }

    void logout() {
        this.user.set(null);
    }

    boolean isLoggedIn() {
        return this.user.get() != null;
    }

    String loggedInUser() {
        return this.isLoggedIn() ?  this.user.get() :  null;
    }

}

@ShellComponent
record LoginCommands (LoginService service) {

    @ShellMethod("login")
    public void login(String username, String password) {
        this.service.login(username, password);
    }

    @ShellMethodAvailability("logoutAvailability")
    @ShellMethod("logout")
    public void logout() {
        this.service.logout();
    }

    public Availability logoutAvailability() {
        return this.service.isLoggedIn() ? Availability.available() : Availability.unavailable("you must login!");
    }

}

@Component
record LoginPromptProvider(LoginService loginService) implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        return this.loginService.isLoggedIn() ?
                new AttributedString(this.loginService.loggedInUser() + ":>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW)) :
                new AttributedString("unknown:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
    }
}