package myapp.erpnewapp.controller;

import myapp.erpnewapp.model.ErpNextSessionInfo;
import myapp.erpnewapp.service.ErpNextSupplierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        ErpNextSessionInfo info = (ErpNextSessionInfo) session.getAttribute("info");
        if (info == null) return "redirect:/login";

        model.addAttribute("info", info);
        return "home";
    }
}