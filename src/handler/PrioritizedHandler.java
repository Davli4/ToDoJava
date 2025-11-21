package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().equals("GET")) {
                String response = gson.toJson(taskManager.getPrioritizedTasks());
                sendSuccess(exchange, response);
            }
        }catch (Exception e){
            sendInternalError(exchange,"Invalid server error" + e.getMessage());
        }
    }
}
