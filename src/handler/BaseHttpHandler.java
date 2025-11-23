package handler;

import adapters.DurationTypeAdapter;
import adapters.LocalDateTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {
    protected final Gson gson;

    public BaseHttpHandler() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, text.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    protected void sendSuccess(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 200);
    }

    protected void sendCreated(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 201);
    }

    protected void sendNotfound(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 404);
    }

    protected void sendIntersection(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 406);
    }

    protected void sendInternalError(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 500);
    }

    protected void sendBadRequest(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 400);
    }

    protected String getPathParameters(String path, int index) {
        String[] params = path.split("/");
        if (params.length > index) {
            return params[index];
        } else {
            return null;
        }
    }

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
