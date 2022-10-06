package managers.taskmanager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    final TaskManager manager;
    final HttpServer httpServer;

    public HttpTaskServer(InetSocketAddress port) throws IOException {
        this.manager = FileBackedTasksManager.loadFromFile(new File("resources/history.csv"));
        this.httpServer = HttpServer.create(port, 0);
        httpServer.createContext("/tasks/task/", new TaskHandler());
        httpServer.createContext("/tasks/epic/", new EpicHandler());
        httpServer.createContext("/tasks/subtask/", new SubtaskHandler());
        httpServer.createContext("/tasks/subtask/epic/", exchange -> {
            Gson gson = new Gson();
            String method = exchange.getRequestMethod();

            int rCode = 400;
            String response;

            if ("GET".equals(method)) {
                String query = exchange.getRequestURI().getQuery();
                try {
                    int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                    response = gson.toJson(manager.getSubtasksOfEpic(id));
                    rCode = 200;
                } catch (StringIndexOutOfBoundsException | NullPointerException e) {
                    response = "В запросе отсутствует необходимый параметр - id";
                } catch (NumberFormatException e) {
                    response = "Неверный формат id";
                }
            } else {
                response = "Некорректный запрос";
            }

            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(rCode, 0);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
        httpServer.createContext("/tasks/history/", exchange -> {
            Gson gson = new Gson();
            String method = exchange.getRequestMethod();

            int rCode = 400;
            String response;

            if ("GET".equals(method)) {
                rCode = 200;
                response = gson.toJson(manager.getHistory());
            } else {
                response = "Некорректный запрос";
            }

            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(rCode, 0);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
        httpServer.createContext("/tasks/", new TasksHandler());
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }

    class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Gson gson = new Gson();
            int rCode;
            String response;

            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    String query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        rCode = 200;
                        response = gson.toJson(manager.getTasks());
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                            Task requestedTask = manager.getTaskById(id);
                            if (requestedTask != null) {
                                response = gson.toJson(requestedTask);
                            } else {
                                response = "Задача с данным id не найдена";
                            }
                            rCode = 200;
                        } catch (StringIndexOutOfBoundsException e) {
                            rCode = 400;
                            response = "В запросе отсутствует необходимый параметр - id";
                        } catch (NumberFormatException e) {
                            rCode = 400;
                            response = "Неверный формат id";
                        }
                    }
                    break;
                case "POST":
                    String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        Task receivedTask = gson.fromJson(bodyRequest, Task.class);
                        int id = receivedTask.getId();
                        if (manager.getTaskById(id) != null) {
                            manager.updateTask(receivedTask);
                            rCode = 200;
                            response = "Задача с id=" + id + " обновлена";
                        }
                        else {
                            int createdId = manager.addTask(receivedTask);
                            rCode = 201;
                            response = "Создана задача с id=" + createdId;
                        }
                    } catch (JsonSyntaxException e) {
                        response = "Неверный формат запроса";
                        rCode = 400;
                    }
                    break;
                case "DELETE":
                    response = "";
                    query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        manager.removeAllTasks();
                        rCode = 204;
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                            manager.removeTaskById(id);
                            rCode = 204;
                        } catch (StringIndexOutOfBoundsException e) {
                            rCode = 400;
                            response = "В запросе отсутствует необходимый параметр - id";
                        } catch (NumberFormatException e) {
                            rCode = 400;
                            response = "Неверный формат id";
                        }
                    }
                    break;
                default:
                    rCode = 400;
                    response = "Некорректный запрос";
            }

            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(rCode, 0);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Gson gson = new Gson();
            int rCode;
            String response;

            String method = exchange.getRequestMethod();

            if (method.equals("GET")) {
                response = gson.toJson(manager.getPrioritizedTasks());
                rCode = 200;
            } else {
                rCode = 400;
                response = "Некорректный запрос";
            }

            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(rCode, 0);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    class EpicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Gson gson = new Gson();
            int rCode;
            String response;

            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    String query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        rCode = 200;
                        response = gson.toJson(manager.getEpicTasks());
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                            EpicTask requestedTask = manager.getEpicTaskById(id);
                            if (requestedTask != null) {
                                response = gson.toJson(requestedTask);
                            } else {
                                response = "Эпик с данным id не найден";
                            }
                            rCode = 200;
                        } catch (StringIndexOutOfBoundsException e) {
                            rCode = 400;
                            response = "В запросе отсутствует необходимый параметр - id";
                        } catch (NumberFormatException e) {
                            rCode = 400;
                            response = "Неверный формат id";
                        }
                    }
                    break;
                case "POST":
                    String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        EpicTask receivedTask = gson.fromJson(bodyRequest, EpicTask.class);
                        if (receivedTask.getSubtasks() == null) {
                            rCode = 400;
                            response = "для Эпика требуется передать параметр subtasks";
                        }
                        else {
                            int id = receivedTask.getId();
                            if (manager.getEpicTaskById(id) != null) {
                                manager.updateTask(receivedTask);
                                rCode = 200;
                                response = "Эпик с id=" + id + " обновлен";
                            } else {
                                int createdId = manager.addTask(receivedTask);
                                rCode = 201;
                                response = "Создан эпик с id=" + createdId;
                            }
                        }
                    } catch (JsonSyntaxException e) {
                        response = "Неверный формат запроса";
                        rCode = 400;
                    }
                    break;
                case "DELETE":
                    response = "";
                    query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        manager.removeAllEpicTasks();
                        rCode = 204;
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                            manager.removeTaskById(id);
                            rCode = 204;
                        } catch (StringIndexOutOfBoundsException e) {
                            rCode = 400;
                            response = "В запросе отсутствует необходимый параметр - id";
                        } catch (NumberFormatException e) {
                            rCode = 400;
                            response = "Неверный формат id";
                        }
                    }
                    break;
                default:
                    rCode = 400;
                    response = "Некорректный запрос";
            }

            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(rCode, 0);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    class SubtaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Gson gson = new Gson();
            int rCode;
            String response;

            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    String query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        rCode = 200;
                        response = gson.toJson(manager.getSubtasks());
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                            Subtask requestedTask = manager.getSubtaskById(id);
                            if (requestedTask != null) {
                                response = gson.toJson(requestedTask);
                            } else {
                                response = "Подзадача с данным id не найдена";
                            }
                            rCode = 200;
                        } catch (StringIndexOutOfBoundsException e) {
                            rCode = 400;
                            response = "В запросе отсутствует необходимый параметр - id";
                        } catch (NumberFormatException e) {
                            rCode = 400;
                            response = "Неверный формат id";
                        }
                    }
                    break;
                case "POST":
                    String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        Subtask receivedTask = gson.fromJson(bodyRequest, Subtask.class);
                        int id = receivedTask.getId();
                        if (manager.getSubtaskById(id) != null) {
                            manager.updateTask(receivedTask);
                            rCode = 200;
                            response = "Подзадача с id=" + id + " обновлена";
                        }
                        else {
                            int createdId = manager.addTask(receivedTask);
                            rCode = 201;
                            response = "Создана подзадача с id=" + createdId;
                        }
                    } catch (JsonSyntaxException e) {
                        response = "Неверный формат запроса";
                        rCode = 400;
                    }
                    break;
                case "DELETE":
                    response = "";
                    query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        manager.removeAllSubtasks();
                        rCode = 204;
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                            manager.removeTaskById(id);
                            rCode = 204;
                        } catch (StringIndexOutOfBoundsException e) {
                            rCode = 400;
                            response = "В запросе отсутствует необходимый параметр - id";
                        } catch (NumberFormatException e) {
                            rCode = 400;
                            response = "Неверный формат id";
                        }
                    }
                    break;
                default:
                    rCode = 400;
                    response = "Некорректный запрос";
            }

            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(rCode, 0);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
