import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(64);

        server.addHandler("GET", "/spring.svg", (request, out) -> {
            try {
                var filePath = Path.of(".", "public", request.getPath());
                var mimeType = Files.probeContentType(filePath);
                System.out.println("spring.svg: " + request.getPath());
                if (request.getQueryParams() != null) {
                    request.getQueryParams().forEach((key, value) -> System.out.println(key + ":" + value));
                }

                final var template = Files.readString(filePath);
                final var content = template.replace("{time}",
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                        .getBytes();
                server.outWrite(mimeType, content, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        server.addHandler("POST", "events.html", (request, out) -> {
            try {
                final var filePath = Path.of(".", "public", request.getPath());
                final var mimeType = Files.probeContentType(filePath);
                System.out.println("events.html: " + request.getPath());
                if (request.getQueryParams() != null) {
                    request.getQueryParams().forEach((key, value) -> System.out.println(key + ":" + value));
                }
                final var content = Files.readAllBytes(filePath);
                server.outWrite(mimeType, content, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        server.start(9999);
    }
}