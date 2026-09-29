# State Flow

## Creators

- Leona Mae Q. Blancaflor / [github-leoleom](https://github.com/leoleom)
- Kenneth G. Mondejar / [github-JuGo-CS](https://github.com/JuGo-CS)


## Overview

StateFlow is a general-purpose programming language designed for programs that naturally operate through different stages or states. Its main idea is to make the current execution state of a program and the transitions between states explicit through `state` and `goto` constructs. The language has a readable syntax inspired by languages such as Python, while still providing common programming features such as variables, functions, conditionals, loops, input, and output. State Flow is dynamically typed, so variables and state arguments do not require declared types. It is intended for programs such as interactive applications, transaction workflows, menu-driven programs, quizzes, and other programs where execution naturally moves between different stages.

## Host language and build

- Host language: Kotlin - 262.9437.185-IJ
- Version metadata: [file that pins it, e.g. rust-toolchain.toml, go.mod]
- Build: `./build.sh`
- [Anything a fresh clone needs to know.]

## Running it


| Command | What it does |
|---|---|
| `./run <file>` | [Executes a program. Available from Lab 4.] |
| `./run --tokenize <file>` | Scans the source file and prints its token stream. |
| `./run --parse <file>` | [Prints the parsed tree.] |
| `./run --eval <file>` | [Evaluates each expression and prints its value.] |
| `./run` | [Starts the REPL.] |


## Exit codes
- 0 - A successful scan exits with code 0.
- 65 [when]
- 70 [when].

## File extension

StateFlow source files use the `.sta` extension.

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
| [op] | [arithmetic, comparison, logical, assignment, other] | [unary or binary] | [left, right, none] | [1 = loosest] |


### Literals


| Kind | Syntax | Produces |
|---|---|---|
| [number] | [e.g. 42, 3.14] | [what runtime value] |
| [string] | [e.g. "hello", escapes supported] | [what runtime value] |
| [boolean] | [true, false] | [what runtime value] |
| [nil] | [spelling] | [what runtime value] |


### Identifiers

- Start characters: [which]
- Continue characters: [which]
- Case-sensitive: [yes or no]
- [Reserved patterns, length limits, or other restrictions.]

### Comments

- Line comments: [token]
- Block comments: [tokens, or "not supported"]
- Nesting: [supported or not]
- [Harness note: comment_prefix in tests/lab*/manifest.json is set to the
  token above.]

## Whitespace and termination

- Whitespace significant: [yes or no, and where]
- Statement terminator: [e.g. semicolon, newline, none]
- Block delimiters: [e.g. braces, indentation]
- Grouping delimiters: [e.g. parentheses]

## Token output format

```
[one line of real --tokenize output]
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
[one real static error]
[one real runtime error]
```


| Failure | Exit code |
|---|---|
| [lexical error] | 65 |
| [syntax error] | 65 |
| [runtime error] | 70 |


## Testing conventions


| Folder | Activity | Mode | Flag |
|---|---|---|---|
| tests/lab1 | Scanner | sidecar | `--tokenize` |
| tests/lab2 | Parser | sidecar | `--parse` |
| tests/lab3 | Evaluator | inline | `--eval` |
| tests/lab4 | Context | inline | none |
| tests/lab5 | Functions | inline | none |


```
[specific tests]...
```

Run locally with:

```bash
curl -sSL https://raw.githubusercontent.com/WhiteLicorice/cmsc-124-harness/v1.1/run_tests.py -o run_tests.py
./build.sh
python3 run_tests.py tests/lab1
```

## Sample code

```
[a short program]
```

Output:

```
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
| Lab 1 | [entry] |
