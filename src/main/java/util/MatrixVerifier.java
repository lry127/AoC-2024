package util;

import java.util.List;

public class MatrixVerifier {
    public static <T extends List<?>> void verify(List<T> matrix)  {
        if (matrix.isEmpty()) {
            throw new IllegalStateException("no row?");
        }
        int col = matrix.getFirst().size();
        for (int i = 1; i < matrix.size(); ++i) {
            int thisCol = matrix.get(i).size();
            if (thisCol != col) {
                throw new IllegalStateException(STR."row \{i} has size \{thisCol} but col 0 has size \{col}");
            }
        }
    }
}
