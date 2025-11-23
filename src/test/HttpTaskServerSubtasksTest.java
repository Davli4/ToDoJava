package test;

import manager.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.SubTask;
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

class HttpTaskServerSubtasksTest {
    private TaskManager taskManager;
    private HttpTaskServer httpTaskServer;
    private HttpClient httpClient;
    private Epic epic;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
        httpClient = HttpClient.newHttpClient();

        epic = new Epic("Parent Epic", "Epic for subtasks");
        taskManager.addEpic(epic);
    }

    @AfterEach
    void tearDown() throws IOException {
        httpTaskServer.stop();
    }

    @Test
    void testAddSubtask() throws IOException, InterruptedException {
        String subtaskJson = """
                {
                    "name": "Test Subtask",
                    "description": "Test Subtask Description",
                    "status": "NEW",
                    "epicId": %d
                }
                """.formatted(epic.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getAllSubTasks().size());
        assertEquals("Test Subtask", taskManager.getAllSubTasks().get(0).getName());
    }

    @Test
    void testGetSubtask() throws IOException, InterruptedException {
        SubTask subtask = new SubTask("Test Subtask", "Test Subtask Description", epic.getId());
        taskManager.addSubTask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtask.getId()))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"id\":" + subtask.getId()));
        assertTrue(response.body().contains("Test Subtask"));
    }

    @Test
    void testGetEpicSubtasks() throws IOException, InterruptedException {
        SubTask subtask = new SubTask("Test Subtask", "Test Subtask Description", epic.getId());
        taskManager.addSubTask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }
}