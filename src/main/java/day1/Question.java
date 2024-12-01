package day1;

import util.ProblemInputReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Question {
    public static void main(String[] args) {
        List<List<Integer>> in = readInput();
        List<Integer> col1 = in.getFirst();
        List<Integer> col2 = in.getLast();

        col1.sort(Integer::compare);
        col2.sort(Integer::compareTo);

        int result = solveQuestion1(col1, col2);
        System.err.println(STR."Question 1: \{result}");

        result = solveQuestion2(col1, col2);
        System.err.println(STR."Question 2: \{result}");
    }

    private static List<List<Integer>> readInput() {
        List<List<Integer>> res = new ArrayList<>(2);
        List<Integer> col1 = new ArrayList<>();
        List<Integer> col2 = new ArrayList<>();
        res.add(col1);
        res.add(col2);

        Scanner r = ProblemInputReader.getInput("day1", "input.txt");
        while (r.hasNextInt()) {
            col1.add(r.nextInt());
            col2.add(r.nextInt());
        }
        if (col1.size() != col2.size()) {
            throw new RuntimeException("list expected to be equal size");
        }

        return res;
    }

    private static int solveQuestion1(List<Integer> c1, List<Integer> c2) {
        int sum = 0;
        for (int i = 0; i < c1.size(); ++i) {
            int val1 = c1.get(i);
            int val2 = c2.get(i);
            sum += Math.abs(val1 - val2);
        }
        return sum;
    }

    private static int solveQuestion2(List<Integer> leftList, List<Integer> rightList) {
        int similarityTotal = 0;
        HashMap<Integer, Integer> cachedFindings = new HashMap<>();
        for (int val : leftList) {
            int occurredTimes = cachedFindings.computeIfAbsent(val, k -> {
                if (!rightList.contains(k)) {
                    return -1;
                }
                int idx = rightList.indexOf(k);
                int occurred = 0;
                for (; idx < rightList.size(); ++idx) {
                    if ((int) rightList.get(idx) == k) {
                        ++occurred;
                    } else {
                        break;
                    }
                }
                return occurred;
            });
            if (occurredTimes != -1) {
                similarityTotal += val * occurredTimes;
            }
        }
        return similarityTotal;
    }

}
