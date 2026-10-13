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
- Requires a JDK and Python 3 (the test harness is a Python script). Run `./build.sh` once before using `./run`.

## Running it

| Command | What it does |
|---|---|
| `./run <file>` | [Executes a program. Available from Lab 4.] |
| `./run --tokenize <file>` | Scans the source file and prints its token stream. Available from Lab 1. |
| `./run --parse <file>` | Parses the file and prints one tree per `;`-terminated expression, in prefix parenthesized form. Available from Lab 2. |
| `./run --eval <file>` | Parses the file, then evaluates each expression in order and prints its value, one per line. A lexical or syntax error rejects the whole file before anything runs (exit 65). A runtime error stops evaluation (exit 70). Available from Lab 3. |
| `./run` | Starts the REPL: reads one line of input at a time, evaluates it and prints each expression's value (same output as `--eval`, applied per line). Each expression must end with `;`, e.g. `1 + 2;`. A syntax or runtime error prints its message and returns the prompt. Variables persist across lines. |

Any other combination of arguments prints a usage line to stderr and exits with code 64.

## Exit codes
- 0 - A successful scan, parse, or evaluation exits with code 0.
- 64 - Incorrect command-line usage, such as an unknown flag or the wrong number of arguments.
- 65 - The file was read successfully, but the scanner found invalid or malformed input, or the parser found a syntax error.
- 66 - The program could not read the file, such as when the file does not exist or cannot be opened.
- 70 - The file scanned and parsed successfully, but evaluation failed with a runtime error (for example, `-"hi"` or `1 / 0`). Used starting in Lab 3.

Runtime errors are reported after evaluation has started, in this format:

```
[line 2] Runtime error: Operand must be a number.
```

The line is the line of the operator (or variable name) that failed. The messages are:

- `Operand must be a number.` (unary `-`)
- `Operands must be numbers.` (`-`, `*`, `/`, `<`, `<=`, `>`, `>=`)
- `Operands must be two numbers or at least one string.` (`+`)
- `Division by zero.`
- `Undefined variable 'x'.`

Static errors (lexical, syntax) reject the whole file before anything runs, with empty stdout. A runtime error is different: values printed by earlier expressions stay printed, evaluation stops at the failing expression, and the exit code is 70. In the REPL a runtime error prints its message and returns the prompt.

## File extension

StateFlow source files use the `.sf` extension.

## Lexical structure

### Keywords
The following words are reserved and cannot be used as identifiers.

| Keyword | Purpose |
| :--- | :--- |
| **state** | Declares a program state. |
| **goto** | Transitions execution to another state. |
| **requires** | Declares a condition that must hold when a state is entered; if it does not, the program stops with a runtime error. |
| **var** | Declares a variable. |
| **global** | Declares a variable as used in multiple states. |
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

> **Current Scope note:** this table lists the full language's reserved words. **Lab 2's grammar (below) covers only expressions** -- literals, variables, unary/binary operators, grouping, and assignment. Statement-level constructs (`var`, `print`, `if`, `while`, `for`, `state`, `goto`, `requires`, `global`) are scanned as keywords, and their AST node definitions exist in `Statement.kt`, but the parser does not parse them yet. See Known limitations.

### Operators

| Operator | Category | Operands | Associativity | Precedence |
|---|---|---|---|---|
| `!` | logical | unary | right | 5 (tightest, with grouping/literals) |
| `-` | arithmetic | unary (negation) | right | 5 |
| `*` | arithmetic | binary | left | 4 |
| `/` | arithmetic | binary | left | 4 |
| `+` | arithmetic | binary | left | 3 |
| `-` | arithmetic | binary | left | 3 |
| `>` | comparison | binary | left | 2 |
| `>=` | comparison | binary | left | 2 |
| `<` | comparison | binary | left | 2 |
| `<=` | comparison | binary | left | 2 |
| `!=` | equality | binary | left | 1 (loosest) |
| `==` | equality | binary | left | 1 (loosest) |
| `=` | assignment | binary | right | 0 (below equality, loosest of all) |

Precedence numbers follow the grammar below: level 0 (`assignment`) is the loosest, level 1 (`equality`) comes next, and level 5 (`unary`) is evaluated first/tightest, matching the convention "lower number = looser."

### Literals

