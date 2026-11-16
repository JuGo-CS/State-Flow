import kotlin.system.exitProcess

fun main(args: Array<String>) {
    when {
        args.size == 2 && args[0] == "--tokenize" -> runTokenFile(args[1])
        args.size == 2 && args[0] == "--eval" -> runEvalFile(args[1])
        args.size == 2 && args[0] == "--parse" -> runParseFile(args[1])
        args.size == 1 -> runFile(args[0])
        args.isEmpty() -> runPrompt()
        else -> {
            exitProcess(64)
        }
    }
}

private fun readSourceFile(path: String): String {
    return try {
        java.io.File(path).readText()
    } catch (e: java.io.IOException) {
        System.err.println("Could not read file: $path")
        exitProcess(66)
    }
}

// scan source and return token list
fun scan(source: String): List<Token>? {
    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()

    if (scanner.hadError) {
        return null
    }

    return tokens
}

// tokenizer
fun runTokenFile(path: String) {
    val source = readSourceFile(path)
    val tokens = scan(source) ?: exitProcess(65)

    for (token in tokens) {
        println(token)
    }

    exitProcess(0)
}

// parser
fun runParseFile(path: String) {
    val source = readSourceFile(path)
    val tokens = scan(source) ?: exitProcess(65)

    val parser = Parser(tokens)
    val program = parser.parse()

    if (parser.hadError) {
        exitProcess(65)
    }

    // an empty or comment-only file prints nothing
    if (program.expressions.isNotEmpty()) {
        println(AstPrinter().print(program))
    }
    exitProcess(0)
}

// evaluator (file mode)
fun runEvalFile(path: String) {
    val source = readSourceFile(path)
    val tokens = scan(source) ?: exitProcess(65)

    val parser = Parser(tokens)
    val program = parser.parse()

    if (parser.hadError) {
        exitProcess(65)   // static error: rejected before running anything
    }

    val interpreter = Interpreter()
    for (e in program.expressions) {
        if (!interpreter.interpret(e)) {
            exitProcess(70)   // runtime error: started, then failed
        }
    }
    exitProcess(0)
}

// plain run (statements)
fun runFile(path: String) {
    val source = readSourceFile(path)
    val tokens = scan(source) ?: exitProcess(65)

    val parser = StatementParser(tokens)
    val statements = parser.parseStatements()
    if (parser.hadError) {
        exitProcess(65)   // syntax error
    }

    val checker = Checker(statements)
    checker.check()
    if (checker.hadError) {
        exitProcess(65)   // static error: START/END, goto targets
    }

    val interpreter = Interpreter()
    if (!interpreter.runProgram(statements)) {
        exitProcess(70)   // runtime error: started, then failed
    }
    exitProcess(0)
}

// REPL
fun runPrompt() {
    val interpreter = Interpreter()   // one for the whole session, so variables persist
    print("> ")
    var line = readlnOrNull()

    while (line != null) {
        evalAndPrintLine(line, interpreter)
        print("> ")
        line = readlnOrNull()
    }

    println()
}

// evaluator (REPL, one line)
fun evalAndPrintLine(source: String, interpreter: Interpreter) {
    val tokens = scan(source) ?: return

    val parser = StatementParser(tokens)
    val statements = parser.parseRepl()
    if (parser.hadError) {
        return
    }

    // error already printed inside; just show the prompt again
    interpreter.runRepl(statements)
}