package managers.servers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import managers.taskmanager.InMemoryTaskManager;
import managers.taskmanager.TaskManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskServerTest {
    static KVServer kvServer;
    static HttpTaskServer taskServer;

    @BeforeAll
    static void startServers() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskServer = new HttpTaskServer(new InetSocketAddress(8080));
            taskServer.start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void stopServers() {
        kvServer.stop(1);
        taskServer.stop(1);
    }

    @BeforeEach
    void resetServerData() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            url = URI.create("http://localhost:8080/tasks/epic/");
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            url = URI.create("http://localhost:8080/tasks/subtask/");
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getTasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Gson gson = new Gson();
        Task task = new Task("title", "description", Status.NEW, 15,
                LocalDateTime.of(2022, 11, 2, 3, 15));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(1, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getEpicsTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Gson gson = new Gson();
        EpicTask task = new EpicTask("title", "description");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(1, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getSubtasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Gson gson = new Gson();
        EpicTask epic = new EpicTask("title", "description");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "запись Эпика через POST запрос");
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                Subtask subtask = new Subtask("sub title", "sub description", Status.DONE, 15,
                        LocalDateTime.of(2022, 11, 3, 3, 15), epicId);
                url = URI.create("http://localhost:8080/tasks/subtask/");

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
                assertEquals(1, arrayTasks.size());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getTaskTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Gson gson = new Gson();
        Task task = new Task("title", "description", Status.NEW, 15,
                LocalDateTime.of(2022, 10, 1, 3, 30));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "запись через POST запрос");
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                task.setId(id);
                url = URI.create("http://localhost:8080/tasks/task/?id=" + id);
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Task responseTask = gson.fromJson(response.body(), Task.class);
                assertEquals(task, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getEpicTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Gson gson = new Gson();
        EpicTask task = new EpicTask("title", "description");
        task.setStatus(Status.NEW);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "запись через POST запрос");
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                task.setId(id);
                url = URI.create("http://localhost:8080/tasks/epic/?id=" + id);
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                EpicTask responseTask = gson.fromJson(response.body(), EpicTask.class);
                assertEquals(task, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    void getSubtaskTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Gson gson = new Gson();
        EpicTask epic = new EpicTask("title", "description");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "запись Эпика через POST запрос");
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                Subtask subtask = new Subtask("sub title", "sub description", Status.DONE, 15,
                        LocalDateTime.of(2022, 11, 4, 3, 15), epicId);
                url = URI.create("http://localhost:8080/tasks/subtask/");

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(201, postResponse.statusCode(), "запись сабтаска через POST запрос");
                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body());
                    subtask.setId(id);
                    url = URI.create("http://localhost:8080/tasks/subtask/?id=" + id);
                    request = HttpRequest.newBuilder().uri(url).GET().build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(200, response.statusCode());
                    Subtask responseTask = gson.fromJson(response.body(), Subtask.class);
                    assertEquals(subtask, responseTask);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void postUpdateTaskTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Gson gson = new Gson();
        Task task = new Task("title", "description", Status.NEW, 15,
                LocalDateTime.of(2022, 11, 5, 3, 15));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                task.setId(id);
                task.setTitle("new title");
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                        .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());

                url = URI.create("http://localhost:8080/tasks/task/?id=" + id);
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Task responseTask = gson.fromJson(response.body(), Task.class);
                assertEquals(task, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void postUpdateEpicTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Gson gson = new Gson();
        EpicTask task = new EpicTask("title", "description");
        task.setStatus(Status.NEW);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                task.setId(id);
                task.setTitle("new epic title");
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                        .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());

                url = URI.create("http://localhost:8080/tasks/epic/?id=" + id);
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                EpicTask responseTask = gson.fromJson(response.body(), EpicTask.class);
                assertEquals(task, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void postUpdateSubtaskTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Gson gson = new Gson();
        EpicTask epic = new EpicTask("title", "description");
        epic.setStatus(Status.NEW);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "запись Эпика через POST запрос");
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                Subtask subtask = new Subtask("sub title", "sub description", Status.DONE, 15,
                        LocalDateTime.of(2022, 11, 6, 3, 15), epicId);
                url = URI.create("http://localhost:8080/tasks/subtask/");

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body());
                    subtask.setId(id);
                    subtask.setTitle("new sub title");
                    request = HttpRequest.newBuilder()
                            .uri(url)
                            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                            .build();
                    client.send(request, HttpResponse.BodyHandlers.ofString());

                    url = URI.create("http://localhost:8080/tasks/subtask/?id=" + id);
                    request = HttpRequest.newBuilder().uri(url).GET().build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(200, response.statusCode());
                    Subtask responseTask = gson.fromJson(response.body(), Subtask.class);
                    assertEquals(subtask, responseTask);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteTasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Gson gson = new Gson();
        Task task = new Task("title", "description", Status.NEW, 15,
                LocalDateTime.of(2022, 11, 7, 3, 15));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(0, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteEpicsTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Gson gson = new Gson();
        EpicTask task = new EpicTask("title", "description");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(0, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteSubtasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Gson gson = new Gson();
        EpicTask epic = new EpicTask("title", "description");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "запись Эпика через POST запрос");
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                Subtask subtask = new Subtask("sub title", "sub description", Status.DONE, 15,
                        LocalDateTime.of(2022, 11, 8, 3, 15), epicId);
                url = URI.create("http://localhost:8080/tasks/subtask/");

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                request = HttpRequest.newBuilder().uri(url).DELETE().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(204, response.statusCode());
                request = HttpRequest.newBuilder().uri(url).GET().build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
                assertEquals(0, arrayTasks.size());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteTaskTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Gson gson = new Gson();
        Task task = new Task("title", "description", Status.NEW, 15,
                LocalDateTime.of(2022, 11, 9, 3, 15));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            int id = Integer.parseInt(postResponse.body());
            url = URI.create("http://localhost:8080/tasks/task/?id=" + id);
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());

            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("Задача с данным id не найдена", response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteEpicTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Gson gson = new Gson();
        EpicTask task = new EpicTask("title", "description");
        task.setStatus(Status.NEW);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "запись через POST запрос");
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                task.setId(id);
                url = URI.create("http://localhost:8080/tasks/epic/?id=" + id);
                request = HttpRequest.newBuilder().uri(url).DELETE().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(204, response.statusCode());

                request = HttpRequest.newBuilder().uri(url).GET().build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals("Эпик с данным id не найден", response.body());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    void deleteSubtaskTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Gson gson = new Gson();
        EpicTask epic = new EpicTask("title", "description");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "запись Эпика через POST запрос");
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                Subtask subtask = new Subtask("sub title", "sub description", Status.DONE, 15,
                        LocalDateTime.of(2022, 11, 10, 3, 15), epicId);
                url = URI.create("http://localhost:8080/tasks/subtask/");

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(201, postResponse.statusCode(), "запись сабтаска через POST запрос");
                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body());
                    subtask.setId(id);
                    url = URI.create("http://localhost:8080/tasks/subtask/?id=" + id);
                    request = HttpRequest.newBuilder().uri(url).DELETE().build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(204, response.statusCode());

                    request = HttpRequest.newBuilder().uri(url).GET().build();
                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals("Подзадача с данным id не найдена", response.body());
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getEpicSubtasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Gson gson = new Gson();
        EpicTask epic = new EpicTask("title", "description");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "запись Эпика через POST запрос");
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                Subtask subtask = new Subtask("sub title 1", "sub description", Status.DONE, 15,
                        LocalDateTime.of(2022, 11, 11, 3, 15), epicId);
                Subtask subtask2 = new Subtask("sub title 2", "sub description", Status.IN_PROGRESS,
                        15, LocalDateTime.of(2022, 11, 12, 3, 15), epicId);
                url = URI.create("http://localhost:8080/tasks/subtask/");
                List<Subtask> subtasks = new ArrayList<>();
                subtasks.add(subtask);
                subtasks.add(subtask2);
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
                subtask.setId(Integer.parseInt(postResponse.body()));
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
                subtask2.setId(Integer.parseInt(postResponse.body()));
                url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=" + epicId);
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                assertEquals(gson.toJson(subtasks), response.body());

            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getHistoryTest() {
        Task task1 = new Task("task1", "good description", Status.NEW, 15, null);
        Task task2 = new Task("task2", "very good description", Status.IN_PROGRESS, 30,
                LocalDateTime.of(2022, 10, 20, 15, 20));
        EpicTask epic = new EpicTask("Epic1", "very epic description");

        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        try {
            List<Integer> expectedHistory = new ArrayList<>();
            URI url = URI.create("http://localhost:8080/tasks/task/");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int id = Integer.parseInt(response.body());
            task1.setId(id);
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            id = Integer.parseInt(response.body());
            expectedHistory.add(id);
            task2.setId(id);

            url = URI.create("http://localhost:8080/tasks/epic/");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            id = Integer.parseInt(response.body());
            epic.setId(id);

            Subtask sub1 = new Subtask("Sub1", "desc1", Status.NEW, 15,
                    LocalDateTime.of(2023, 1, 13, 8, 0), id);
            Subtask sub2 = new Subtask("Sub2", "desc2", Status.IN_PROGRESS, 45,
                    LocalDateTime.of(2022, 11, 12, 15, 30), id);
            Subtask sub3 = new Subtask("Sub3", "desc3", Status.IN_PROGRESS, 120,
                    LocalDateTime.of(2022, 11, 12, 14, 30), id);

            url = URI.create("http://localhost:8080/tasks/subtask/");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(sub1)))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            id = Integer.parseInt(response.body());
            expectedHistory.add(id);
            sub1.setId(id);

            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(sub2)))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            id = Integer.parseInt(response.body());
            sub2.setId(id);

            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(sub3)))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            id = Integer.parseInt(response.body());
            sub3.setId(id);

            url = URI.create("http://localhost:8080/tasks/task/?id=" + expectedHistory.get(0));
            request = HttpRequest.newBuilder().uri(url).GET().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            url = URI.create("http://localhost:8080/tasks/subtask/?id=" + expectedHistory.get(1));
            request = HttpRequest.newBuilder().uri(url).GET().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

            url = URI.create("http://localhost:8080/tasks/history/");
            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            JsonArray jsonArrayHistory = JsonParser.parseString(response.body()).getAsJsonArray();
            List<Integer> responseHistory = StreamSupport.stream(jsonArrayHistory.spliterator(), true)
                    .map(reader -> gson.fromJson(reader, Task.class))
                    .map(Task::getId)
                    .collect(Collectors.toList());
            assertEquals(expectedHistory, responseHistory);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getSortedTasksTest() {
        TaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("task1", "good description", Status.NEW, 15, null);
        Task task2 = new Task("task2", "very good description", Status.IN_PROGRESS, 30,
                LocalDateTime.of(2022, 10, 20, 15, 20));
        EpicTask epic = new EpicTask("Epic1", "very epic description");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(epic);

        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        try {
            URI url = URI.create("http://localhost:8080/tasks/task/");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int id = Integer.parseInt(response.body());
            task1.setId(id);
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            id = Integer.parseInt(response.body());
            task2.setId(id);

            url = URI.create("http://localhost:8080/tasks/epic/");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            id = Integer.parseInt(response.body());
            epic.setId(id);

            Subtask sub1 = new Subtask("Sub1", "desc1", Status.NEW, 15,
                    LocalDateTime.of(2023, 1, 13, 8, 0), id);
            Subtask sub2 = new Subtask("Sub2", "desc2", Status.IN_PROGRESS, 45,
                    LocalDateTime.of(2022, 11, 12, 15, 30), id);
            Subtask sub3 = new Subtask("Sub3", "desc3", Status.IN_PROGRESS, 120,
                    LocalDateTime.of(2022, 11, 12, 14, 30), id);
            manager.addTask(sub1);
            manager.addTask(sub2);
            manager.addTask(sub3);

            url = URI.create("http://localhost:8080/tasks/subtask/");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(sub1)))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            id = Integer.parseInt(response.body());
            sub1.setId(id);

            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(sub2)))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            id = Integer.parseInt(response.body());
            sub2.setId(id);

            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(sub3)))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            id = Integer.parseInt(response.body());
            sub3.setId(id);

            url = URI.create("http://localhost:8080/tasks/");
            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonArray arraySortedTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            LocalDateTime lastStartTime = LocalDateTime.of(0, 1, 1, 0, 0);
            for (JsonElement jsonTask : arraySortedTasks) {
                Task task = gson.fromJson(jsonTask, Task.class);
                LocalDateTime startTime = task.getStartTime();
                if (startTime == null) {
                    continue;
                }
                assertTrue(lastStartTime.isBefore(startTime) || lastStartTime.isEqual(startTime));
                lastStartTime = startTime;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}