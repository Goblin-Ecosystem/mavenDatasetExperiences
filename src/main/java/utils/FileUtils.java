package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class FileUtils {

    public static void exportMapToCSV(Map<String, Integer> map, String header, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append(header+"\n");

            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                writer.append(entry.getKey())
                        .append(",")
                        .append(String.valueOf(entry.getValue()))
                        .append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
