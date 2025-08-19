/*
    SkyLib is a library that contains shared code for all of my plugins.
    Copyright (c) 2024 lukeskywlker19

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 */
package com.github.lukesky19.skylib.api.math;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Stack;

/**
 * This class is used to evaluate equations from a {@link String} with a Hashmap of variables.
 */
public class EquationUtil {
    /**
     * All methods in this class are static so this constructor will throw a runtime exception if used.
     * @throws RuntimeException if the constructor is used.
     */
    public EquationUtil() {
        throw new RuntimeException("This class cannot be instanced. Use the static references to methods instead.");
    }

    /**
     * Takes a {@link String} as an input that contains an equation and calculates the result.
     * @param equation The equation to parse and calculate.
     * @param variables The variables and the values to replace them with.
     * @return The calculated result as a {@link Double}
     * @throws RuntimeException If a equation part is not recognized by the parser.
     * @throws ArithmeticException If a divide-by-zero situation occurs.
     */
    public static @NotNull Double evaluateEquation(@NotNull String equation, @NotNull Map<String, String> variables) {
        // Replace any variables
        for(Map.Entry<String, String> entry : variables.entrySet()) {
            equation = equation.replace(entry.getKey(), entry.getValue());
        }

        // Remove spaces from the equation
        equation = equation.replace(" ", "");

        // Split the equation into parts
        String[] parts = equation.split("(?<=[-+*/^()])|(?=[-+*/^()])");

        // Two stacks that store the equation's values and operators.
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        // Iterate through each part in the equation.
        for(int i = 0; i < parts.length; i++) {
            String currentPart = parts[i];

            if(isDouble(currentPart)) {
                double number = Double.parseDouble(currentPart);
                values.push(number);
            } else if(currentPart.equals("(")) {
                operators.push('(');
            } else if(currentPart.equals(")")) {
                while(operators.peek() != '(') {
                    double b = values.pop();
                    double a = values.pop();
                    char operator = operators.pop();
                    values.push(applyOperator(operator, b, a));
                }

                operators.pop();
            } else if (isOperator(currentPart)) {
                // Handle unary minus
                if(currentPart.equals("-") && (i == 0 || isOperator(parts[i - 1]) || parts[i - 1].equals("("))) {
                    // This is a unary minus, so we need to treat the next number as negative
                    i++;

                    if(i < parts.length && isDouble(parts[i])) {
                        double number = -Double.parseDouble(parts[i]);
                        values.push(number);
                    } else {
                        throw new RuntimeException("Invalid part of equation after unary minus.");
                    }
                } else {
                    while(!operators.isEmpty() && hasPrecedence(currentPart.charAt(0), operators.peek())) {
                        double b = values.pop();
                        double a = values.pop();
                        char operator = operators.pop();
                        values.push(applyOperator(operator, b, a));
                    }

                    operators.push(currentPart.charAt(0));
                }
            } else {
                throw new RuntimeException("Unknown part of equation: " + currentPart);
            }
        }

        // Process remaining operators
        while (!operators.isEmpty()) {
            double b = values.pop();
            double a = values.pop();
            char operator = operators.pop();
            values.push(applyOperator(operator, b, a));
        }

        return values.pop(); // Return the final result
    }

    /**
     * Check if a {@link String} is a Double.
     * @param string The {@link String} to check.
     * @return true if a Double, otherwise false.
     */
    private static boolean isDouble(@NotNull String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check if a {@link String} is an operator.
     * @param string The {@link String} to check.
     * @return true if an operator, otherwise false.
     */
    private static boolean isOperator(@NotNull String string) {
        return string.equals("+") || string.equals("-") || string.equals("*") || string.equals("/") || string.equals("^");
    }

    /**
     * Determines if the first operator has precedence over the second operator.
     * The precedence rules are based on square root having higher precedence over multiplication and division and
     * multiplication and division having higher precedence than addition and subtraction.
     * Parentheses are handled separately and are considered special cases.
     * @param operator1 the first operator to compare
     * @param operator2 the second operator to compare
     * @return true if operator1 has precedence over operator2, false otherwise.
     */
    private static boolean hasPrecedence(char operator1, char operator2) {
        if (operator2 == '(' || operator2 == ')') {
            return false;
        }

        if (operator1 == '^') {
            return true;
        }

        if(operator1 == '*' || operator1 == '/') {
            return operator2 != '+' && operator2 != '-';
        }

        return false;
    }

    /**
     * Takes two numbers and an operator and returns the result.
     * @param operator The operator to apply to the numbers.
     * @param b The second number.
     * @param a The first number.
     * @return A {@link Double} of the result.
     * @throws RuntimeException if an unknown operator is provided.
     * @throws ArithmeticException if a divide-by-zero error occurs.
     */
    @NotNull
    private static Double applyOperator(char operator, double b, double a) {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) throw new ArithmeticException("Cannot divide by zero");
                yield a / b;
            }
            case '^' -> Math.pow(a, b);

            default -> throw new RuntimeException("Unknown operator provided: " + operator);
        };
    }
}
