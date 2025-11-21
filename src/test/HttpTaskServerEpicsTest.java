package test;

import manager.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerEpicsTest {
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
    void tearDown() throws  IOException {
        httpTaskServer.stop();
    }

    @Test
    void testAddEpic() throws IOException, InterruptedException {
        String epicJson = """
        {
            "name": "Test Epic",
            "description": "Test Epic Description"
        }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals("Test Epic", taskManager.getAllEpics().get(0).getName());
    }

    @Test
    void testGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Epic Description");
        taskManager.addEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epic.getId()))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"id\":" + epic.getId()));
        assertTrue(response.body().contains("Test Epic"));
    }

    @Test
    void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Epic Description");
        taskManager.addEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epic.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getAllEpics().isEmpty());
    }
}