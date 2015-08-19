package chapter3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileProcessor {
    @FunctionalInterface
    interface BufferedReaderProcessor {
        String process(BufferedReader br) throws IOException;
    }

    public static String processFile(String filePath,
                                     BufferedReaderProcessor processor) {
        String result = "default value";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            result = processor.process(br);
        } catch (IOException e){
            // handle e;
        }

        return "";
    }

    public static void apiUsage() {
        String filePath = "./data.txt";

        String oneLine = processFile(
                filePath,
                (BufferedReader br) -> br.readLine() + br.readLine());
    }
}
