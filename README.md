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
| `./run --tokenize <file>` | Scans the source file and prints its token stream. Available from Lab 1. |
| `./run --parse <file>` | Parses the file and prints one tree per expression statement, in prefix parenthesized form. Available from Lab 2. |
| `./run --eval <file>` | [Evaluates each expression and prints its value. Available from Lab 3.] |
| `./run` | Starts the REPL: reads one line of input at a time and prints its token stream (same output as `--tokenize`, applied per line). |

## Exit codes
- 0 - A successful scan (or parse) exits with code 0.
- 65 - The file was read successfully, but the scanner found invalid or malformed input, or the parser found a syntax error.
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
| input | Receives input from the user. |
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

> **Current Scope note:** this table lists the full language's reserved words. **Lab 2's grammar (below) covers only expressions** -- literals, unary/binary operators, and grouping -- per the activity's own scope. Statement-level constructs (`var`, `print`, `if`, `while`, `for`, `state`, `goto`, `requires`, `global`) are implemented in the parser ahead of schedule, but are not part of what Lab 2 grades and are not yet reflected in the Grammar section. See Known limitations.

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
| `=` | assignment | binary | right | not covered by Lab 2's grammar -- implemented, belongs to the statement-level work noted above |

Precedence numbers follow the grammar below: level 1 (`equality`) is evaluated last/loosest, level 5 (`unary`) is evaluated first/tightest, matching the convention "1 = loosest."

### Literals

| Kind | Syntax | Produces |
|---|---|---|
| Number | `42`, `3.14` | A numeric literal (double-precision). No leading-dot decimals (`.42` scans as DOT then NUMBER), no trailing-dot folding (`42.` scans as NUMBER then DOT), no underscore separators, and no unary-minus folding (`-5` scans as MINUS then NUMBER). |
| String | `"hello"` | A string literal. Supports `\n`, `\t`, `\\`, `\"` escapes and legal multi-line strings. An unrecognized escape sequence is a lexical error. Empty string `""` is legal. |
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
expression -> equality
equality   -> comparison ( ( "!=" | "==" ) comparison )*
comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )*
term       -> factor ( ( "-" | "+" ) factor )*
factor     -> unary ( ( "/" | "*" ) unary )*
unary      -> ( "!" | "-" ) unary | primary
primary    -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")"
```

One expression per `--parse`-able unit, as described in the current activity. `and`/`or` are reserved keywords but not part of this grammar -- no logical-expression rule exists above `equality`.

> **Beyond Lab 2:** the parser also accepts statement-level constructs (`var`, `print`, `if`, `while`, `for`, `global`, `state`, `goto`, `requires`) built ahead of the activities that actually require them (Lab 4/5). That grammar is intentionally not documented here, since it isn't what Lab 2 grades and documenting it here would make this section describe more than the activity asked for. It belongs in this README once the corresponding lab is in scope.

## Parse output format

```
(+ 1.0 (* 2.0 3.0))
```

- Groupings print as: `(group <expr>)`
- Numbers print as: Kotlin's default `Double.toString()` -- a whole number keeps its decimal point, e.g. `2.0`, not `2`
- Strings print as their raw content, with no surrounding quotes, e.g. `hello`
- Booleans print as `true` / `false`; `nil` prints as `nil`

## File-splitting rule

Each expression in a file must be a complete statement terminated by `;`. `--parse` prints one tree per expression statement, in file order, one per output line. This is the semicolon-terminator option the activity names as an alternative to one-expression-per-line (and happens to match this language's own `;` statement terminator, already documented above), rather than relying on newlines to separate expressions.

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

### Value printing

- **Numbers:** [e.g. 5 rather than 5.0]
- **Nil:** printed as `nil`.
- **Strings:** printed as their contents without surrounding quotation marks.
- **Booleans:** printed as `true` or `false`.

### Truthiness

[The complete rule. Which values are false in a condition; everything else is
true.]

### Operator semantics

- **Arithmetic:** [accepted operand types]
- **`+` on strings:** [concatenation, error, or coercion]
- **Mixed types:** [what happens]
- **Comparison:** [accepted operand types]
- **Equality across types:** [false, or an error]
- **Division by zero:** [value produced, or runtime error]

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
- **Undefined name:** [static error with exit 65, or runtime error with exit 70]

The distinction between parameters and globals is intentional. Parameters are
used when a value is specific to a state transition and should be passed
explicitly, while global variables are used when a value is intentionally
shared across multiple states. This avoids requiring every shared value to be
passed through parameters while preventing all state-local variables from being
implicitly accessible everywhere.

### Control flow and functions

- **State entry:** Executable programs must contain a `START` state as their
  entry point and an `END` state as their termination state. Every state except
  `END` must explicitly specify a transition using `goto`.
- **State transitions:** A `goto` must reference an existing state. State
  parameters may receive values explicitly supplied by the transition.
- **State requirements:** A `requires` condition defines a condition that must
  be satisfied before entering the associated state.
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

`tests/lab2-statements/` (supplementary, not part of the graded Lab 2 suite) covers the ahead-of-schedule statement grammar noted above.

Run locally with:

```bash
curl -sSL https://raw.githubusercontent.com/WhiteLicorice/cmsc-124-harness/v1.1/run_tests.py -o run_tests.py
./build.sh
python3 run_tests.py tests/lab0
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

