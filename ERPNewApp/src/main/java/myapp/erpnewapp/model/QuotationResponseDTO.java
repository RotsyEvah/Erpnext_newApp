package myapp.erpnewapp.model;

import lombok.Data;
import java.util.List;

@Data
public class QuotationResponseDTO {
    private String rfqName;
    private String supplierName;
    private List<RequestForQuotationItem> items;

    public String getRfqName() {
        return rfqName;
    }

    public void setRfqName(String rfqName) {
        this.rfqName = rfqName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public List<RequestForQuotationItem> getItems() {
        return items;
    }

    public void setItems(List<RequestForQuotationItem> items) {
        this.items = items;
    }
}