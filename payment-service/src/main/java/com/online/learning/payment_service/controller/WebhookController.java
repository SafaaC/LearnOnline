import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper; 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.online.learning.payment_service.service.PaymentService;
import com.razorpay.Utils;


@RestController
@RequestMapping("/payment/webhook")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    public WebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,                                   // raw text, not parsed
            @RequestHeader("X-Razorpay-Signature") String signature) throws Exception {

        // 1. Is this really from Razorpay?
        boolean valid;
        try {
            valid = Utils.verifyWebhookSignature(payload, signature, webhookSecret);
        } catch (Exception e) {
            valid = false;
        }
        if (!valid) {
            log.warn("Webhook rejected: bad signature");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        // 2. Now it's safe to read
        JsonNode root = objectMapper.readTree(payload);
        String event = root.path("event").asText();

        if ("order.paid".equals(event)) {
            String orderId = root.path("payload").path("payment").path("entity").path("order_id").asText();
            paymentService.markOrderPaid(orderId);       // exception → 500 → Razorpay retries
            log.info("Order {} marked as paid", orderId);
        } else {
            log.info("Ignored webhook event {}", event);
        }
        return ResponseEntity.ok("OK");
    }
}