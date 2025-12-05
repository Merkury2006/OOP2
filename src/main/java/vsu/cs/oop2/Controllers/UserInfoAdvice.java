package vsu.cs.oop2.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vsu.cs.oop2.Entity.User;
import vsu.cs.oop2.Services.UserService;

@ControllerAdvice
public class UserInfoAdvice {
    @Autowired
    private UserService userService;

    @ModelAttribute("username")
    public String addCurrentUsername(@CurrentSecurityContext SecurityContext currentSecurityContext) {
        Authentication authentication = currentSecurityContext.getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            String email = authentication.getName();
            try {
                User curUser = userService.getUserByEmail(email);
                return curUser.getUsername();
            } catch (IllegalArgumentException e) {
                return email;
            }
        }
        return null;
    }
}
