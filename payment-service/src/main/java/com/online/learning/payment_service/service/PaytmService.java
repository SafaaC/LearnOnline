package com.online.learning.payment_service.service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

public class PaytmService {
    private static final String PAYTM_MERCHANT_ID = "your_merchant_id";
    private static final String PAYTM_MERCHANT_KEY = "your_merchant_key";
    private static final String PAYTM_WEBSITE = "your_website";

    // Method to create payment request
    public String createPayment(String orderId, String amount) {
        String paytmParams = buildPaytmParams(orderId, amount);
        return sendPaytmRequest(paytmParams);
    }

    // Build the request parameters
    private String buildPaytmParams(String orderId, String amount) {
        // Construct the parameters as required by Paytm
        StringBuilder params = new StringBuilder();
        params.append("MID=" + PAYTM_MERCHANT_ID);
        params.append("&ORDER_ID=" + orderId);
        params.append("&TXN_AMOUNT=" + amount);
        params.append("&WEBSITE=" + PAYTM_WEBSITE);
        params.append("&CALLBACK_URL=http://localhost:8080/payment/callback");

        return params.toString();
    }

    // Send request to Paytm
    private String sendPaytmRequest(String params) {
        try {
            URL url = new URL("https://securegw-stage.paytm.in/order/process"); // For testing (use live URL for production)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = params.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            connection.getResponseCode();  // Get the response code (200 for success)
            return connection.getInputStream().toString(); // Return the Paytm response
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing payment.";
        }
    }
}