| Kind | Syntax | Produces |
|---|---|---|
| Number | `42`, `3.14` | A numeric literal (double-precision). No leading-dot decimals (`.42` scans as DOT then NUMBER), no trailing-dot folding (`42.` scans as NUMBER then DOT), no underscore separators, and no unary-minus folding (`-5` scans as MINUS then NUMBER). |
| String | `"hello"` | A string literal. Supports `\n`, `\t`, `\\`, `\"` escapes and legal multi-line strings. An unrecognized escape sequence is a lexical error. Empty string `""` is legal. |
| Boolean | `true`, `false` | A boolean literal |
| Nil | `nil` | A nil value |


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


Each token prints as one line with four fields:

```
Token(type=VAR, lexeme=var, literal=null, line=1)
```

| Field | Meaning |
|---|---|
| `type` | The token's category, such as `VAR`, `IDENTIFIER`, `NUMBER` or `PLUS`. |
| `lexeme` | The exact source text the token was scanned from. For strings it includes the surrounding quotes and unprocessed escapes, e.g. `"hi\n"`. The `EOF` token has an empty lexeme. |
| `literal` | The token's value, or `null` if it has none. `NUMBER` tokens carry a Kotlin `Double` (`42` is `42.0`). `STRING` tokens carry the string contents without quotes, with escapes resolved. Every other token, including `true`, `false` and `nil`, has `null`. |
| `line` | The line number the token ends on, counting from 1. A string that spans several lines reports the line of its closing quote. |

## Grammar

```
program    -> ( expression ";" )* EOF
expression -> assignment
assignment -> IDENTIFIER "=" assignment | equality
equality   -> comparison ( ( "!=" | "==" ) comparison )*
comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )*
term       -> factor ( ( "-" | "+" ) factor )*
factor     -> unary ( ( "/" | "*" ) unary )*
unary      -> ( "!" | "-" ) unary | primary
primary    -> NUMBER | STRING | IDENTIFIER | "true" | "false" | "nil" | "(" expression ")"
```

The first rule is the file-splitting rule: a file is a sequence of expressions, and **every** expression ends with `;`, including the last one. The `assignment` rule is right-recursive on purpose, so `a = b = c` means `a = (b = c)`. The left side of `=` is parsed as an ordinary expression first and then checked: if it is not a plain identifier (e.g. `1 + 2 = 3;`), the parser reports `Invalid assignment target.` and carries on. `and`/`or` are reserved keywords but not part of this grammar -- no logical-expression rule exists between `assignment` and `equality`.

> **Beyond Lab 2:** statement-level constructs (`var`, `print`, `if`, `while`, `for`, `global`, `state`, `goto`, `requires`) are not parsed yet (Lab 4/5). This grammar describes only what the parser currently accepts.

## Parse output format

```
(+ 1.0 (* 2.0 3.0))
```

- Groupings print as: `(group <expr>)`
- Numbers print as: Kotlin's default `Double.toString()` -- a whole number keeps its decimal point, e.g. `2.0`, not `2`
- Strings print as their raw content, with no surrounding quotes, e.g. `hello`
- Booleans print as `true` / `false`; `nil` prints as `nil`
- Variables print as their name, e.g. `x`
- Assignments print as `(= <name> <expr>)`, e.g. `x = 1;` prints `(= x 1.0)`

## File-splitting rule

Every expression in a file must be terminated by `;`, including the last one. Newlines are ignored, so one expression may span several lines and several expressions may share a line. `--parse` prints one tree per expression, in file order, one per output line. This is the semicolon-terminator option the activity names as an alternative to one-expression-per-line, and it matches this language's own `;` statement terminator.

- A missing `;` is a syntax error: `Expect ';' after expression.`
- A stray `;` with no expression before it (e.g. `;;`) is a syntax error: `Expect expression.`
- An empty file (or one holding only comments) is valid: nothing is printed and the exit code is 0.
- The same rule applies in the REPL, one line at a time.

## Semantics

### Values and types

StateFlow is dynamically typed, so variables and state parameters do not
require declared types. The runtime supports the following value kinds:

- **Numbers** — represented as Kotlin `Double` values. Whole-number literals such
  as `42` are therefore represented as `42.0` internally.
- **Strings** — represented as Kotlin `String` values.
- **Booleans** — represented by the `true` and `false` values.
- **Nil** — represents the absence of a value and is represented as Kotlin
  `null`.

