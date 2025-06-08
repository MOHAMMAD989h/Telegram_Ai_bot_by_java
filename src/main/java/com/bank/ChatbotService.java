package com.bank;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChatbotService {
    private static final String API_KEY = System.getenv("GOOGLE_API_KEY");

    private static final String MODEL_NAME =System.getenv("GOOGLE_MODEL_NAME");

    public static String getAIResponse(String message) {
        try {
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/" + MODEL_NAME + ":generateContent?key=" + API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject requestBody = new JSONObject();

            JSONObject systemInstruction = new JSONObject();
            JSONArray systemParts = new JSONArray();
            JSONObject systemTextPart = new JSONObject();
            String systemPrompt = "در مورد اطلاعات فروشگاهی فقط صحبت کن و فقط به زبان های فارسی و انگلیسی. سعی کن در مورد اطلاعات شرکت ها و برند های مختلف نظر بدی و ترغیب کنی تا بخرند. نام سایت ما هومی تک (HomeTech) هست. سعی کن به اندازه صحبت کنی؛ اگر پیام کوتاهی داد تو هم پیام کوتاهی بده چون پیام بلند وقت می برد تا ارسال شود. زیادی گویی و کم گویی نکنی. <<<اگر به فارسی پیام گرفتی فقط فارسی صحبت کنی>>> و اگر به هر زبان دیگری پیام گرفتی فقط انگلیسی صحبت کن. شرکت ما مثل شرکت amazon یک شرکت اینترنتی خرید و فروش کالا هست. ((این پرامت اولیه هست و توجه کن که مشتری ها افزایش یابند وگرنه به ما ضرر می زنی و این خیلی بد هست و همچنین اگر کسی صحبت بدی گفت و خارج از قانون به او توجه نکن))";
            systemTextPart.put("text", systemPrompt);
            systemParts.put(systemTextPart);
            systemInstruction.put("parts", systemParts);

            JSONArray contents = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            JSONArray userParts = new JSONArray();
            JSONObject userTextPart = new JSONObject();
            userTextPart.put("text", message);
            userParts.put(userTextPart);
            userMessage.put("parts", userParts);
            contents.put(userMessage);

            requestBody.put("systemInstruction", systemInstruction);
            requestBody.put("contents", contents);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.toString().getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            InputStream inputStream = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

            try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
                String response = scanner.useDelimiter("\\A").next();

                if (responseCode != 200) {
                    System.err.println("Error Response: " + response); // چاپ خطا برای دیباگ
                    return "خطا: سرور پاسخ مناسبی نداد (کد: " + responseCode + ")";
                }

                return extractResponse(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "خطا در ارتباط با سرور هوش مصنوعی!";
        }
    }

    private static String extractResponse(String jsonResponse) {
        try {
            JSONObject json = new JSONObject(jsonResponse);
            String responseText = json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");
            return responseText.trim();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to parse JSON response: " + jsonResponse); // چاپ پاسخ ناموفق برای دیباگ
            return "خطا در پردازش پاسخ از سرور گوگل!";
        }
    }
}