> **Note:** this sample describes the target language design but goes well beyond Lab 2's actual scope, and doesn't fully parse yet even with the ahead-of-schedule statement work -- `input()` needs function-call syntax, which isn't implemented. A Lab-2-accurate sample is a semicolon-terminated expression, e.g. `(1 + 2) * 3 == 9;` -> `(== (* (group (+ 1.0 2.0)) 3.0) 9.0)`.

## Design rationale

StateFlow was designed around the idea that some programs naturally move through a sequence of stages. Instead of treating this movement as ordinary control flow hidden inside conditionals and loops, the language makes the current state and transitions between states explicit through `state` and `goto`. This was chosen because the intended programs include interactive applications, transaction workflows, menu-driven programs, quizzes, and other programs where execution naturally moves from one stage to another. Requiring a `START` state as the entry point and an `END` state as the termination point further makes the overall flow of an executable StateFlow program explicit.

We chose a readable syntax inspired by languages such as Python while retaining semicolon-terminated statements and braces for blocks. The goal was to make the language approachable without removing clear boundaries between statements and blocks. Semicolons also provide an unambiguous way to separate expressions when parsing a file, rather than making newlines determine where an expression ends.

State transitions were designed to use `goto` rather than requiring programmers to represent every stage using nested conditionals. A transition explicitly identifies the next state, making the control-flow structure visible in the source code. We also introduced `requires` as a condition associated with entering a state. This separates the condition for entering a state from the actions performed inside that state, which fits the language's focus on state-based execution.

A major design decision concerns how data is accessed between states. We considered omitting state parameters and relying on shared variables instead. This would make state declarations and transitions shorter, but it would also make dependencies between states less explicit. A state could use a value without showing where that value came from, and it could become difficult to determine which state owns or modifies a variable. We therefore decided to keep state parameters so that state-specific values can be passed explicitly during a transition. For example, `goto GREETING(name);` makes it clear that `GREETING` receives `name` from the preceding state.

At the same time, we decided that parameters should not be the only mechanism for sharing data. Passing the same value through many states can become repetitive when the value is intentionally shared by the entire program. We therefore chose to support explicitly declared global variables. A variable is not global simply because it exists outside the current state; the programmer must explicitly declare it as global. This provides a distinction between values that are intentionally shared and values that belong only to a particular state.

The resulting scope model favors explicit access. State-local variables belong to their declaring state, state parameters represent values deliberately passed into a state, and explicitly declared global variables represent values intended to be shared. This was chosen as a compromise between two extremes: making state data freely accessible everywhere and forcing every shared value to be passed through state parameters. The former makes data ownership difficult to track, while the latter can create unnecessary parameter passing for genuinely shared information.