At runtime every value is held as a Kotlin `Any?` that is a `Double`, a
`String`, a `Boolean` or `null`. The evaluator finds out which one it has with
runtime checks (`isDouble`, `isString`, and so on), which is what lets `+`
choose between addition and concatenation.

### Value printing

- **Numbers:** a whole number prints without a decimal point (`5`, not `5.0`; `-3`, not `-3.0`). Any other number prints its shortest decimal form (`2.5`). Numbers of magnitude 10^15 or more, and very small fractions, fall back to Kotlin's scientific form (e.g. `1.0E15`). This differs from `--parse`, which prints number literals as `2.0` (see Parse output format).
- **Nil:** printed as `nil`.
- **Strings:** printed as their contents without surrounding quotation marks.
- **Booleans:** printed as `true` or `false`.

### Truthiness

The values `nil`, `false`, the number `0` and the empty string `""` are false
in a condition. Every other value is true, including `-1`, `"0"`, `" "` and
`"false"`. The `!` operator applies this rule and always returns a boolean:
`!0` is `true` and `!"a"` is `false`. The same rule will be used by `if`,
`while` and the logical operators once they are parsed. Equality does not use
truthiness: `0 == false` is `false`.

### Operator semantics

- **Arithmetic:** `-`, `*`, `/` and unary `-` accept numbers only. Any other
  operand type is a runtime error.
- **`+` on strings:** `+` is the language's one overloaded operator. If either
  operand is a string, the result is concatenation, and the other operand is
  converted to text with the value-printing rules above: `"n: " + 5` is
  `n: 5` and `"a" + nil` is `anil`. If both operands are numbers it adds.
- **Mixed types:** `+` on any other combination (e.g. `1 + true`, `nil + nil`)
  is a runtime error, and so is any mix of types for `-`, `*`, `/` and the
  comparisons. Only `+` converts. `+` is left-associative, so `1 + 2 + "a"`
  is `3a` while `"a" + 1 + 2` is `a12`.
- **Comparison:** `<`, `<=`, `>` and `>=` accept numbers only. Comparing
  strings is a runtime error.
- **Equality across types:** `==` and `!=` never fail. Values of different
  types are never equal (`1 == "1"` and `0 == false` are both `false`);
  `nil == nil` is `true`. No conversion happens.
- **Division by zero:** a runtime error, `Division by zero.` It is not
  `Infinity`, so a bad calculation stops the program.
- **Evaluation order:** left to right. Both operands of a binary operator are
  evaluated, the left one first, before the operator is applied.
- **Assignment:** `a = b = 3;` assigns `3` to `b`, then to `a`, and produces
  `3`. The right side is evaluated before the assignment happens.

### Scope and bindings

StateFlow distinguishes between state-local variables, state parameters, and
explicitly declared global variables.

- **State-local variables:** A variable declared inside a state is accessible
  only within that state.
- **State parameters:** A state may explicitly receive values through its
  parameters. These provide a controlled way of passing state-specific data
  between states rather than allowing one state to directly access another
  state's local variables.
- **Global variables:** A variable is shared across states only when it is
  explicitly declared as global. Variables are not automatically global merely
  because they are declared outside a particular state.
- **Redeclaration in the same scope:** [allowed or an error]
- **Uninitialized variable holds:** [value]
- **Shadowing:** [behavior]
- **Undefined name:** a runtime error with exit 70, `Undefined variable 'x'.`
  This applies to reading a name that was never declared and to assigning to
  one (`x = 5;` does not create `x`; variables are declared with `var`).

The distinction between parameters and globals is intentional. Parameters are
used when a value is specific to a state transition and should be passed
explicitly, while global variables are used when a value is intentionally
shared across multiple states. This avoids requiring every shared value to be
passed through parameters while preventing all state-local variables from being
implicitly accessible everywhere.

### Control flow and functions

The state rules below describe the language design. They will be implemented from
Lab 4 and Lab 5 onward; the parser does not accept `state`, `goto` or
`requires` yet.

- **State entry:** Executable programs must contain a `START` state as their
  entry point and an `END` state as their termination state. Every state except
  `END` must explicitly specify a transition using `goto`.
