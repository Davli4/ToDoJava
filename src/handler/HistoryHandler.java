package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends  BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException{
        try{
            if(exchange.getRequestMethod().equalsIgnoreCase("GET")){
                String response = gson.toJson(taskManager.getHistory());
                sendSuccess(exchange,response);
            }else{
                sendNotfound(exchange,"Method Not Found");
            }
        }catch (Exception e){
            sendInternalError(exchange,"Invalid server errior" + e.getMessage());
        }
    }
}
