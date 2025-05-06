package myapp.erpnewapp.model;

import lombok.Data;

@Data
public class PurchaseOrder {
    private String name;
    private String transaction_date;
    private String status;
    private double per_received;
    private double per_billed;
    private String supplier; // ← ID du fournisseur (ex: "SUP-0002")

    private boolean paid;

    public void setPaid(boolean paid){
        this.paid = paid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTransaction_date() {
        return transaction_date;
    }
    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public double getPer_received() {
        return per_received;
    }
    public void setPer_received(double per_received) {
        this.per_received = per_received;
    }
    public double getPer_billed() {
        return per_billed;
    }
    public void setPer_billed(double per_billed) {
        this.per_billed = per_billed;
    }
    public String getSupplier() {
        return supplier;
    }
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getPaymentStatus() {
        return paid ? "Payé" : "Non payé";
    }

    public String getReceptionStatus() {
        if (this.per_received == 100.0) {
            return "Reçu";
        } else {
            return "Non reçu";
        }
    }
    

    
}

