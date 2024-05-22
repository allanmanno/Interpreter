import java.util.*;
import java.util.regex.*;

public class Toy {
    // To store variables and their values
    private static Map<String, Integer> variables = new HashMap<>();
    // List for errors
    private static List<String> errors = new ArrayList<>();
    // regex to validate identifiers
    private static Pattern identifierPattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
    // regex to validate integer literals
    private static Pattern literalPattern = Pattern.compile("0|[1-9][0-9]*");

    public static void main(String[] args) {
        // Check if the correct number of arguments (one) is provided
        if (args.length != 1) {
            System.out.println("Usage: java Toy \"program\"");
            return;
        }

        // Take the argument
        String program = args[0];
        // Check if the program ends with a semicolon
        if (!program.endsWith(";")) {
            errors.add("error");
        } else {
            // Split the program into lines 
            String[] lines = program.split(";");
            for (String line : lines) {
                line = line.trim();
                // Process each line of the program
                if (!line.isEmpty()) {
                    processLine(line);
                }
            }
        }

        // This will print errors or variable assignments
        if (!errors.isEmpty()) {
            errors.forEach(System.out::println);
        } else {
            variables.forEach((k, v) -> System.out.println(k + " = " + v));
        }
    }

    // Process each line of the program
    private static void processLine(String line) {
        // Split the line into 2 parts. identifier and expression parts
        String[] parts = line.split("=");
        if (parts.length != 2) {
            errors.add("error");
            return;
        }

        // Extract the identifier and expression
        String identifier = parts[0].trim();
        String expression = parts[1].trim();

        // Check if the identifier is valid
        if (!isValidIdentifier(identifier)) {
            errors.add("error");
            return;
        }

        try {
            // Evaluate the expression and store the result in the variables map
            int value = evaluateExpression(expression);
            variables.put(identifier, value);
        } catch (Exception e) {
            // Handle exceptions
            errors.add("error");
        }
    }

    // Check if an identifier (variable name) is valid
    private static boolean isValidIdentifier(String identifier) {
        return identifierPattern.matcher(identifier).matches();
    }

    // Evaluate an expression and return the result
    private static int evaluateExpression(String expression) throws Exception {
        // Tokenize the expression into tokens
        List<Token> tokens = tokenize(expression);
        // Parse the expression and evaluate it
        return parseExpression(tokens);
    }

    // Tokenize an expression into tokens
    private static List<Token> tokenize(String expression) throws Exception {
        List<Token> tokens = new ArrayList<>();
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            // Skip whitespace characters
            if (Character.isWhitespace(ch)) {
                continue;
            }

            // Handle parentheses, operators, literals, and identifiers
            if (ch == '(' || ch == ')' || ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                tokens.add(new Token(TokenType.OPERATOR, Character.toString(ch)));
            } else if (Character.isDigit(ch)) {
                // Parse integer literals
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                    sb.append(expression.charAt(i++));
                }
                i--;
                String literal = sb.toString();
                if (literal.startsWith("0") && literal.length() > 1) {
                    throw new Exception("Invalid literal");
                }
                tokens.add(new Token(TokenType.LITERAL, literal));
            } else if (Character.isLetter(ch) || ch == '_') {
                // Parse identifiers (variable names)
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (Character.isLetterOrDigit(expression.charAt(i)) || expression.charAt(i) == '_')) {
                    sb.append(expression.charAt(i++));
                }
                i--;
                String var = sb.toString();
                if (!variables.containsKey(var)) {
                    throw new Exception("Uninitialized variable");
                }
                tokens.add(new Token(TokenType.IDENTIFIER, var));
            } else {
                throw new Exception("Invalid character");
            }
        }
        return tokens;
    }

    // Parse and evaluate an expression using a stack-based algorithm
    private static int parseExpression(List<Token> tokens) throws Exception {
        Stack<Integer> values = new Stack<>();
        Stack<Character> operators = new Stack<>();
        boolean expectUnary = true;

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            // Handle literals, identifiers, parentheses, and operators
            if (token.type == TokenType.LITERAL) {
                values.push(Integer.parseInt(token.value));
                expectUnary = false;
            } else if (token.type == TokenType.IDENTIFIER) {
                values.push(variables.get(token.value));
                expectUnary = false;
            } else if (token.value.equals("(")) {
                operators.push('(');
                expectUnary = true;
            } else if (token.value.equals(")")) {
                while (operators.peek() != '(') {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop();
                expectUnary = false;
            } else if (token.type == TokenType.OPERATOR) {
                char op = token.value.charAt(0);
                if (expectUnary && (op == '+' || op == '-')) {
                    values.push(0);
                }
                while (!operators.isEmpty() && hasPrecedence(op, operators.peek())) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(op);
                expectUnary = true;
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.isEmpty() ? 0 : values.pop()));
        }

        return values.pop();
    }

    // Check if a character is an operator
    private static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    // Check if an operator has higher precedence than another operator
    private static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false;
        }
        return true;
    }

    // Apply an operator to two operands
    private static int applyOperator(char op, int b, int a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
					throw new ArithmeticException("Division by zero");
                }
                return a / b;
        }
        return 0;
    }

    // Token class to represent different types of tokens in the expression
    static class Token {
        TokenType type;
        String value;

        Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    // Enum to represent the type of token
    enum TokenType {
        LITERAL,
        IDENTIFIER,
        OPERATOR
    }
}

