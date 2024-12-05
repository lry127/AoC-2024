package day5;

import util.ProblemInputReader;

import java.util.*;
import java.util.stream.Collectors;

public class Answer {
    private record InputReadResult(HashMap<Integer, List<Integer>> rules, List<List<Integer>> updates) {
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Rules:\n");

            for (Map.Entry<Integer, List<Integer>> entry : rules.entrySet()) {
                builder.append(STR."\{entry.getKey()}:\t \{entry.getValue().stream().map(String::valueOf).collect(Collectors.joining(", "))}\n");
            }
            builder.append("\n").append("Updates:\n");
            for (List<Integer> update : updates) {
                builder.append(update.stream().map(String::valueOf).collect(Collectors.joining(", "))).append('\n');
            }

            return builder.toString();
        }
    }

    public static void main() {
        InputReadResult result = readInput();
        System.err.println(result);

        List<List<Integer>> correctUpdates = solveProblem1(result.rules(), result.updates());
        System.err.println(STR."Question 1: \{sumMiddle(correctUpdates)}");

        List<List<Integer>> incorrectUpdates = new ArrayList<>(result.updates()) {{
            removeAll(correctUpdates);
        }};
        List<List<Integer>> correctedUpdates = solveProblem2(result.rules(), incorrectUpdates);
        System.err.println(STR."Question 2: \{sumMiddle(correctedUpdates)}");
    }

    private static int sumMiddle(List<List<Integer>> updates) {
        return updates.stream().mapToInt(update -> update.get((update.size() - 1) / 2)).sum();
    }

    private static List<List<Integer>> solveProblem1(HashMap<Integer, List<Integer>> rules, List<List<Integer>> updates) {
        List<List<Integer>> correctUpdates = new ArrayList<>();
        for (List<Integer> update : updates) {
            if (isUpdateCorrect(update, rules)) {
                correctUpdates.add(update);
            }
        }
        return correctUpdates;
    }

    private static List<List<Integer>> solveProblem2(HashMap<Integer, List<Integer>> rules, List<List<Integer>> incorrectUpdates) {
        ArrayList<List<Integer>> correctedUpdates = new ArrayList<>();
        for (List<Integer> incorrectUpdateCopy : incorrectUpdates) {
            ArrayList<Integer> incorrectUpdate = new ArrayList<>(incorrectUpdateCopy);
            ArrayList<Integer> correctedUpdate = new ArrayList<>();
            outer:
            while (!incorrectUpdate.isEmpty()) {
                incorrectUpdate.removeAll(correctedUpdate);
                for (int i = 0; i < incorrectUpdate.size(); ++i) {
                    int value = incorrectUpdate.get(i);
                    boolean shouldPush = true;
                    List<Integer> acceptableSucceeding = rules.get(value);
                    if (acceptableSucceeding != null) {
                        for (int otherValue : incorrectUpdate) {
                            if (otherValue != value && !acceptableSucceeding.contains(otherValue)) {
                                shouldPush = false;
                                break;
                            }
                        }
                    } else {
                        int last = incorrectUpdate.getLast();
                        if (value != last) {
                            incorrectUpdate.set(i, last);
                            incorrectUpdate.set(incorrectUpdate.size() -1 , value);
                            continue outer;
                        }
                    }
                    if (shouldPush) {
                        correctedUpdate.add(value);
                        continue outer;
                    }
                }
            }
            correctedUpdates.add(correctedUpdate);
        }
        return correctedUpdates;
    }

    private static boolean isUpdateCorrect(List<Integer> update, HashMap<Integer, List<Integer>> rules) {
        for (int currPageIdx = 0; currPageIdx < update.size() - 1; ++currPageIdx) {
            int pageNo = update.get(currPageIdx);
            List<Integer> rule = rules.get(pageNo);
            if (rule == null) {
                return false;
            }
            for (int succeedingPageIdx = currPageIdx + 1; succeedingPageIdx < update.size(); ++succeedingPageIdx) {
                int succeedingPageNo = update.get(succeedingPageIdx);
                if (!rule.contains(succeedingPageNo)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static InputReadResult readInput() {
        Scanner scanner = ProblemInputReader.getInputAsScanner("day5", "input.txt");
        HashMap<Integer, List<Integer>> rules = new HashMap<>();
        List<List<Integer>> updates = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String data = scanner.nextLine();
            if (data.isBlank()) {
                continue;
            }
            if (data.indexOf('|') != -1) {
                String[] pages = data.split("\\|");
                int pageNo = Integer.parseInt(pages[0]);
                int successorPageNo = Integer.parseInt(pages[1]);
                List<Integer> rulesForPageNo = rules.computeIfAbsent(pageNo, _ -> new ArrayList<>());
                rulesForPageNo.add(successorPageNo);
            } else {
                updates.add(Arrays.stream(data.split(",")).map(Integer::parseInt).toList());
            }
        }
        return new InputReadResult(rules, updates);
    }
}
