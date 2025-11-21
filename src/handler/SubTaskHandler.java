package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.SubTask;

import java.io.IOException;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    public SubTaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException{
        try{
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
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
                    sendNotfound(exchange, "Not Method Found");
                    break;
            }
        }catch(Exception e){
            sendInternalError(exchange,"Invalid server error" + e.getMessage());
        }
    }

    private void handleGetRequest(HttpExchange exchange, String path) throws IOException{
        String subTaskId = getPathParameters(path,2);

        if(subTaskId==null){
            String response = gson.toJson(taskManager.getAllSubTasks());
            sendSuccess(exchange,response);
        }else{
            try{
                int subTaskIdInt = Integer.parseInt(subTaskId);
                SubTask subTask = taskManager.getSubTaskById(subTaskIdInt);
                String response = gson.toJson(subTask);
                sendSuccess(exchange,response);
            }catch (NumberFormatException e){
                sendIntersection(exchange,path);
            }
        }
    }

    private void handlePostRequest(HttpExchange exchange, String path) throws IOException{
        String requestBody = readRequestBody(exchange);
        SubTask subTask = gson.fromJson(requestBody,SubTask.class);

        try {
            if(subTask.getId() == 0 || taskManager.getSubTaskById(subTask.getId()) == null){
                taskManager.addSubTask(subTask);
                String response = gson.toJson(subTask);
                sendCreated(exchange,response);
            }else{
                taskManager.updateSubTask(subTask);
                sendSuccess(exchange, "SubTask Updated");
            }
        }catch (NumberFormatException e){
            sendBadRequest(exchange,path);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String path) throws IOException{
        String subTaskId = getPathParameters(path,2);

        if(subTaskId ==null){
            taskManager.removeAllSubTasks();
            sendSuccess(exchange,"All SubTasks Deleted");
        }else{
            try{
                int subTaskIdInt = Integer.parseInt(subTaskId);
                taskManager.removeSubTaskById(subTaskIdInt);
                sendSuccess(exchange,"SubTask Deleted");
            }catch (NumberFormatException e){
                sendBadRequest(exchange,"Invalid SubTask Id");
            }
        }
    }
}
