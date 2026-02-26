package pharmacie.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pharmacie.service.ReapprovisionnementService;

@RestController
@RequestMapping("/api/reapprovisionnement")
public class ReapprovisionnementController {

    @Autowired
    private ReapprovisionnementService reapprovisionnementService;

    @PostMapping("/declencher")
    public ResponseEntity<Map<String, String>> declencherReapprovisionnement() {
        Map<String, String> response = new HashMap<>();
        try {
            reapprovisionnementService.processReapprovisionnement();
            response.put("message",
                    "Le processus de réapprovisionnement a été déclenché avec succès. Les e-mails ont été envoyés.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Erreur lors de l'envoi des e-mails : " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}