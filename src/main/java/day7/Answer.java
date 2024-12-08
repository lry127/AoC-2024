package day7;

import util.ProblemInputReader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Answer {
    private abstract static sealed class Token permits Operand, Operator {
    }

    private static final class Operand extends Token {
        private final long operand;

        public Operand(long operand) {
            this.operand = operand;
        }

        @Override
        public String toString() {
            return String.valueOf(operand);
        }
    }

    private abstract static sealed class Operator extends Token permits Plus, Multiply, Concatenation {

    }

    private static final class Plus extends Operator {
        @Override
        public String toString() {
            return "+";
        }
    }

    private static final class Multiply extends Operator {
        @Override
        public String toString() {
            return "*";
        }
    }

    private static final class Concatenation extends Operator {
        @Override
        public String toString() {
            return "||";
        }
    }

    private static class Expression {
        private final List<Token> tokens;

        public Expression() {
            tokens = new LinkedList<>();
        }

        public void addToken(Token t) {
            tokens.add(t);
        }

        public void addFront(List<Token> prepend) {
            for (int i = prepend.size() - 1; i >=0; --i) {
                tokens.addFirst(prepend.get(i));
            }
        }

        public List<Token> getTokens() {
            return tokens;
        }

        public long evaluate() {
            while (tokens.size() != 1) {
                Operand op1 = (Operand) tokens.removeFirst();
                Operator operand = (Operator) tokens.removeFirst();
                Operand op2 = (Operand) tokens.removeFirst();

                if (operand instanceof Multiply) {
                    tokens.addFirst(new Operand(op1.operand * op2.operand));
                } else if (operand instanceof Plus) {
                    tokens.addFirst(new Operand(op1.operand + op2.operand));
                } else {
                    String concat = STR."\{op1.operand}\{op2.operand}";
                    tokens.addFirst(new Operand(Long.parseLong(concat)));
                }
            }
            return ((Operand) tokens.getFirst()).operand;
        }

        @Override
        public String toString() {
            return tokens.toString();
        }

        public Expression getCopy() {
            Expression expression = new Expression();
            for (Token token : tokens) {
                expression.addToken(token);
            }
            return expression;
        }

        public static List<Expression> generateCombination(List<Integer> operands, boolean enableConcatenation) {
            if (operands.size() < 2) {
                throw new IllegalArgumentException("can't generate expression from less than 2 numbers");
            } else if (operands.size() == 2) {
                int operand1 =  operands.getFirst();
                int operand2 = operands.getLast();

                return new LinkedList<>() {{
                    add(twoOperandExpr(operand1, operand2, new Plus()));
                    add(twoOperandExpr(operand1, operand2, new Multiply()));
                    if (enableConcatenation) {
                        add(twoOperandExpr(operand1, operand2, new Concatenation()));
                    }
                }};
            }

            int first = operands.removeFirst();
            List<Expression> trailing = generateCombination(operands, enableConcatenation);


            List<Expression> plusAdded = prependExpression(first, new Plus(), trailing);
            List<Expression> multiplyAdded = prependExpression(first, new Multiply(), trailing);
            plusAdded.addAll(multiplyAdded);

            if (enableConcatenation) {
                plusAdded.addAll(prependExpression(first, new Concatenation(), trailing));
            }
            return plusAdded;
        }

        private static Expression twoOperandExpr(int operand1, int operand2, Operator operator) {
            Expression expr = new Expression();
            expr.addToken(new Operand(operand1));
            expr.addToken(operator);
            expr.addToken(new Operand(operand2));
            return expr;
        }

        private static List<Expression> prependExpression(int operand, Operator operator, List<Expression> trailing) {
            Expression prepend = new Expression();
            prepend.addToken(new Operand(operand));
            prepend.addToken(operator);
            List<Expression> copy = new LinkedList<>();
            for (Expression expr : trailing) {
                copy.add(expr.getCopy());
            }

            for (Expression expr : copy) {
                expr.addFront(prepend.getTokens());
            }
            return copy;
        }
    }


    private static class Equation {
        private final long target;
        private final List<Integer> operands = new ArrayList<>();

        public Equation(String line) {
            String[] parts = line.split(":");
            target = Long.parseLong(parts[0]);

            Scanner operandsScanner = new Scanner(parts[1]);
            while (operandsScanner.hasNextInt()) {
                operands.add(operandsScanner.nextInt());
            }
        }

        public long getTarget() {
            return target;
        }

        public List<Integer> getOperands() {
            return new ArrayList<>(List.copyOf(operands));
        }
    }

    public static void main() {
        List<Equation> equations = readInput();
        System.err.println(STR."Question 1: \{solveProblem1(equations)}");
        System.err.println(STR."Question 2: \{solveProblem2(equations)}");
    }


    private static long solveProblem1(List<Equation> equations) {
        long sum = 0;
        for (Equation eq : equations) {
            long target  = eq.getTarget();
            List<Expression> allPossibleExpressions = Expression.generateCombination(eq.getOperands(), false);
            for (Expression expr : allPossibleExpressions) {
                if (expr.evaluate() == target) {
                    sum += target;
                    break;
                }
            }
        }
        return sum;
    }

    private static long solveProblem2(List<Equation> equations) {
        long sum = 0;
        for (Equation eq : equations) {
            long target  = eq.getTarget();
            List<Expression> allPossibleExpressions = Expression.generateCombination(eq.getOperands(), true);
            for (Expression expr : allPossibleExpressions) {
                if (expr.evaluate() == target) {
                    sum += target;
                    break;
                }
            }
        }
        return sum;
    }


    private static List<Equation> readInput() {
        Scanner s = ProblemInputReader.getInputAsScanner("day7", "input.txt");
        List<Equation> result = new ArrayList<>();
        while (s.hasNextLine()) {
            result.add(new Equation(s.nextLine()));
        }
        return result;
    }
}