- **State transitions:** A `goto` must reference an existing state. State
  parameters may receive values explicitly supplied by the transition, as in
  `goto MENU(5);` for `state MENU(myVar) { ... }`. Arguments are evaluated left
  to right, in the calling state, before the transition happens. A wrong number
  of arguments is a static error (exit 65). `START` takes no parameters. A
  `goto` never returns a value and cannot appear inside an expression, which
  is what keeps a state different from a function. Transitions are run in a
  loop rather than by recursive calls, so a long-running flow cannot overflow
  the stack.
- **State requirements:** `requires <expr>;`  states a condition
  that must hold when a state is entered. It may appear only at the top of a
  state body and is checked in order, with the state's parameters in scope. A
  failed requirement is a runtime error (exit 70) that names the state. It is
  for catching bad arguments or invalid program state, not for choosing
  between branches; use `if` for that. It uses the truthiness rule above.
- **Logical operators return:** [booleans, or the operand]
- **Dangling else binds to:** [which if]
- **Closure capture of a loop variable:** [per iteration, or shared]
- **Function with no return statement produces:** [value]
- **Arity mismatch:** [message and exit code]

## Native functions

| Name | Arguments | Returns | Notes |
|---|---|---|---|
| [name] | [count and types] | [type] | [caveats] |

## Errors and diagnostics

An unterminated string or an illegal character does not stop scanning
immediately. The scanner keeps consuming input, so multiple errors in
one file are all reported to stderr in a single run (see
`tests/lab1/multiple_illegal_characters.sf`).

If any lexical error occurred, **no tokens are printed to stdout at
all**, stdout stays empty, exit code is 65. A file either scans clean
(full token stream + EOF + exit 0) or is rejected outright (empty
stdout + exit 65). There is no partial-output mode.

Message format:

```
[line 3] Error: Unexpected character: '@'
[line 1] Error at ')': Expect expression.
[line 1] Error at end: Expect ';' after expression.
```

The first is a scanner (lexical) error. The second and third are parser (syntax) errors: `Error at '<lexeme>'` when a specific bad token was found, `Error at end` when the parser ran out of tokens before finding what it needed. After a syntax error the parser skips ahead to just past the next `;` and keeps parsing, so several syntax errors in one file are all reported in a single run.

| Failure | Exit code |
|---|---|
| Bad command-line usage | 64 |
| Lexical error | 65 |
| Syntax error | 65 |
| File read error | 66 |
| Runtime error | 70 |

## Testing conventions

| Folder | Activity | Mode | Flag |
|---|---|---|---|
| tests/lab1 | Scanner | sidecar | `--tokenize` |
| tests/lab2 | Parser | sidecar | `--parse` |
| tests/lab3 | Evaluator | inline | `--eval` |
| tests/lab4 | Context | inline | none |
| tests/lab5 | Functions | inline | none |

`tests/lab2/` covers every literal kind, the `term` level (including left-associativity, `1 - 2 - 3`), the `factor` level (including precedence over `term`, `2 + 3 * 4`), comparison, equality (including a comparison nested inside an equality), unary (including double negation, `!!true`), simple and nested grouping, one expression exercising the full six-level chain, and syntax-error cases (unclosed paren, dangling operator, missing `;`) asserting exit 65 with empty stdout. Every expression in every `tests/lab2/*.sf` file ends with `;`.

`tests/lab3/` uses inline annotations: each expression ends with `;` followed by `// expect: <value>`, so the source and its expected output sit together. It covers literals, arithmetic and precedence, whole-number printing, string concatenation and coercion (including left-to-right `1 + 2 + "a"` versus `"a" + 1 + 2`), comparison, equality across types, truthiness through `!`, unary negation, output printed before a runtime error, one runtime-error case per message (exit 70), and a syntax error in a file that also holds a valid expression, to show that nothing runs (exit 65). Expected values were worked out by hand from the rules in Semantics, not copied from the interpreter's output.

Run locally with:

```bash
curl -sSL https://raw.githubusercontent.com/WhiteLicorice/cmsc-124-harness/v1.1/run_tests.py -o run_tests.py
./build.sh
python3 run_tests.py tests/lab0
python3 run_tests.py tests/lab1
python3 run_tests.py tests/lab2
python3 run_tests.py tests/lab3
```

## Sample code

```
state START {
  var name = input();
  goto GREETING(name);
}

state GREETING(name) {
  requires name != nil;

  print "Hello, " + name;
  goto END;
}

state END {
}
```

```
Output (when the user types Leona):

Hello, Leona
```

