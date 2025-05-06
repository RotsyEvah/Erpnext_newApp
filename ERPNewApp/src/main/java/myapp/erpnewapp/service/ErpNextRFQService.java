package myapp.erpnewapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import myapp.erpnewapp.model.QuotationResponseDTO;
import myapp.erpnewapp.model.RequestForQuotation;
import myapp.erpnewapp.model.RequestForQuotationItem;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ErpNextRFQService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String erpnextUrl;

    public ErpNextRFQService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.erpnextUrl = "http://erpnext.localhost:8000";
    }

    public List<RequestForQuotation> getRFQs(String sid) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(erpnextUrl + "/api/resource/Request for Quotation")
                .queryParam("fields", "[\"name\",\"transaction_date\",\"status\",\"company\"]");

        URI uri = builder.build().encode().toUri();

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                uri, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode dataNode = root.get("data");

            List<RequestForQuotation> rfqs = new ArrayList<>();
            for (JsonNode node : dataNode) {
                RequestForQuotation rfq = new RequestForQuotation();
                rfq.setName(node.path("name").asText());
                rfq.setTransactionDate(node.path("transaction_date").asText());
                rfq.setStatus(node.path("status").asText());
                rfq.setCompany(node.path("company").asText());
                rfqs.add(rfq);
            }
            return rfqs;
        } else {
            throw new RuntimeException("Erreur lors de la récupération des RFQs: " +
                    response.getStatusCode() + " - " + response.getBody());
        }
    }

    public RequestForQuotation getRFQDetails(String rfqName, String sid) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = erpnextUrl + "/api/resource/Request for Quotation/" +
                URLEncoder.encode(rfqName, StandardCharsets.UTF_8);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.get("data");

            RequestForQuotation rfq = new RequestForQuotation();
            rfq.setName(data.path("name").asText());
            rfq.setTransactionDate(data.path("transaction_date").asText());
            rfq.setStatus(data.path("status").asText());
            rfq.setCompany(data.path("company").asText());

            // Récupération des articles
            List<RequestForQuotationItem> items = new ArrayList<>();
            JsonNode itemsNode = data.get("items");
            if (itemsNode != null && itemsNode.isArray()) {
                for (JsonNode itemNode : itemsNode) {
                    RequestForQuotationItem item = new RequestForQuotationItem();
                    item.setItemCode(itemNode.path("item_code").asText());
                    item.setItemName(itemNode.path("item_name").asText());
                    item.setDescription(itemNode.path("description").asText());
                    item.setQty(itemNode.path("qty").asDouble());
                    item.setUom(itemNode.path("uom").asText());
                    item.setWarehouse(itemNode.path("warehouse").asText());
                    items.add(item);
                }
            }
            rfq.setItems(items);

            return rfq;
        } else {
            throw new RuntimeException("Erreur lors de la récupération du RFQ: " +
                    response.getStatusCode() + " - " + response.getBody());
        }
    }

    public List<RequestForQuotation> getRFQsBySupplier(String supplierName, String sid) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 1. D'abord, récupérer tous les RFQ qui ont ce fournisseur dans leurs suppliers
        String filters = String.format("[[\"Request for Quotation Supplier\",\"supplier\",\"=\",\"%s\"]]", supplierName);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(erpnextUrl + "/api/resource/Request for Quotation")
                .queryParam("fields", "[\"name\",\"transaction_date\",\"status\",\"company\",\"vendor\"]")
                .queryParam("filters", filters);

        URI uri = builder.build().encode().toUri();

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                uri, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode dataNode = root.get("data");

            List<RequestForQuotation> rfqs = new ArrayList<>();
            for (JsonNode node : dataNode) {
                RequestForQuotation rfq = new RequestForQuotation();
                rfq.setName(node.path("name").asText());
                rfq.setTransactionDate(node.path("transaction_date").asText());
                rfq.setStatus(node.path("status").asText());
                rfq.setCompany(node.path("company").asText());
                rfqs.add(rfq);
            }
            return rfqs;
        } else {
            throw new RuntimeException("Erreur lors de la récupération des RFQs par fournisseur: " +
                    response.getStatusCode() + " - " + response.getBody());
        }
    }


    public boolean isSupplierInRFQ(String rfqName, String supplierName, String sid) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = erpnextUrl + "/api/resource/Request for Quotation/" +
                URLEncoder.encode(rfqName, StandardCharsets.UTF_8);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode data = objectMapper.readTree(response.getBody()).get("data");
            JsonNode suppliers = data.get("suppliers");
            if (suppliers != null) {
                for (JsonNode supplier : suppliers) {
                    if (supplierName.equals(supplier.path("supplier").asText())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String createSupplierQuotationFromRFQ(QuotationResponseDTO response, String sid) throws Exception {
        // Vérification de sécurité
        if (!isSupplierInRFQ(response.getRfqName(), response.getSupplierName(), sid)) {
            throw new SecurityException("Ce fournisseur n'est pas autorisé à répondre à ce RFQ");
        }

        // Récupérer les détails du RFQ original
        RequestForQuotation rfq = getRFQDetails(response.getRfqName(), sid);

        // Préparer les headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Construire le payload
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode payload = mapper.createObjectNode();

        // Champs de base
        payload.put("doctype", "Supplier Quotation");
        payload.put("supplier", response.getSupplierName());
        payload.put("company", rfq.getCompany());
        payload.put("transaction_date", LocalDate.now().toString());
        payload.put("valid_till", LocalDate.now().plusDays(30).toString());
        payload.put("request_for_quotation", rfq.getName());
        payload.put("status", rfq.getStatus());

        // Construction des items
        ArrayNode itemsArray = mapper.createArrayNode();
        for (RequestForQuotationItem item : response.getItems()) {
            ObjectNode itemNode = mapper.createObjectNode();
            itemNode.put("item_code", item.getItemCode());
            itemNode.put("item_name", item.getItemName());
            itemNode.put("description", item.getDescription());
            itemNode.put("qty", item.getQty());
            itemNode.put("uom", item.getUom());
            itemNode.put("warehouse", item.getWarehouse());
            itemNode.put("rate", item.getProposedRate());
            itemsArray.add(itemNode);
        }
        payload.set("items", itemsArray);

        // Envoyer la requête pour créer le Supplier Quotation
        String sqUrl = erpnextUrl + "/api/resource/Supplier Quotation";
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(payload), headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(sqUrl, request, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            JsonNode responseNode = mapper.readTree(responseEntity.getBody());
            String sqName = responseNode.get("data").get("name").asText();

            // Mettre à jour le statut du RFQ
            submitRFQ(response.getRfqName(), sid);

            return sqName;
        } else {
            throw new RuntimeException("Erreur lors de la création du devis fournisseur: " +
                    responseEntity.getBody());
        }
    }

    private void submitRFQ(String rfqName, String sid) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // D'abord récupérer le document existant
        String getUrl = erpnextUrl + "/api/resource/Request for Quotation/" +
                URLEncoder.encode(rfqName, StandardCharsets.UTF_8);

        HttpEntity<String> getRequest = new HttpEntity<>(headers);
        ResponseEntity<String> getResponse = restTemplate.exchange(
                getUrl, HttpMethod.GET, getRequest, String.class);

        if (!getResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Échec de la récupération du RFQ: " + getResponse.getBody());
        }

        // Modifier les données nécessaires pour la soumission
        ObjectMapper mapper = new ObjectMapper();
        JsonNode existingData = mapper.readTree(getResponse.getBody()).get("data");
        ObjectNode payload = (ObjectNode) existingData;

        // Champs requis pour la soumission
        payload.put("docstatus", 1); // 1 = Submitted
        payload.put("status", "Submitted");

        // Envoyer la mise à jour
        String putUrl = erpnextUrl + "/api/resource/Request for Quotation/" +
                URLEncoder.encode(rfqName, StandardCharsets.UTF_8);

        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(payload), headers);
        ResponseEntity<String> response = restTemplate.exchange(
                putUrl, HttpMethod.PUT, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Échec de la soumission du RFQ: " + response.getBody());
        }
    }

}