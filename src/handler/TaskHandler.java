package handler;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    handleGetRequest(exchange, path);
                    break;
                case "POST":
                    handlePostRequest(exchange);
                    break;
                case "DELETE":
                    handleDeleteRequest(exchange, path);
                    break;
                default:
                    sendNotfound(exchange, "No Method Found");
                    break;
            }
        } catch (Exception e) {
            sendInternalError(exchange, "Invalid server error" + e.getMessage());
        }
    }

    private void handleGetRequest(HttpExchange exchange, String path) throws IOException {
        String taskId = getPathParameters(path, 2);

        if (taskId == null) {
            String response = gson.toJson(taskManager.getAllTasks());
            sendSuccess(exchange, response);
        } else {
            try {
                int id = Integer.parseInt(taskId);
                Task newTask = taskManager.getTaskById(id);
                String response = gson.toJson(newTask);
                sendSuccess(exchange, response);
            } catch (NumberFormatException e) {
                sendIntersection(exchange, path);
            }
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String requestBody = readRequestBody(exchange);
        Task task = gson.fromJson(requestBody, Task.class);

        try {
            if (task.getId() == 0) {
                taskManager.addTask(task);
                String response = gson.toJson(task);
                sendCreated(exchange, response);
            } else {
                taskManager.updateTask(task);
                sendSuccess(exchange, "Task updated successfully");
            }
        } catch (NumberFormatException e) {
            sendIntersection(exchange, e.getMessage());
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String path) throws IOException {
        String taskID = getPathParameters(path, 2);

        if (taskID == null) {
            taskManager.removeAllTasks();
            sendSuccess(exchange, "Tasks deleted successfully");
        } else {
            try {
                int id = Integer.parseInt(taskID);
                taskManager.removeTaskById(id);
                sendSuccess(exchange, "Task removed successfully");
            } catch (NumberFormatException e) {
                sendIntersection(exchange, e.getMessage());
            }
        }
    }
}
