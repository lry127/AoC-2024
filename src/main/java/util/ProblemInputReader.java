package util;

import java.io.InputStream;
import java.util.Scanner;

public class ProblemInputReader {
    public static Scanner getInput(String day, String problemSet) {
        String fullPath = STR."/\{day}/\{problemSet}";
        InputStream is = ProblemInputReader.class.getResourceAsStream(fullPath);
        if (is == null) {
            throw new IllegalArgumentException(STR."can't find required input data: \{fullPath}. is it added to resources?");
        }
        return new Scanner(is);
    }
}
