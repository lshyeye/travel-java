package com.example.traveljava.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class LLMUtils {
    private String apiKey;
    private String baseUrl;
    private String model;
    private OkHttpClient client;
    private ObjectMapper objectMapper = new ObjectMapper();


    public LLMUtils(String apiKey, String baseUrl, String model) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.model = model;

        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

//    调硅基流动 的接口，旅游推荐接口
    public String chat(String systemPrompt, String userPrompt) {
        String requestBody = buildRequestBody(systemPrompt, userPrompt, false);
//        System.out.println(requestBody);
//        创建 post 请求
        Request request =  new Request.Builder()
                .url(baseUrl + "/chat/completions")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(requestBody, MediaType.parse("application/json; charset=utf-8")))
                .build();

        try(Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                throw new IOException("LLM 调用异常："+ response.code());
//            }
//            System.out.println(response.body().string());
           String responseBody =  response.body().string();
            System.out.println(responseBody);
           return extractContent(responseBody);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 模型返回数据处理
    private String extractContent(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode choices = root.path("choices");
        if (choices.isArray() && choices.size() > 0) {
            return choices.get(0).path("message").path("content").asText();
        }
        return "";
    }

    public  String buildRequestBody(String systemPrompt, String userPrompt, Boolean stream) {
        StringBuilder str =  new StringBuilder();
        str.append("{");
        str.append("\"model\":\"" + model + "\",");
        str.append("\"stream\":" + stream + ",");
        str.append("\"messages\":[");
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            str.append("{\"role\":\"system\",\"content\":\"" + systemPrompt + "\"},");
        }
//        用户提示词
        str.append("{\"role\":\"user\",\"content\":\"" + escapeJson(userPrompt) + "\"}");
        str.append("],");
        str.append("\"temperature\":0.7");
        str.append("}");

        return str.toString();
    }
    // JSON字符串转义方法
    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")      // 转义反斜杠
                .replace("\"", "\\\"")      // 转义双引号
                .replace("\n", "\\n")       // 转义换行符
                .replace("\r", "\\r")       // 转义回车符
                .replace("\t", "\\t");      // 转义制表符
    }

    public String chatStream(String systemPrompt, String userPrompt, Consumer<String> callback) throws IOException {
        String requestBody = buildRequestBody(systemPrompt,userPrompt,true);
//        创建请求
        Request request = new Request.Builder()
                .url(baseUrl + "/chat/completions")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Accept", "text/event-stream")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json; charset=utf-8")))
                .build();
//        记录完整的内容
        StringBuilder fullContent = new StringBuilder();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("LLM 调用异常："+ response.code());
            }
//            读取流数据
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream(), java.nio.charset.StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
//                    data: 你好
//                    data：123
                    if (line.startsWith("data:")) {
//                        截取字符串
                        String data = line.substring("data: ".length());
                        if ("[DONE]".equals(data)) {
                            break;
                        }
                        String content = parseStreamContent(data);
                        if(content != null && !content.isEmpty())  {
                            fullContent.append(content);
                            if (callback != null) {
                                callback.accept(content);
                            }

                        }
                    }
                }
            }
        }
        return  fullContent.toString();
    }
    // 大模型返回对话流数据处理函数
    private String parseStreamContent(String data) {
        try {
            JsonNode root = objectMapper.readTree(data);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode delta = choices.get(0).path("delta");
                return delta.path("content").asText("");
            }
        } catch (Exception e) {
            System.out.println("解析流式数据失败: {}" + e.getMessage());
        }
        return null;
    }

}