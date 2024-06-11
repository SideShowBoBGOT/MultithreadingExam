import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class MatrixServer {
    private static final int PORT = 12345;
    private static final String MATRIX_FILE = "/Users/kyryl/Desktop/MultithreadingExam/variant_11_sidak/matrix.txt";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                handleClient(socket);
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        ExecutorService computationPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try (BufferedReader reader = new BufferedReader(new FileReader(MATRIX_FILE));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            List<Callable<Double>> tasks = new ArrayList<>();
            for (String row : lines) {
                tasks.add(() -> {
                    double[] numbers = Arrays.stream(row.split(" "))
                            .mapToDouble(Double::parseDouble)
                            .toArray();
                    return Arrays.stream(numbers).average().orElse(0.0);
                });
            }

            List<Future<Double>> futures = computationPool.invokeAll(tasks);

            for (Future<Double> future : futures) {
                try {
                    Double average = future.get();
                    out.println(average);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException | InterruptedException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            computationPool.shutdown();
        }
    }
}