> **Note:** this sample describes the target language design but goes well beyond Lab 2's actual scope, and doesn't fully parse yet even with the ahead-of-schedule statement work -- `input()` needs function-call syntax, which isn't implemented. A Lab-2-accurate sample is a semicolon-terminated expression, e.g. `(1 + 2) * 3 == 9;` -> `(== (* (group (+ 1.0 2.0)) 3.0) 9.0)`.

## Design rationale

StateFlow was designed around the idea that some programs naturally move through a sequence of stages. Instead of treating this movement as ordinary control flow hidden inside conditionals and loops, the language makes the current state and transitions between states explicit through `state` and `goto`. This was chosen because the intended programs include interactive applications, transaction workflows, menu-driven programs, quizzes, and other programs where execution naturally moves from one stage to another. Requiring a `START` state as the entry point and an `END` state as the termination point further makes the overall flow of an executable StateFlow program explicit.

We chose a readable syntax inspired by languages such as Python while retaining semicolon-terminated statements and braces for blocks. The goal was to make the language approachable without removing clear boundaries between statements and blocks. Semicolons also provide an unambiguous way to separate expressions when parsing a file, rather than making newlines determine where an expression ends.

State transitions were designed to use `goto` rather than requiring programmers to represent every stage using nested conditionals. A transition explicitly identifies the next state, making the control-flow structure visible in the source code. We also introduced `requires` as a condition associated with entering a state. This separates the condition for entering a state from the actions performed inside that state, which fits the language's focus on state-based execution. A failed requirement is a runtime error rather than a silent skip or a fallback transition, because `requires` is meant to catch bad arguments or invalid program state, not to choose between branches; that is what `if` is for.

A major design decision concerns how data is accessed between states. We considered omitting state parameters and relying on shared variables instead. This would make state declarations and transitions shorter, but it would also make dependencies between states less explicit. A state could use a value without showing where that value came from, and it could become difficult to determine which state owns or modifies a variable. We therefore decided to keep state parameters so that state-specific values can be passed explicitly during a transition. For example, `goto GREETING(name);` makes it clear that `GREETING` receives `name` from the preceding state.

At the same time, we decided that parameters should not be the only mechanism for sharing data. Passing the same value through many states can become repetitive when the value is intentionally shared by the entire program. We therefore chose to support explicitly declared global variables. A variable is not global simply because it exists outside the current state; the programmer must explicitly declare it as global. This provides a distinction between values that are intentionally shared and values that belong only to a particular state.

The resulting scope model favors explicit access. State-local variables belong to their declaring state, state parameters represent values deliberately passed into a state, and explicitly declared global variables represent values intended to be shared. This was chosen as a compromise between two extremes: making state data freely accessible everywhere and forcing every shared value to be passed through state parameters. The former makes data ownership difficult to track, while the latter can create unnecessary parameter passing for genuinely shared information.

The language is dynamically typed. We chose not to require type declarations for variables and state arguments because the intended programs benefit from a relatively lightweight syntax, and requiring programmers to specify types would add syntax without being necessary for the initial language design. Runtime values include numbers, strings, booleans, and `nil`, allowing the language to support both simple calculations and interactive programs.

The expression grammar uses separate precedence levels for equality, comparison, arithmetic terms, factors, unary operators, and primary expressions. This structure was chosen so that familiar expressions such as `2 + 3 * 4` can be interpreted according to conventional precedence rules without requiring excessive parentheses. Binary operators are left-associative where appropriate, while unary operators are right-associative. Assignment is treated differently from ordinary arithmetic and comparison operations because its purpose is to update a variable binding rather than simply combine two independently evaluated values.

Assignment is represented as its own expression form in the AST (`Expr.Assign`), parsed by an `assignment` rule that sits above `equality`. A variable expression represents reading the value associated with a name, while an assignment represents writing a value to that name. Keeping these concepts distinct prevents the left side of an assignment from being treated simply as another expression to evaluate. Assignment also produces the value that was assigned, allowing it to participate in larger expressions.

`+` is deliberately the only overloaded operator. Allowing `"n: " + 5` makes building output text convenient, while keeping `-`, `*`, `/` and the comparisons strict means a type mistake in arithmetic still fails loudly instead of silently producing a surprising value. Equality never converts either: `1 == "1"` is simply `false`. Truthiness follows Python's convention (`nil`, `false`, `0` and `""` are false) so conditions on counters and strings read naturally. Division by zero is a runtime error rather than `Infinity`, so a bad calculation stops the program. Operands are evaluated left to right so that the order of side effects matches the order of the source text.

