package myapp.erpnewapp.controller;

import jakarta.servlet.http.HttpSession;
import myapp.erpnewapp.model.ErpNextSessionInfo;
import myapp.erpnewapp.model.PurchaseOrder;
import myapp.erpnewapp.model.Supplier;
import myapp.erpnewapp.service.ErpNextPurchaseOrderService;
import myapp.erpnewapp.service.ErpNextSupplierService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PurchaseOrderController {

    @Autowired
    private ErpNextPurchaseOrderService purchaseOrderService;

    @Autowired
    private ErpNextSupplierService supplierService;


   @GetMapping("/purchase-orders")
    public String listPurchaseOrders(@RequestParam(value = "supplier", required = false) String supplierFilter,
                                    HttpSession session, Model model) {
        ErpNextSessionInfo info = (ErpNextSessionInfo) session.getAttribute("info");

        String sid = info.getSid();

        if (sid == null) {
            return "redirect:/login";
        }

        try {
            List<Supplier> suppliers = supplierService.getSuppliers(sid);
            model.addAttribute("suppliers", suppliers);

            List<PurchaseOrder> purchaseOrders = new ArrayList<>();

            if (supplierFilter != null && !supplierFilter.isEmpty()) {
                purchaseOrders = purchaseOrderService.getPurchaseOrdersBySupplier(sid, supplierFilter);
                model.addAttribute("purchaseOrders", purchaseOrders);
                model.addAttribute("selectedSupplier", supplierFilter);
            } else {
                purchaseOrders = purchaseOrderService.getPurchaseOrders(sid);
                model.addAttribute("purchaseOrders", purchaseOrders);
            }
            

            model.addAttribute("purchaseOrders", purchaseOrders);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Impossible de récupérer les données.");
        }

        return "purchase_order_list";
    }
}
