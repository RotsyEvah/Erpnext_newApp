package myapp.erpnewapp.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class ErpNextService {
    private final String ERPNext_URL = "http://erpnext.localhost:8000";

    public boolean authenticate(String username, String password) {
        try {
            // 1. Configurez le RestTemplate avec un timeout
            RestTemplate restTemplate = new RestTemplate();

            // 2. Configurez les headers nécessaires
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.set("X-Frappe-Site-Name", "erpnext.localhost"); // Important pour le multisite

            // 3. Corps de la requête
            Map<String, String> body = Map.of(
                    "usr", username,
                    "pwd", password
            );

            // 4. Envoyez la requête
            ResponseEntity<String> response = restTemplate.exchange(
                    ERPNext_URL + "/api/method/login",
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    String.class
            );

            // 5. Logs de débogage
            System.out.println("ERPNext Response Status: " + response.getStatusCode());
            System.out.println("ERPNext Response Body: " + response.getBody());

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("ERPNext Authentication Error:");
            e.printStackTrace();
            return false;
        }
    }
}