We included common programming constructs such as `var`, `if`, `else`, `while`, `for`, `fun`, `return`, `print`, and `input` because StateFlow is intended to be a general-purpose language rather than a language limited to state transitions. States provide the higher-level structure of a program, while these constructs provide the ordinary computation and interaction needed within those states. Logical operators such as `and` and `or` were also reserved as part of the intended language design, although they are not yet included in the Lab 2 expression grammar.

Some features were deliberately kept out of the current Lab 2 grammar. The Lab 2 grammar focuses only on expressions, covering literals, variables, grouping, unary operators, arithmetic, comparison, equality, and assignment. Statement-level constructs are scanned as keywords and have AST node definitions, but the parser does not handle them yet. This keeps the documented grammar identical to what the parser accepts.

We also chose to use `.sf` as the source-file extension and reserve `START` and `END` as state names. These conventions distinguish StateFlow source files from ordinary files and establish consistent entry and termination points for executable programs. Similarly, identifiers are case-sensitive and cannot use reserved keywords, preventing ambiguity between language constructs and user-defined names.

The language currently uses `//` for line comments and does not support block comments. This keeps the lexical rules small while still providing a basic mechanism for documenting code. Block comments were not considered necessary for the current implementation and can be added later without changing the fundamental execution model.

The implementation is written in Kotlin, while StateFlow itself remains independent of the implementation language. Kotlin was chosen as the host language for the compiler/interpreter implementation, while the language design focuses on the syntax and semantics experienced by StateFlow programmers.

Finally, the language has evolved incrementally alongside the laboratory activities. Lab 1 established the scanner and lexical structure, while Lab 2 introduced the recursive-descent expression parser and AST, and Lab 3 added the evaluator. Statement-level and state-related features are part of the intended language but are not parsed yet. The design therefore distinguishes between the current implementation and the eventual target language, and the Grammar section only ever describes the former.


## Known limitations

- Lab 2's grammar (documented above) covers expressions only. Statement-level constructs (`var`, `print`, `if`, `state`, `goto`, ...) are scanned but not parsed yet.
- Every expression in a file, including the last, must end with `;` -- there's no bare "one expression per line" mode, and the REPL needs the `;` too.
- `and` / `or` are reserved and scanned, but not wired into the expression grammar -- no logical-expression precedence level exists above `equality`.
- Function-call syntax (`input()`, etc.), `var` and state declarations are not implemented yet, so the Sample code above can't run.
- `./run <file>` is not implemented yet (Lab 4).
- `var` declarations are not parsed yet, so no variable can exist. Until Lab 4, every variable read or assignment is a runtime error (`Undefined variable 'x'.`).
- Whole numbers of magnitude 10^15 or more, and very small fractions, print in Kotlin's scientific notation (`1.0E15`) instead of plain digits.
- Leading-dot decimal literals (`.42`) are not recognized as one token.
- Trailing-dot literals (`42.`) are not folded into the number.
- Underscore digit separators (`4_200`) are not supported.
- Unary minus is not folded during scanning (`-5` is two tokens; sign
  handling is deferred to the parser).
- Block comments (`/* */`) are not implemented [line comments only].

## Changelog

| Activity | What changed in the language |
|---|---|
| Lab 1 | Added the scanner and defined the lexical structure of StateFlow, including keywords, identifiers, literals, operators, punctuation, whitespace, comments, and lexical error handling. Established `START` as the required entry state and `END` as the required termination state. |
| Lab 2 | Added the parser: a recursive-descent expression grammar (`assignment` -> `equality` -> `comparison` -> `term` -> `factor` -> `unary` -> `primary`) producing an `Expr` AST, plus `AstPrinter` for `--parse`'s prefix-parenthesized output. Every expression must be terminated by `;`. The REPL now prints parses instead of tokens. |
| Lab 3 | Added the evaluator: `Interpreter` walks the `Expr` tree and computes values (numbers, strings, booleans, nil), `RuntimeError` reports failures with a line number (exit 70), and `Environment` holds variables. Added `--eval`, and the REPL now prints values. `+` concatenates when either side is a string. Truthiness, value printing, operator typing and division by zero are specified in Semantics. |
