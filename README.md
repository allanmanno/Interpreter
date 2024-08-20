# Toy Programming Language Interpreter

## Overview

This is a simple interpreter written in Java for a basic programming language that supports variable assignments and arithmetic operations. The program processes an input string, evaluates the expressions, and assigns values to variables while also handling various syntax and runtime errors.

## Features

- **Variable Assignment**: Assign integer values to variables.
- **Expression Evaluation**: Supports basic arithmetic operations (`+`, `-`, `*`, `/`) with proper operator precedence and parentheses.
- **Error Handling**: Detects common errors such as missing semicolons, invalid identifiers, division by zero, and usage of uninitialized variables.

## Usage

### Compilation

Compile the program using the following command:
```rb
javac Toy.java
```

### Running the Interpreter

Run the interpreter by passing your program as a string argument:

```rb
java Toy "your_program_here"
```

### Examples

#### Valid Input

- `java Toy "x = 5;"` 
  - Output: `x = 5`
  
- `java Toy "x = 5 + 3 * 2;"` 
  - Output: `x = 11`
  
- `java Toy "x = 5; y = x * 2 + 3;"` 
  - Output: 
    ```
    x = 5
    y = 13
    ```

#### Error Input

- `java Toy "x = 5"` 
  - Output: `error` (Missing semicolon)
  
- `java Toy "x = 10 / 0;"` 
  - Output: `error` (Division by zero)
  
- `java Toy "y = x + 2;"` 
  - Output: `error` (Uninitialized variable)

## How It Works

1. **Tokenization**: The program first tokenizes the input string into components like identifiers, operators, and literals.
2. **Parsing & Evaluation**: It then parses the tokens and evaluates the expressions to compute the values.
3. **Error Detection**: Various errors are detected during parsing and evaluation, which are then reported to the user.
