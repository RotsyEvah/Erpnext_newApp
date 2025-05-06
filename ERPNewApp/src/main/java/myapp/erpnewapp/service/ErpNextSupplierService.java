package myapp.erpnewapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import myapp.erpnewapp.model.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ErpNextSupplierService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String erpnextUrl = "http://erpnext.localhost:8000";

    private ObjectMapper objectMapper = new ObjectMapper();


    public List<Supplier> getSuppliers(String sid) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String resource = "Supplier";
        String fieldsParam = "[\"*\"]";
        String url = erpnextUrl + "/api/resource/" + resource + "?fields=" + fieldsParam;

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        System.out.println("Body " + response.getBody());
        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode dataNode = root.get("data");

            List<Supplier> suppliers = new ArrayList<>();
            for (JsonNode node : dataNode) {
                Supplier supplier = new Supplier();
                supplier.setName(node.path("name").asText(null));
                supplier.setSupplierName(node.path("supplier_name").asText(null));
                supplier.setSupplierGroup(node.path("supplier_group").asText(null));
                supplier.setSupplierType(node.path("supplier_type").asText(null));
                supplier.setCountry(node.path("country").asText(null));
                supplier.setLanguage(node.path("language").asText(null));
                supplier.setEmailId(node.path("email_id").asText(null));
                supplier.setMobileNo(node.path("mobile_no").asText(null));
                supplier.setPrimaryAddress(node.path("primary_address").asText(null));

                suppliers.add(supplier);
            }
            return suppliers;
        } else {
            throw new Exception("Échec de la récupération des fournisseurs : " + response.getStatusCode());
        }
    }

    public Supplier getSupplierByName(String name, String sid) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = erpnextUrl + "/api/resource/Supplier/" + URLEncoder.encode(name, StandardCharsets.UTF_8);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode data = objectMapper.readTree(response.getBody()).get("data");
            Supplier supplier = new Supplier();
            supplier.setName(data.path("name").asText());
            supplier.setSupplierName(data.path("supplier_name").asText());
            // ... autres champs
            return supplier;
        } else {
            throw new Exception("Fournisseur non trouvé");
        }
    }


}


