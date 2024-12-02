package day2;

import util.ProblemInputReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Answer {
    public static void main() {
        List<List<Integer>> reports = readInput();
        List<Integer> safeReports = solveQuestion1(reports);
        System.err.println(STR."Question 1: \{safeReports.size()}");

        List<Integer> updatedSafeReport = solveQuestion2(reports, safeReports);
        System.err.println(STR."Question 2: \{updatedSafeReport.size()}");
    }

    private static List<List<Integer>> readInput() {
        Scanner s = ProblemInputReader.getInput("day2", "input.txt");
        List<List<Integer>> reports = new ArrayList<>();
        while (s.hasNextLine()) {
            String line = s.nextLine();
            Scanner lineScanner = new Scanner(line);
            List<Integer> report = new ArrayList<>();
            while (lineScanner.hasNextInt()) {
                report.add(lineScanner.nextInt());
            }
            reports.add(report);
        }
        return reports;
    }

    private static List<Integer> solveQuestion1(List<List<Integer>> reports) {
        List<Integer> safeReportsIdx = new ArrayList<>();
        for (int i = 0; i < reports.size(); ++i) {
            List<Integer> report = reports.get(i);
            if (isReportSafe(report)) {
                safeReportsIdx.add(i);
            }
        }
        return safeReportsIdx;
    }

    private static List<Integer> solveQuestion2(List<List<Integer>> allReports, List<Integer> safeReportsIdx) {
        List<Integer> unsafeReportsIdx = new ArrayList<>(allReports.size());
        for (int i = 0; i < allReports.size(); ++i) {
            unsafeReportsIdx.add(i);
        }
        unsafeReportsIdx.removeAll(safeReportsIdx);
        List<Integer> newSafeReports = new ArrayList<>();
        for (int unsafeReportIdx : unsafeReportsIdx) {
            List<Integer> unsafeReport = allReports.get(unsafeReportIdx);
            for (int i = 0; i < unsafeReport.size(); ++i) {
                boolean successful = removeAndTest(unsafeReport, i);
                if (successful) {
                    newSafeReports.add(unsafeReportIdx);
                    System.err.println(STR."making safe (remove gap): \{unsafeReport}");
                    break;
                }
            }
        }
        newSafeReports.addAll(safeReportsIdx);
        return newSafeReports;
    }

    private static boolean removeAndTest(List<Integer> report, int offendingIdx) {
        List<Integer> copy = new ArrayList<>(report);
        copy.remove(offendingIdx);
        return isReportSafe(copy);
    }


    private static boolean isReportSafe(List<Integer> report) {
        int firstLevel = report.get(0);
        int secondLevel = report.get(1);
        boolean isIncreasing = secondLevel - firstLevel > 0;
        int prevLevel = firstLevel;
        for (int i = 1; i < report.size(); ++i) {
            int currLevel = report.get(i);
            int diff = currLevel - prevLevel;
            if (isIncreasing) {
                if (diff <= 0 || diff > 3) {
                    return false;
                }
            } else {
                if (diff >= 0 || diff < -3) {
                    return false;
                }
            }
            prevLevel = currLevel;
        }
        return true;
    }
}
