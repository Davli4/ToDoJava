package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;
import java.util.logging.Handler;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager  taskManager;
    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange  exchange) throws IOException{
        try{
            String method = exchange.getRequestMethod();
            String path =  exchange.getRequestURI().getPath();

            switch(method){
                case "GET":
                    handleGetRequest(exchange,path);
                    break;
                case "POST":
                    handlePostRequest(exchange,path);
                    break;
                case "DELETE":
                    handleDeleteRequest(exchange,path);
                    break;
                default:
                    sendNotfound(exchange, "No Method Found");
                    break;
            }
        }catch (Exception e){
            sendInternalError(exchange,"Invalid server error" + e.getMessage());
        }
    }

    private void handleGetRequest(HttpExchange exchange, String path) throws IOException{
        String epicId = getPathParameters(path, 2);

        if(epicId == null){
            String response = gson.toJson(taskManager.getAllEpics());
            sendSuccess(exchange,response);
        }else{
            try{
                int id =  Integer.parseInt(epicId);
                Epic epic = taskManager.getEpicById(id);
                String response = gson.toJson(epic);
                sendSuccess(exchange,response);
            }catch (NumberFormatException e){
                sendBadRequest(exchange, "Invalid epic id");
            }
        }
    }

    private void handlePostRequest(HttpExchange exchange, String path) throws IOException{
        String requestBody = readRequestBody(exchange);
        Epic epic = gson.fromJson(requestBody, Epic.class);

        try{
            if(epic.getId() == 0){
                taskManager.addEpic(epic);
                String response = gson.toJson(epic);
                sendCreated(exchange,response);
            }else{
                taskManager.updateEpic(epic);
                sendSuccess(exchange,"Epic updated");
            }
        }catch (IllegalArgumentException e){
            sendBadRequest(exchange, e.getMessage());
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String path) throws IOException{
            String epicId = getPathParameters(path, 2);

            if(epicId == null){
                taskManager.removeAllEpics();
                sendSuccess(exchange,"Epic deleted");
            }else{
                try{
                    int id = Integer.parseInt(epicId);
                    taskManager.removeEpicById(id);
                    sendSuccess(exchange,"Epic deleted");
                }catch (NumberFormatException e){
                    sendIntersection(exchange, "Invalid epic id");
                }
            }
    }

}
