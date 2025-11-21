package test;

import manager.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTasksTest {
    private TaskManager taskManager;
    private HttpTaskServer httpTaskServer;
    private HttpClient httpClient;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
        httpClient = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() throws IOException {
        httpTaskServer.stop();
    }

    @Test
    void testAddTask() throws IOException, InterruptedException {
        String taskJson = """
        {
            "name": "Test Task",
            "description": "Test Description", 
            "status": "NEW"
        }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals("Test Task", taskManager.getAllTasks().get(0).getName());
    }

    @Test
    void testGetTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW);
        taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"id\":" + task.getId()));
        assertTrue(response.body().contains("Test Task"));
        assertTrue(response.body().contains("Test Description"));
    }


    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Original Task", "Original Description", TaskStatus.NEW);
        taskManager.addTask(task);

        String updatedJson = """
        {
            "id": %d,
            "name": "Updated Task",
            "description": "Updated Description", 
            "status": "IN_PROGRESS"
        }
        """.formatted(task.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(updatedJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Updated Task", taskManager.getTaskById(task.getId()).getName());
        assertEquals("Updated Description", taskManager.getTaskById(task.getId()).getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW);
        taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.DONE);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(2, taskManager.getAllTasks().size());
        assertTrue(response.body().contains("Task 1"));
        assertTrue(response.body().contains("Task 2"));
    }
}