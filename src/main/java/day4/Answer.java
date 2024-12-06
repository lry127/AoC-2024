package day4;

import util.MatrixVerifier;
import util.ProblemInputReader;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Answer {
    private static class CharacterMatrix {
        private final List<List<Character>> matrix = new ArrayList<>();

        public void addRow(String rowData) {
            List<Character> row = new ArrayList<>();
            for (char c : rowData.toCharArray()) {
                row.add(c);
            }
            matrix.add(row);
        }

        public int getRowSize() {
            return matrix.size();
        }

        public int getColSize() {
            return matrix.getFirst().size();
        }

        public Character get(int row, int col) {
            if (row >= getRowSize() || col >= getColSize() || row < 0 || col < 0) {
                return null;
            }
            return matrix.get(row).get(col);
        }

        public Character get(Point point) {
            return get(point.x, point.y);
        }

        public void verify() {
            MatrixVerifier.verify(matrix);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(STR."idx\t\t: \{IntStream.range(0, getColSize()).boxed().toList()}\n");
            for (int i = 0; i < matrix.size(); ++i) {
                builder.append(STR."row \{i}\t: \{matrix.get(i)}").append("\n");
            }
            return builder.toString();
        }
    }

    private interface NextPosStrategy {
        Point getNext(Point current);
    }

    private static class HorizontalStrategy implements NextPosStrategy {

        @Override
        public Point getNext(Point current) {
            return new Point(current.x, current.y + 1);
        }

        @Override
        public String toString() {
            return "Horizontal";
        }
    }

    private static class VerticalStrategy implements NextPosStrategy {

        @Override
        public Point getNext(Point current) {
            return new Point(current.x + 1, current.y);
        }

        @Override
        public String toString() {
            return "Vertical";
        }
    }

    private static class RightBottomDiagonalStrategy implements NextPosStrategy {

        @Override
        public Point getNext(Point current) {
            return new Point(current.x + 1, current.y + 1);
        }

        @Override
        public String toString() {
            return "Right Bottom Diagonal";
        }
    }

    private static class LeftBottomDiagonalStrategy implements NextPosStrategy {

        @Override
        public Point getNext(Point current) {
            return new Point(current.x + 1, current.y - 1);
        }

        @Override
        public String toString() {
            return "Left Bottom Diagonal";
        }
    }

    public static void main() {
        CharacterMatrix matrix = readInput();
        System.err.println(STR."Question 1: \{solveProblem1(matrix)}");
        System.err.println(STR."Question 2: \{solveProblem2(matrix)}");
    }

    private static int solveProblem1(CharacterMatrix allChar) {
        int colSize = allChar.getColSize();
        int rowSize = allChar.getRowSize();
        int allMatches = 0;
        List<NextPosStrategy> allSearchStrategy = List.of(new HorizontalStrategy(),
                new VerticalStrategy(), new RightBottomDiagonalStrategy(), new LeftBottomDiagonalStrategy());

        for (int row = 0; row < rowSize; ++row) {
            for (int col = 0; col < colSize; ++col) {
                for (NextPosStrategy strategy : allSearchStrategy) {
                    if (isCorrectWord(allChar, strategy, new Point(row, col), "XMAS")) {
//                        System.err.println(STR."\{strategy} matches @ (\{row}, \{col})");
                        ++allMatches;
                    }
                }
            }
        }
        return allMatches;
    }

    private static int solveProblem2(CharacterMatrix allChar) {
        int colSize = allChar.getColSize();
        int rowSize = allChar.getRowSize();

        List<Point> leftBottomMatch = new ArrayList<>();
        List<Point> rightBottomMatch = new ArrayList<>();

        LeftBottomDiagonalStrategy leftBottomDiagonalStrategy = new LeftBottomDiagonalStrategy();
        RightBottomDiagonalStrategy rightBottomDiagonalStrategy = new RightBottomDiagonalStrategy();
        for (int row = 0; row < rowSize; ++row) {
            for (int col = 0; col < colSize; ++col) {
                Point currPos = new Point(row, col);
                if (isCorrectWord(allChar, leftBottomDiagonalStrategy, currPos, "MAS")) {
                    leftBottomMatch.add(leftBottomDiagonalStrategy.getNext(currPos));
                }
                if (isCorrectWord(allChar, rightBottomDiagonalStrategy, currPos, "MAS")) {
                    rightBottomMatch.add(rightBottomDiagonalStrategy.getNext(currPos));
                }
            }
        }
        leftBottomMatch.retainAll(rightBottomMatch);
        return leftBottomMatch.size();
    }

    private static boolean isCorrectWord(CharacterMatrix matrix, NextPosStrategy strategy, Point beginPos, String expected) {
        StringBuilder word = new StringBuilder();
        Point iterPoint = beginPos;
        for (int i = 0; i < expected.length(); ++i) {
            Character thisChar = matrix.get(iterPoint);
            if (thisChar == null) {
                return false;
            }
            word.append(matrix.get(iterPoint));
            iterPoint = strategy.getNext(iterPoint);
        }
        return expected.contentEquals(word) || expected.contentEquals(word.reverse());
    }

    private static CharacterMatrix readInput() {
        Scanner s = ProblemInputReader.getInputAsScanner("day4", "input.txt");
        CharacterMatrix matrix = new CharacterMatrix();
        while (s.hasNextLine()) {
            String line = s.nextLine();
            matrix.addRow(line);
        }
        matrix.verify();
        return matrix;
    }
}
