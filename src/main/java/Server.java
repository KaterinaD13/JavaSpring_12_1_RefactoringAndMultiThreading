import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    // final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private final static Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();
    ExecutorService executorService;

    public Server(int threadsSize) {
        executorService = Executors.newFixedThreadPool(threadsSize);
    }

    public void start(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                final var socket = serverSocket.accept();
                executorService.execute(() -> connect(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHandler(String method, String validPaths, Handler handler) {
        if (handlers.get(method) == null) {
            handlers.put(method, new ConcurrentHashMap<>());
        }
        handlers.get(method).put(validPaths.toString(), handler);
    }

    public void connect(Socket socket) {
        try (socket;
             final var in = socket.getInputStream();
             final var out = new BufferedOutputStream(socket.getOutputStream());) {

            var request = Request.getRequest(in, out);
            var pathHandlerMap = handlers.get(request.getMethod());
            if (pathHandlerMap == null) {
                out.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
                return;
            }
            var handler = pathHandlerMap.get(request.getPath());
            if (handler == null) {
                out.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
                return;
            }
            handler.handle(request, out);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    static void outWrite(String mimeType, byte[] content, BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + content.length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.write(content);
        out.flush();
    }
}