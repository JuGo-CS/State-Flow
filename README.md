# State Flow

## Creators

- Leona Mae Q. Blancaflor / [github-leoleom](https://github.com/leoleom)
- Kenneth G. Mondejar / [github-JuGo-CS](https://github.com/JuGo-CS)


## Overview

StateFlow is a general-purpose programming language designed for programs that naturally operate through different stages or states. Its main idea is to make the current execution state of a program and the transitions between states explicit through `state` and `goto` constructs. The language has a readable syntax inspired by languages such as Python, while still providing common programming features such as variables, functions, conditionals, loops, input, and output. State Flow is dynamically typed, so variables and state arguments do not require declared types. It is intended for programs such as interactive applications, transaction workflows, menu-driven programs, quizzes, and other programs where execution naturally moves between different stages.

## Host language and build

- Host language: Kotlin 
- Version metadata: 2.0.20
- Build: `./build.sh`
- [Anything a fresh clone needs to know.]

## Running it


| Command | What it does |
|---|---|
| `./run <file>` | [Executes a program. Available from Lab 4.] |
| `./run --tokenize <file>` | Scans the source file and prints its token stream. Available from Lab 1.|
| `./run --parse <file>` | [Prints the parsed tree.] |
| `./run --eval <file>` | [Evaluates each expression and prints its value.] |
| `./run` | [Starts the REPL.] |


## Exit codes
- 0 - A successful scan exits with code 0.
- 65 - The file was read successfully, but the scanner found invalid or malformed input.
- 66 - The program could not read the file, such as when the file does not exist or cannot be opened.
- 70 - The scanner started successfully but encountered an unexpected internal failure while processing the file. This will only be used starting in Lab 3.

## File extension

StateFlow source files use the `.sf` extension.

## Lexical structure

### Keywords
The following words are reserved and cannot be used as identifiers.

| Keyword | Purpose |
| :--- | :--- |
| **state** | Declares a program state. |
| **goto** | Transitions execution to another state. |
| **requires** | Defines a condition that must be satisfied before entering a state. |
| **var** | Declares a variable. |
| **fun** | Declares a function. |
| **if** | Starts a conditional branch. |
| **else** | Defines the alternative branch of a conditional. |
| **while** | Starts a while loop. |
| **for** | Starts a for loop. |
| **return** | Returns a value from a function. |
| **print** | Outputs a value. |
| **input** | Receives input from the user. |
| **true** | Boolean literal representing true. |
| **false** | Boolean literal representing false. |
| **nil** | Represents the absence of a value. |
| **and** | Logical AND operator. |
| **or** | Logical OR operator. |


### Operators


| Operator | Category | Operands | Associativity | Precedence |
|---|---|---|---|---|
| `!` | logical | unary | right | [TBD] |
| `!=` | comparison | binary | left | [TBD] |
| `=` | assignment | binary | right | [TBD] |
| `==` | comparison | binary | left | [TBD] |
| `>` | comparison | binary | left | [TBD] |
| `>=` | comparison | binary | left | [TBD] |
| `<` | comparison | binary | left | [TBD] |
| `<=` | comparison | binary | left | [TBD] |
| `+` | arithmetic | binary | left | [TBD] |
| `-` | arithmetic | binary or unary | left/right | [TBD] |
| `*` | arithmetic | binary | left | [TBD] |
| `/` | arithmetic | binary | left | [TBD] |



### Literals

| Kind | Syntax | Produces |
|---|---|---|
| [number] | `42, 3.14` | A numeric literal |
| [string] | `"hello"` escapes supported] | A string literal |
| [boolean] | `true`, `false` | A boolean literal |
| [nil] | `nil` | A nil value |



### Identifiers

- Start characters: (A-Z, a-z) and underscore (_)
- Continue characters: Letters, digits (0-9), and underscore (_)
- Case-sensitive: Yes
- Identifiers cannot be reserved keywords.
- Executable files must contain a state named START, which serves as the program's entry point.
- Executable files must contain a state named END, which serves as the program's end state.
- START and END are reserved state names and cannot be redefined.
- Every state except END must explicitly specify a transition using goto <state>.
- A goto statement must reference an existing state.



### Comments

- Line comments: `//`
- Block comments: Not supported yet
- Nesting: Not supported
- [Harness note: comment_prefix in tests/lab*/manifest.json is set to the
  token above.]

## Whitespace and termination

- Whitespace significant: No
- Statement terminator: `;`
- Block delimiters: `{` and `}`
- Grouping delimiters: `(` and `)`

## Token output format

```
Token(type=VAR, lexeme=var, literal=null, line=1)
```

[What each field means. Frozen as of Lab 1; changes are recorded in the
changelog.]

## Grammar

```
[Your complete context-free grammar, current as of the latest activity.
Unambiguous, with precedence and associativity encoded in rule structure.]
```

## Parse output format

```
[one line of real --parse output, e.g. (+ 1.0 (* 2.0 3.0))]
```

- Groupings print as: [form]
- Numbers print as: [form]

## Semantics

### Values and types

[What runtime values exist, and how they are represented in the host
language.]

### Value printing

- Numbers: [e.g. 5 rather than 5.0]
- Nil: [spelling]
- Strings: [with or without quotes]

### Truthiness

[The complete rule. Which values are false in a condition; everything else is
true.]

### Operator semantics

- Arithmetic: [accepted operand types]
- `+` on strings: [concatenation, error, or coercion]
- Mixed types: [what happens]
- Comparison: [accepted operand types]
- Equality across types: [false, or an error]
- Division by zero: [value produced, or runtime error]

### Scope and bindings

- Redeclaration in the same scope: [allowed or an error]
- Uninitialized variable holds: [value]
- Shadowing: [behavior]
- Undefined name: [static error with exit 65, or runtime error with exit 70]

### Control flow and functions

- Logical operators return: [booleans, or the operand]
- Dangling else binds to: [which if]
- Closure capture of a loop variable: [per iteration, or shared]
- Function with no return statement produces: [value]
- Arity mismatch: [message and exit code]

## Native functions


| Name | Arguments | Returns | Notes |
|---|---|---|---|
| [name] | [count and types] | [type] | [caveats] |


## Errors and diagnostics

Message format:

```
[line 3] Error: Unexpected character: '@'
[line 1] Error at ')': Expect expression.
[line 1] Error at end: Expect end of expression.
```

The first is a scanner (lexical) error. The second and third are parser (syntax) errors: `Error at '<lexeme>'` when a specific bad token was found, `Error at end` when the parser ran out of tokens before finding what it needed. [one real runtime error -- not yet possible, no evaluator exists until Lab 3]
 

| Failure | Exit code |
|---|---|
| Lexical error | 65 |
| Syntax error | 65 |
| File read error | 66 |
| Runtime error | 70 (not yet reachable -- Lab 3) |



## Testing conventions


| Folder | Activity | Mode | Flag |
|---|---|---|---|
| tests/lab1 | Scanner | sidecar | `--tokenize` |
| tests/lab2 | Parser | sidecar | `--parse` |
| tests/lab3 | Evaluator | inline | `--eval` |
| tests/lab4 | Context | inline | none |
| tests/lab5 | Functions | inline | none |


`tests/lab2/` covers every literal kind, the `term` level (including left-associativity, `1 - 2 - 3`), the `factor` level (including precedence over `term`, `2 + 3 * 4`), comparison, equality (including a comparison nested inside an equality), unary (including double negation, `!!true`), simple and nested grouping, one expression exercising the full six-level chain, and two syntax-error cases (unclosed paren, dangling operator) asserting exit 65 with empty stdout.


```
[specific tests]...
```

Run locally with:

```bash
curl -sSL https://raw.githubusercontent.com/WhiteLicorice/cmsc-124-harness/v1.1/run_tests.py -o run_tests.py
./build.sh
python3 run_tests.py tests/lab1
python3 run_tests.py tests/lab2
```

## Sample code

```
state START {
  var name = input();
  goto GREETING(name);
}

state GREETING(name) {
  requires(name != nil);

  print "Hello, " + name;
  goto END;
}

state END {
}
```

```
Output:


[its output]
```

## Design rationale

[Why the language is the way it is. Cover the choices that surprised you, the
features you cut, and the decisions you reversed. Specific reasons, not
approval of your own work.]

## Known limitations

- [What doesn't work, what is unimplemented, where behavior is worse than you
  would like.]

## Changelog


| Activity | What changed in the language |
|---|---|
| Lab 1 | Added the scanner and defined the lexical structure of StateFlow, including keywords, identifiers, literals, operators, punctuation, whitespace, comments, and lexical error handling. Established `START` as the required entry state and `END` as the required termination state. |