package myapp.erpnewapp.controller;

import myapp.erpnewapp.model.ErpNextSessionInfo;
import myapp.erpnewapp.service.ErpNextAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class  AuthController {

    @Autowired
    private ErpNextAuthService erpNextService;

    @GetMapping("/login")
    public String loginForm(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model
    ) {
        if (error != null) model.addAttribute("error", "Identifiants incorrects");
        if (logout != null) model.addAttribute("message", "Déconnexion réussie");
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session
    ) {
        ErpNextSessionInfo info = erpNextService.loginAndGetSessionInfo(username, password);
        if (info != null) {
            session.setAttribute("info", info);
            return "redirect:/home";
        }
        return "redirect:/login?error=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}