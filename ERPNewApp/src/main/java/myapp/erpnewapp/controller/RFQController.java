package myapp.erpnewapp.controller;

import jakarta.servlet.http.HttpSession;
import myapp.erpnewapp.model.ErpNextSessionInfo;
import myapp.erpnewapp.model.RequestForQuotation;
import myapp.erpnewapp.model.Supplier;
import myapp.erpnewapp.service.ErpNextRFQService;
import myapp.erpnewapp.service.ErpNextSupplierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/rfqs")
public class RFQController {

    private final ErpNextSupplierService supplierService;
    private final ErpNextRFQService rfqService;

    public RFQController(ErpNextSupplierService supplierService,
                         ErpNextRFQService rfqService) {
        this.supplierService = supplierService;
        this.rfqService = rfqService;
    }

    @GetMapping
    public String showSupplierSelection(Model model, HttpSession session) throws Exception {
        ErpNextSessionInfo info = (ErpNextSessionInfo) session.getAttribute("info");
        if (info == null) {
            return "redirect:/login";
        }

        List<Supplier> suppliers = supplierService.getSuppliers(info.getSid());
        model.addAttribute("suppliers", suppliers);
        return "select-supplier";
    }

    @GetMapping("/by-supplier")
    public String showRFQsForSupplier(
            @RequestParam String supplier,
            Model model,
            HttpSession session) throws Exception {

        ErpNextSessionInfo info = (ErpNextSessionInfo) session.getAttribute("info");
        if (info == null) {
            return "redirect:/login";
        }

        List<RequestForQuotation> rfqs = rfqService.getRFQsBySupplier(supplier, info.getSid());
        model.addAttribute("rfqs", rfqs);
        model.addAttribute("selectedSupplier", supplier);

        return "rfqs";
    }
}