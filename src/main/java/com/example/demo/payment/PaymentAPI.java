/* 
package com.example.demo.payment;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import org.json.JSONObject;

public class PaymentAPI {
    public static void main(String[] args) {
        try {
            // 요청 본문 생성 (예시)
            String requestBody = "{"
                + "\"paymentKey\": \"your_payment_key\","
                + "\"orderId\": \"your_order_id\""
                + "}";

            // API 요청 보내기
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
                    .header("Authorization", "Bearer YOUR_ACCESS_TOKEN") // 여기에 실제 인증 토큰을 입력
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // HTTP 클라이언트 생성
            HttpClient client = HttpClient.newHttpClient();

            // API 응답 받기
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 응답 데이터 출력
            System.out.println("Response Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            // JSON 응답에서 필요한 값 추출 (예: orderName)
            JSONObject jsonResponse = new JSONObject(response.body());
            String orderName = jsonResponse.getString("orderName");
            System.out.println("Order Name: " + orderName);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

*/
