package com.example.demo.controller;

import com.example.demo.service.TossService;
import com.example.demo.service.PaymentService;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;

@Controller
public class WidgetController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TossService tossService;
    private final PaymentService paymentService;
    private final UserService userService;

    // 생성자 주입
    public WidgetController(TossService tossService, PaymentService paymentService, UserService userService) {
        this.tossService = tossService;
        this.paymentService = paymentService;
        this.userService = userService;
    }

    // 결제 확인
    @PostMapping("/confirm")
    public ResponseEntity<JSONObject> confirmPayment(@RequestBody String jsonBody, HttpServletRequest request) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject responseJson = new JSONObject();

        try {
            // 받은 JSON 데이터 파싱
            JSONObject requestData = (JSONObject) parser.parse(jsonBody);
            String paymentKey = (String) requestData.get("paymentKey");
            String orderId = (String) requestData.get("orderId");
            int amount = Integer.parseInt(requestData.get("amount").toString()); // 금액 변환
            int totalPrice = Integer.parseInt(requestData.get("totalPrice").toString()); // totalPrice
            long totalQuantity = Long.parseLong(requestData.get("totalQuantity").toString()); // totalQuantity

            logger.info("Payment Confirmed! Total Price: {}, Total Quantity: {}", totalPrice, totalQuantity);

            // Toss API 호출
            JSONObject tossRequest = new JSONObject();
            tossRequest.put("orderId", orderId);
            tossRequest.put("amount", amount);
            tossRequest.put("paymentKey", paymentKey);

            String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
            String authorization = "Basic " + Base64.getEncoder().encodeToString((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));

            URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", authorization);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(tossRequest.toString().getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            InputStream responseStream = responseCode == 200 ? connection.getInputStream() : connection.getErrorStream();
            try (Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
                responseJson = (JSONObject) parser.parse(reader);
            }

            logger.info("Response from Toss Payments: {}", responseJson);

            // 결제 저장
            String signupId = (String) request.getSession().getAttribute("signupId");
            if (signupId == null) signupId = "UNKNOWN_USER"; // 기본값

            String paymentMethod = (String) responseJson.getOrDefault("method", "UNKNOWN");
            int totalAmount = ((Number) responseJson.getOrDefault("totalAmount", 0)).intValue();
            Timestamp paymentTime = Timestamp.from(Instant.now());

            User user = userService.getUserBySignupId(signupId);

            paymentService.webPaymentInfo(signupId, totalPrice, totalQuantity, "success");
            tossService.savePaymentInfo(paymentMethod, "KRW", "KR", totalAmount, "0", paymentTime, signupId);

        } catch (Exception e) {
            logger.error("Failed to process payment confirmation: {}", jsonBody, e);
            responseJson.put("status", "fail");
            responseJson.put("message", e.getMessage());
            throw new RuntimeException("Payment confirmation failed", e);
        }

        return ResponseEntity.ok(responseJson);
    }

    // 결제 성공 처리
    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String paymentSuccess(HttpServletRequest request, Model model) {
        return "success";
    }

    // 결제 실패 처리
    @RequestMapping(value = "/fail", method = RequestMethod.GET)
    public String failPayment(HttpServletRequest request, Model model) {
        model.addAttribute("code", request.getParameter("code"));
        model.addAttribute("message", request.getParameter("message"));
        return "fail";
    }

    // 결제 준비
    @RequestMapping(value = "/widget/checkout", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> prepareCheckout() {
        JSONObject response = new JSONObject();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    // 결제 페이지
    @RequestMapping(value = "/checkout", method = RequestMethod.GET)
    public String checkoutPage() {
        return "checkout";
    }
}
