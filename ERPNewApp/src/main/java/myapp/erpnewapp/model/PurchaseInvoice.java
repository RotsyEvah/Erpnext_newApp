package myapp.erpnewapp.model;

import lombok.Data;

@Data
public class PurchaseInvoice {
    private String name;
    private String supplier;
    private String postingDate;
    private String status;
    private Double outstandingAmount;
    private Double total;
    private String company;

    public String getCompany(){
        return this.company;
    }

    public void setCompany(String company){
        this.company = company;
    }


    public PurchaseInvoice() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(Double outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public boolean isPaid() {
        return "Paid".equalsIgnoreCase(this.status);
    }
    
}
