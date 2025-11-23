package manager;

import com.sun.net.httpserver.HttpServer;
import handler.*;
import model.Task;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final static int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this.taskManager = Managers.getDefault();
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        configureHandlers();
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        configureHandlers();
    }

    private void configureHandlers() {
        server.createContext("/tasks", new TaskHandler(this.taskManager));
        server.createContext("/epics", new EpicHandler(this.taskManager));
        server.createContext("/subtasks", new SubTaskHandler(this.taskManager));
        server.createContext("/history", new HistoryHandler(this.taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(this.taskManager));
    }


    public void start() throws IOException {
        server.start();
        System.out.println("Server started on port " + PORT);
    }

    public void stop() throws IOException {
        server.stop(0);
        System.out.println("Server stopped");
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer taskServer = new HttpTaskServer();
            taskServer.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