The language is dynamically typed. We chose not to require type declarations for variables and state arguments because the intended programs benefit from a relatively lightweight syntax, and requiring programmers to specify types would add syntax without being necessary for the initial language design. Runtime values include numbers, strings, booleans, and `nil`, allowing the language to support both simple calculations and interactive programs.

The expression grammar uses separate precedence levels for equality, comparison, arithmetic terms, factors, unary operators, and primary expressions. This structure was chosen so that familiar expressions such as `2 + 3 * 4` can be interpreted according to conventional precedence rules without requiring excessive parentheses. Binary operators are left-associative where appropriate, while unary operators are right-associative. Assignment is treated differently from ordinary arithmetic and comparison operations because its purpose is to update a variable binding rather than simply combine two independently evaluated values.

Assignment is represented as its own expression form in the AST. A variable expression represents reading the value associated with a name, while an assignment represents writing a value to that name. Keeping these concepts distinct prevents the left side of an assignment from being treated simply as another expression to evaluate. Assignment also produces the value that was assigned, allowing it to participate in larger expressions.

We included common programming constructs such as `var`, `if`, `else`, `while`, `for`, `fun`, `return`, `print`, and `input` because StateFlow is intended to be a general-purpose language rather than a language limited to state transitions. States provide the higher-level structure of a program, while these constructs provide the ordinary computation and interaction needed within those states. Logical operators such as `and` and `or` were also reserved as part of the intended language design, although they are not yet included in the Lab 2 expression grammar.

Some features were deliberately kept out of the current Lab 2 grammar even though parts of their implementation already exist. The Lab 2 grammar focuses only on expressions, covering literals, grouping, unary operators, arithmetic, comparison, and equality. Statement-level constructs were implemented ahead of schedule so that later work could build on them, but they are not documented as part of the graded Lab 2 grammar. This distinction was kept in the README to avoid making the documented grammar larger than the scope of the activity.

We also chose to use `.sf` as the source-file extension and reserve `START` and `END` as state names. These conventions distinguish StateFlow source files from ordinary files and establish consistent entry and termination points for executable programs. Similarly, identifiers are case-sensitive and cannot use reserved keywords, preventing ambiguity between language constructs and user-defined names.

The language currently uses `//` for line comments and does not support block comments. This keeps the lexical rules small while still providing a basic mechanism for documenting code. Block comments were not considered necessary for the current implementation and can be added later without changing the fundamental execution model.

The implementation is written in Kotlin, while StateFlow itself remains independent of the implementation language. Kotlin was chosen as the host language for the compiler/interpreter implementation, while the language design focuses on the syntax and semantics experienced by StateFlow programmers.

Finally, the language has evolved incrementally alongside the laboratory activities. Lab 1 established the scanner and lexical structure, while Lab 2 introduced the recursive-descent expression parser and AST. Some statement-level and state-related features were implemented ahead of the activity that formally requires them. This means that the current implementation contains features that are part of the intended language but are not yet part of the documented or graded grammar. The design therefore intentionally distinguishes between the current implementation, the current laboratory scope, and the eventual target language rather than treating every implemented feature as already finalized.


## Known limitations

- Lab 2's grammar (documented above) covers expressions only. Statement-level constructs are implemented in the parser ahead of the labs that require them, and are explicitly out of scope for what Lab 2 grades.
- Every expression in a file must end with `;` -- there's no bare "one expression per line" mode.
- `and` / `or` are reserved and scanned, but not wired into the expression grammar -- no logical-expression precedence level exists above `equality`.
- Function-call syntax (`input()`, etc.) doesn't exist yet, so the Sample code above doesn't fully parse.
- `--eval` and `./run <file>` are not implemented yet (Lab 3 and Lab 4 respectively).
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
| Lab 2 | Added the parser: a six-level recursive-descent expression grammar (`equality` -> `comparison` -> `term` -> `factor` -> `unary` -> `primary`) producing an `Expr` AST, plus `AstPrinter` for `--parse`'s prefix-parenthesized output. Expressions are terminated by `;`. (Statement-level grammar was also built this cycle, ahead of schedule -- see Known limitations.) |
