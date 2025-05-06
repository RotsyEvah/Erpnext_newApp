package myapp.erpnewapp.controller;

import jakarta.servlet.http.HttpSession;
import myapp.erpnewapp.model.ErpNextSessionInfo;
import myapp.erpnewapp.model.PurchaseInvoice;
import myapp.erpnewapp.service.ErpNextInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PurchaseInvoiceController {

    @Autowired
    private ErpNextInvoiceService invoiceService;

    @GetMapping("/purchase-invoices")
    public String listPurchaseInvoices(HttpSession session, Model model) {
        ErpNextSessionInfo info = (ErpNextSessionInfo) session.getAttribute("info");

        String sid = info.getSid();

        if (sid == null) {
            return "redirect:/login";
        }

        try {
            List<PurchaseInvoice> purchaseInvoices = invoiceService.getPurchaseInvoices(sid);
            model.addAttribute("purchaseInvoices", purchaseInvoices);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Impossible de récupérer les factures.");
        }

        return "purchase_invoice_list";  // La vue JSP associée
    }


    @PostMapping("/purchase-invoices/pay")
    public String payInvoice(@RequestParam("invoiceName") String invoiceName, 
                            HttpSession session, 
                            Model model) {
        ErpNextSessionInfo info = (ErpNextSessionInfo) session.getAttribute("info");
        String sid = info.getSid();
    
        if (sid == null) {
            return "redirect:/login";
        }
    
        try {
            // 1. Vérifier si la facture existe
            PurchaseInvoice invoice = invoiceService.getPurchaseInvoiceByName(sid, invoiceName);
            if (invoice == null) {
                model.addAttribute("error", "Facture introuvable.");
            } else {
                // 2. Effectuer le paiement avec la nouvelle méthode
                invoiceService.payInvoice(sid, invoiceName); // Note: signature modifiée
                
                // 3. Rafraîchir la liste des factures
                List<PurchaseInvoice> purchaseInvoices = invoiceService.getPurchaseInvoices(sid);
                model.addAttribute("purchaseInvoices", purchaseInvoices);
                model.addAttribute("success", "Facture payée avec succès.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors du paiement: " + e.getMessage());
            
            // Pour le débogage, afficher le message complet dans les logs
            System.err.println("Erreur complète du paiement:");
            e.printStackTrace();
        }
    
        return "purchase_invoice_list";
    }


}