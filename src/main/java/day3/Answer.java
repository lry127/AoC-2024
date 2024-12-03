package day3;

import util.ProblemInputReader;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Answer {
    private record Instruction(int operator1, int operator2, int instructionPos) {
        public int compute() {
            return operator1 * operator2;
        }

        @Override
        public String toString() {
            return STR."mul(\{operator1},\{operator2}) @ \{instructionPos}";
        }
    }

    private static class ConditionController {
        private final BitSet conditionSet;

        public ConditionController(String data) {
            conditionSet = new BitSet(data.length());
            int enableIdx = 0;
            int disableIdx = data.indexOf("don't()");

            while (enableIdx != -1) {
                int enableEndIdx = disableIdx != -1 ? disableIdx : data.length();
                for (int i = enableIdx; i < enableEndIdx; ++i) {
                    conditionSet.set(i);
                }
                enableIdx = data.indexOf("do()", enableIdx + 1);
                disableIdx = data.indexOf("don't()", enableIdx);
            }
        }

        public boolean isOperationEnabledAt(int pos) {
            return conditionSet.get(pos);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < conditionSet.size(); ++i) {
                builder.append(STR."pos \{i}: \{isOperationEnabledAt(i) ? "enabled" : "disabled"}\n");
            }
            return builder.toString();
        }
    }

    public static void main() {
        String input = new String(ProblemInputReader.readAllBytes("day3", "input.txt"), StandardCharsets.UTF_8);
        List<Instruction> instructions = readInstructions(input);
        int question1Answer = instructions.stream().mapToInt(Instruction::compute).sum();
        System.err.println(STR."Question 1: \{question1Answer}");

        ConditionController controller = new ConditionController(input);
        int question2Answer = instructions.stream()
                .filter(ins -> controller.isOperationEnabledAt(ins.instructionPos()))
                .mapToInt(Instruction::compute).sum();

        System.err.println(STR."Question 2: \{question2Answer}");
    }

    private static List<Instruction> readInstructions(String input) {
        List<Instruction> result = new ArrayList<>();
        char[] inputArr = input.toCharArray();
        int idx = input.indexOf("mul(");
        while (idx != -1) {
            Instruction ins = readInstruction(inputArr, idx + 4);
            if (ins != null) {
                result.add(ins);
            }
            idx = input.indexOf("mul", idx + 1);
        }
        return result;
    }

    private static Instruction readInstruction(char[] arr, int iterIdx) {
        int beginIdx = iterIdx;
        String operator1 = readOperator(arr, iterIdx);
        if (operator1.isEmpty() || operator1.length() > 3) {
            return null;
        }
        iterIdx += operator1.length();
        if (iterIdx >= arr.length) {
            return null;
        }
        char delimiter = arr[iterIdx];
        if (delimiter != ',') {
            return null;
        }
        ++iterIdx;
        String operator2 = readOperator(arr, iterIdx);
        if (operator2.isEmpty() || operator2.length() > 3) {
            return null;
        }
        iterIdx += operator2.length();
        if (iterIdx >= arr.length) {
            return null;
        }
        delimiter = arr[iterIdx];
        if (delimiter != ')') {
            return null;
        }
        return new Instruction(Integer.parseInt(operator1), Integer.parseInt(operator2), beginIdx);
    }

    private static String readOperator(char[] arr, int beginIdx) {
        StringBuilder builder = new StringBuilder();
        while (beginIdx < arr.length) {
            char c = arr[beginIdx];
            if (Character.isDigit(c)) {
                builder.append(c);
            } else {
                return builder.toString();
            }
            ++beginIdx;
        }
        return builder.toString();
    }
}
