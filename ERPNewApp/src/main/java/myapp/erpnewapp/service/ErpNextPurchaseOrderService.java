package myapp.erpnewapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import myapp.erpnewapp.model.PurchaseOrder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ErpNextPurchaseOrderService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String erpnextUrl = "http://erpnext.localhost:8000";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<PurchaseOrder> getPurchaseOrders(String sid) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String resource = "Purchase Order";
        String fieldsParam = "[\"*\"]";
        String url = erpnextUrl + "/api/resource/" + resource + "?fields=" + fieldsParam;

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        System.out.println("Body " + response.getBody());
        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode dataNode = root.get("data");

            List<PurchaseOrder> orders = new ArrayList<>();
            for (JsonNode node : dataNode) {
                PurchaseOrder po = new PurchaseOrder();
                po.setName(node.path("name").asText(null));
                po.setTransaction_date(node.path("transaction_date").asText(null));
                po.setStatus(node.path("status").asText(null));
                po.setPer_received(node.path("per_received").asDouble(0));
                po.setPer_billed(node.path("per_billed").asDouble(0));
                po.setSupplier(node.path("supplier").asText(null));
                boolean isPaid = isPurchaseOrderPaid(sid, po.getName());
                po.setPaid(isPaid);

                orders.add(po);
            }
            return orders;
        } else {
            throw new Exception("Échec de la récupération des bons de commande : " + response.getStatusCode());
        }
    }


    public List<PurchaseOrder> getPurchaseOrdersBySupplier(String sid, String supplier) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);
    
        String resource = "Purchase Order";
        String fieldsParam = "[\"*\"]";
    
        // Ici on filtre côté serveur grâce à la query Frappe
        String filtersParam = "[[\"Purchase Order\", \"supplier\", \"=\", \"" + supplier + "\"]]";
        String url = erpnextUrl + "/api/resource/" + resource +
                     "?fields=" + fieldsParam +
                     "&filters=" + filtersParam;
    
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    
        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode dataNode = root.get("data");
    
            List<PurchaseOrder> purchaseOrders = new ArrayList<>();
            for (JsonNode node : dataNode) {
                PurchaseOrder po = new PurchaseOrder();
                po.setName(node.path("name").asText(null));
                po.setTransaction_date(node.path("transaction_date").asText(null));
                po.setStatus(node.path("status").asText(null));
                po.setSupplier(node.path("supplier").asText(null));
                po.setPer_received(node.path("per_received").asDouble(0));
                po.setPer_billed(node.path("per_billed").asDouble(0));
                boolean isPaid = isPurchaseOrderPaid(sid, po.getName());
                po.setPaid(isPaid);

                purchaseOrders.add(po);
            }
    
            return purchaseOrders;
        } else {
            throw new Exception("Échec de la récupération des bons de commande : " + response.getStatusCode());
        }
    }

    public boolean isPurchaseOrderPaid(String sid, String purchaseOrderName) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);
    
        // Solution alternative qui n'utilise pas Purchase Invoice Item directement
        String url = erpnextUrl + "/api/resource/Purchase Invoice?fields=[\"name\",\"status\",\"items.purchase_order\"]";
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    
        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode data = objectMapper.readTree(response.getBody()).get("data");
            for (JsonNode invoice : data) {
                // Vérifier si la facture contient des items liés à la commande
                JsonNode items = invoice.path("items");
                if (items.isArray()) {
                    for (JsonNode item : items) {
                        String poName = item.path("purchase_order").asText();
                        if (purchaseOrderName.equals(poName) && 
                            "Paid".equalsIgnoreCase(invoice.path("status").asText(""))) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } else {
            throw new Exception("Erreur lors de la récupération des factures: " + response.getStatusCode());
        }
    }
    
    
}
