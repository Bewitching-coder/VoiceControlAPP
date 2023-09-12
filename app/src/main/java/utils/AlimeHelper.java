package utils;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AlimeHelper {
    public static void sendMessageToAlime(String messageContent, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        // 构建请求体
        String requestBodyStr = "{\"messages\":[{\"content\":\"" + messageContent + "\",\"role\":\"user\"}]}";
        RequestBody requestBody = RequestBody.create(requestBodyStr, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("https://bjcn-api.apusai.com/ai/chat/")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Auth-Key", "ShznkDwvIcsrhUNT1BeGs8gT0mjZR5XIrSquWpfsjvLMwhvW")
                .build();

        client.newCall(request).enqueue(callback);
    }
}
