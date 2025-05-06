package myapp.erpnewapp.controller;

import jakarta.servlet.http.HttpSession;
import myapp.erpnewapp.model.*;
import myapp.erpnewapp.service.ErpNextRFQService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Controller
@RequestMapping("/rfqs")
public class RFQDetailController {

    private final ErpNextRFQService rfqService;

    public RFQDetailController(ErpNextRFQService rfqService) {
        this.rfqService = rfqService;
    }

    @GetMapping("/{rfqName}")
    public String getRFQDetails(@PathVariable String rfqName,
                                @RequestParam String supplier,
                                Model model,
                                HttpSession session) throws Exception {
        ErpNextSessionInfo info = (ErpNextSessionInfo) session.getAttribute("info");
        if (info == null) {
            return "redirect:/login";
        }

        RequestForQuotation rfq = rfqService.getRFQDetails(rfqName, info.getSid());

        QuotationResponseDTO response = new QuotationResponseDTO();
        response.setItems(new ArrayList<>());
        response.setRfqName(rfqName);
        response.setSupplierName(supplier);

        model.addAttribute("rfq", rfq);
        model.addAttribute("supplierName", supplier);
        model.addAttribute("quotationResponse", response);
        return "rfq-details";
    }

    @PostMapping("/{rfqName}/submit-quotation")
    public String submitQuotation(@PathVariable String rfqName,
                                  @RequestParam String supplier,
                                  @ModelAttribute QuotationResponseDTO response,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            ErpNextSessionInfo info = (ErpNextSessionInfo) session.getAttribute("info");
            if (info == null) {
                redirectAttributes.addFlashAttribute("error", "Session expirée, veuillez vous reconnecter");
                return "redirect:/login";
            }

            response.setRfqName(rfqName);
            response.setSupplierName(supplier);

            String sqName = rfqService.createSupplierQuotationFromRFQ(response, info.getSid());
            redirectAttributes.addFlashAttribute("success", "Devis soumis avec succès: " + sqName);
            return "redirect:/rfqs/by-supplier?supplier=" + URLEncoder.encode(supplier, StandardCharsets.UTF_8);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la soumission: " + e.getMessage());
            return "redirect:/rfqs/" + rfqName + "?supplier=" + URLEncoder.encode(supplier, StandardCharsets.UTF_8);
        }
    }
}