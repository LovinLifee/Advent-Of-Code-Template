package org.togetherjava.aoc.api;

import lombok.RequiredArgsConstructor;
import org.togetherjava.aoc.Aoc;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class HttpUtils {

    private static final HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

    public static HttpResponse<Stream<String>> sendFormData(URI URI, Map<Object, Object> data) {
        HttpRequest request = getAuthenticatedRequest(URI)
                .POST(HttpUtils.ofFormData(data))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofLines());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HttpResponse<Stream<String>> getEndpoint(final URI URI) {
        HttpRequest request = getAuthenticatedRequest(URI).build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofLines());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HttpRequest.Builder getAuthenticatedRequest(final URI URI) {
        return HttpRequest.newBuilder().uri(URI).timeout(Duration.ofSeconds(3))
                .headers("User-Agent", "https://github.com/LovinLifee/Advent-Of-Code-Template")
                .headers("Cookie", "session=" + Aoc.getInstance().getConfig().getSessionCookie());
    }

    private static HttpRequest.BodyPublisher ofFormData(Map<Object, Object> data) {
        StringBuilder result = new StringBuilder();
        data.entrySet().forEach(o -> {
            Object key = o.getKey();
            Object value = o.getValue();
            if (!result.isEmpty()) {
                result.append("&");
            }
            String encodedName = URLEncoder.encode(key.toString(), StandardCharsets.UTF_8);
            String encodedValue = URLEncoder.encode(value.toString(), StandardCharsets.UTF_8);
            result.append(encodedName);
            if (encodedValue != null) {
                result.append("=");
                result.append(encodedValue);
            }
        });
        return HttpRequest.BodyPublishers.ofString(result.toString());
    }
}
