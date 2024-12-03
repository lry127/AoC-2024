package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ProblemInputReader {
    public static Scanner getInputAsScanner(String day, String problemSet) {
        InputStream is = getRawInput(day, problemSet);
        return new Scanner(is);
    }

    public static InputStream getRawInput(String day, String problemSet) {
        String fullPath = STR."/\{day}/\{problemSet}";
        InputStream is = ProblemInputReader.class.getResourceAsStream(fullPath);
        if (is == null) {
            throw new IllegalArgumentException(STR."can't find required input data: \{fullPath}. is it added to resources?");
        }
        return is;
    }

    public static byte[] readAllBytes(String day, String problemSet) {
        InputStream is = getRawInput(day, problemSet);
        try {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new IllegalArgumentException("failed to read input");
        }
    }
}
