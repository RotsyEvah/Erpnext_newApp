package myapp.erpnewapp.service;


import myapp.erpnewapp.model.ErpNextSessionInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ErpNextAuthService {

    private final String erpnextUrl = "http://erpnext.localhost:8000";

    private final RestTemplate restTemplate = new RestTemplate();

    public ErpNextSessionInfo loginAndGetSessionInfo(String username, String password) {
        String loginUrl = erpnextUrl + "/api/method/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("usr", username);
        formData.add("pwd", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                ErpNextSessionInfo sessionInfo = new ErpNextSessionInfo();

                List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
                if (cookies != null) {
                    for (String cookie : cookies) {
                        if (cookie.startsWith("sid=")) {
                            sessionInfo.setSid(getCookieValue(cookie));
                        } else if (cookie.startsWith("system_user=")) {
                            sessionInfo.setSystemUser(getCookieValue(cookie));
                        } else if (cookie.startsWith("full_name=")) {
                            sessionInfo.setFullName(getCookieValue(cookie));
                        } else if (cookie.startsWith("user_id=")) {
                            sessionInfo.setUserId(getCookieValue(cookie));
                        }
                    }
                }
                return sessionInfo;
            }
        } catch (Exception e) {
            System.out.println("Erreur de connexion ERPNext : " + e.getMessage());
        }
        return null;
    }

    private String getCookieValue(String cookie) {
        return cookie.split("=")[1].split(";")[0].trim();
    }
}

