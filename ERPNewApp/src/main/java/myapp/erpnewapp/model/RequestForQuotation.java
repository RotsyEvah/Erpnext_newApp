package myapp.erpnewapp.model;

import lombok.Data;
import java.util.List;

@Data
public class RequestForQuotation {
    private String name;            // Référence RFQ
    private String transactionDate; // Date de création
    private String status;          // Statut (Draft, Submitted, etc.)
    private String company;         // Société
    private List<RequestForQuotationItem> items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public List<RequestForQuotationItem> getItems() {
        return items;
    }

    public void setItems(List<RequestForQuotationItem> items) {
        this.items = items;
    }
}