package day6;

import util.MatrixVerifier;
import util.ProblemInputReader;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class Answer {
    private static class Map {
        private static final byte FLAG_NONE = 0x0;
        private static final byte FLAG_VISITED = 1;

        private static final byte FLAG_OBSTRUCTION = 1 << 1;
        private static final byte FLAG_MANUALLY_CREATED_OBSTRUCTION = 1 << 2;

        private static final byte FLAG_OBSTRUCTION_MET_FACING_UP = (byte) (1 << 7);
        private static final byte FLAG_OBSTRUCTION_MET_FACING_DOWN = (byte) (1 << 6);
        private static final byte FLAG_OBSTRUCTION_MET_FACING_LEFT = (byte) (1 << 5);
        private static final byte FLAG_OBSTRUCTION_MET_FACING_RIGHT = (byte) (1 << 4);

        private final ArrayList<ArrayList<Byte>> map = new ArrayList<>();
        private Point initPos;

        public Point getInitPos() {
            return initPos;
        }

        public void addRow(String rowData) {
            ArrayList<Byte> row = new ArrayList<>();
            char[] rowArr = rowData.toCharArray();
            for (int i = 0; i < rowArr.length; ++i) {
                char c = rowArr[i];
                if (c == '.') {
                    row.add(FLAG_NONE);
                } else if (c == '#') {
                    row.add(FLAG_OBSTRUCTION);
                } else if (c == '^') {
                    Point newInitPos = new Point(getRowSize(), i);
                    if (initPos == null) {
                        initPos = newInitPos;
                    } else {
                        throw new IllegalStateException(STR."another init pos present?! curr init: \{initPos}, another: \{newInitPos}");
                    }
                    row.add(FLAG_NONE);
                } else {
                    throw new IllegalArgumentException(STR."unrecognized token: \{c}");
                }
            }
            map.add(row);
        }

        public void verify() {
            MatrixVerifier.verify(map);
            if (initPos == null) {
                throw new IllegalStateException("init pos not found");
            }
        }

        public void clearMapFlags() {
            for (int row = 0; row < getRowSize(); ++row) {
                for (int col = 0; col < getColSize(); ++col) {
                    byte pos = map.get(row).get(col);
                    map.get(row).set(col, (byte) (pos & FLAG_OBSTRUCTION));
                }
            }
        }

        private void addObstructionManually(int row, int col) {
            byte flag = validateAndGet(row,col);
            map.get(row).set(col, (byte) (flag | FLAG_MANUALLY_CREATED_OBSTRUCTION));
        }

        private void meetObstruction(Point pt, HeadingDirection headingDirection) {
            byte flag = validateAndGet(pt.x, pt.y);
            switch (headingDirection) {
                case UpwardHeadingDirection _ -> flag |= FLAG_OBSTRUCTION_MET_FACING_UP;
                case DownwardHeadingDirection _ -> flag |= FLAG_OBSTRUCTION_MET_FACING_DOWN;
                case LeftHeadingDirection _ -> flag |= FLAG_OBSTRUCTION_MET_FACING_LEFT;
                case RightHeadingDirection _ -> flag |= FLAG_OBSTRUCTION_MET_FACING_RIGHT;
            }
            map.get(pt.x).set(pt.y, flag);
        }

        private boolean isObstructionMetWithDirection(Point pt, HeadingDirection headingDirection) {
            boolean met;
            byte flag = validateAndGet(pt.x, pt.y);
            switch (headingDirection) {
                case UpwardHeadingDirection _ -> met =  (flag & FLAG_OBSTRUCTION_MET_FACING_UP) != 0;
                case DownwardHeadingDirection _ -> met = (flag & FLAG_OBSTRUCTION_MET_FACING_DOWN) != 0;
                case LeftHeadingDirection _ ->met =  (flag & FLAG_OBSTRUCTION_MET_FACING_LEFT) != 0;
                case RightHeadingDirection _ ->met =  (flag & FLAG_OBSTRUCTION_MET_FACING_RIGHT) != 0;
            }
            return met;
        }

        public int getAllVisitedPosCount() {
            return (int) map.stream().flatMap(Collection::stream).filter(pos -> (pos & FLAG_VISITED) != 0).count();
        }

        public void visit(Point pt) {
            visit(pt.x, pt.y);
        }

        public boolean isObstruction(Point pt) {
            return isObstruction(pt.x, pt.y);
        }

        public void visit(int x, int y) {
            byte pos = validateAndGet(x, y);
            if (isObstruction(x, y)) {
                throw new IllegalArgumentException(STR."you can't visit a obstruction @ (\{x},\{y})");
            }
            map.get(x).set(y, (byte) (pos | FLAG_VISITED));
        }

        public boolean isObstruction(int x, int y) {
            byte pos = validateAndGet(x, y);
            return (pos & (FLAG_OBSTRUCTION | FLAG_MANUALLY_CREATED_OBSTRUCTION)) != 0;
        }

        private byte validateAndGet(int x, int y) {
            if (!isValidMapPos(new Point(x, y))) {
                throw new IllegalArgumentException(STR."invalid visit @ (\{x},\{y})");
            }
            return map.get(x).get(y);
        }

        public boolean isValidMapPos(Point point) {
            int x = point.x;
            int y = point.y;
            return x >= 0 && y >= 0 && x < getRowSize() && y < getColSize();
        }

        private int getRowSize() {
            return map.size();
        }

        private int getColSize() {
            return map.getFirst().size();
        }
    }

    private sealed interface HeadingDirection permits UpwardHeadingDirection, DownwardHeadingDirection, RightHeadingDirection, LeftHeadingDirection {
        Point peekNextStep(Point currPos);

        HeadingDirection getTurningDirection();
    }

    private static final class UpwardHeadingDirection implements HeadingDirection {

        @Override
        public Point peekNextStep(Point currPos) {
            return new Point(currPos.x - 1, currPos.y);
        }

        @Override
        public HeadingDirection getTurningDirection() {
            return new RightHeadingDirection();
        }
    }

    private static final class RightHeadingDirection implements HeadingDirection {

        @Override
        public Point peekNextStep(Point currPos) {
            return new Point(currPos.x, currPos.y + 1);
        }

        @Override
        public HeadingDirection getTurningDirection() {
            return new DownwardHeadingDirection();
        }
    }

    private static final class DownwardHeadingDirection implements HeadingDirection {

        @Override
        public Point peekNextStep(Point currPos) {
            return new Point(currPos.x + 1, currPos.y);
        }

        @Override
        public HeadingDirection getTurningDirection() {
            return new LeftHeadingDirection();
        }
    }

    private static final class LeftHeadingDirection implements HeadingDirection {

        @Override
        public Point peekNextStep(Point currPos) {
            return new Point(currPos.x, currPos.y - 1);
        }

        @Override
        public HeadingDirection getTurningDirection() {
            return new UpwardHeadingDirection();
        }
    }


    public static void main() {
        Map map = readInput();
        System.err.println(STR."Question 1: \{solveProblem1(map)}");
        System.err.println(STR."Question 2: \{solveProblem2(map)}");
    }

    private static int solveProblem1(Map map) {
        HeadingDirection direction = new UpwardHeadingDirection();
        Point current = map.getInitPos();
        while (map.isValidMapPos(current)) {
            map.visit(current);
            Point nextPos = direction.peekNextStep(current);
            if (!map.isValidMapPos(nextPos)) {
                break;
            }
            while (map.isObstruction(nextPos)) {
                direction = direction.getTurningDirection();
                nextPos = direction.peekNextStep(current);
            }
            current = nextPos;
        }
        return map.getAllVisitedPosCount();
    }

    private static int solveProblem2(Map map) {
        int loopCount = 0;
        for (int row = 0; row < map.getRowSize(); ++row) {
            for (int col = 0; col < map.getColSize(); ++col) {
                map.clearMapFlags();
                map.addObstructionManually(row, col);
                if (hasLoop(map)) {
                    ++loopCount;
                }
            }
        }
        return loopCount;
    }

    private static boolean hasLoop(Map map) {
        HeadingDirection direction = new UpwardHeadingDirection();
        Point current = map.getInitPos();
        boolean loop = false;
        while (map.isValidMapPos(current)) {
            Point nextPos = direction.peekNextStep(current);
            if (!map.isValidMapPos(nextPos)) {
                break;
            }
            while (map.isObstruction(nextPos)) {
                if (map.isObstructionMetWithDirection(nextPos, direction)) {
                    loop = true;
                    break;
                } else {
                    map.meetObstruction(nextPos, direction);
                }
                direction = direction.getTurningDirection();
                nextPos = direction.peekNextStep(current);
            }
            current = nextPos;
        }

        return loop;
    }


    private static Map readInput() {
        Scanner scanner = ProblemInputReader.getInputAsScanner("day6", "input.txt");
        Map map = new Map();
        while (scanner.hasNextLine()) {
            map.addRow(scanner.nextLine());
        }
        map.verify();
        return map;
    }
}
