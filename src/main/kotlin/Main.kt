import kotlin.system.exitProcess

fun main(args: Array<String>) {
    when {
        args.size == 2 && args[0] == "--tokenize" -> runTokenFile(args[1])
        args.size == 2 && args[0] == "--eval" -> runEvalFile(args[1])
        args.size == 2 && args[0] == "--parse" -> runParseFile(args[1])
        args.size == 1 -> println("Hello, maayong buntag!")
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

    println(AstPrinter().print(program))
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


// REPL
fun runPrompt() {
    val interpreter = Interpreter()
    print("> ")
    var line = readlnOrNull()

    while (line != null) {
        evalAndPrintLine(line, interpreter)
        print("> ")
        line = readlnOrNull()
    }

    println()
}

fun runScanner(source: String) {
    val tokens = scan(source) ?: return

    for (token in tokens) {
        println(token)
    }
}

fun parseAndPrintLine(source: String) {
    val tokens = scan(source) ?: return

    val parser = Parser(tokens)
    val program = parser.parse()
    if (parser.hadError) {
        return
    }

    println(AstPrinter().print(program))
}

// evaluator (REPL, one line)
fun evalAndPrintLine(source: String, interpreter: Interpreter) {
    val tokens = scan(source) ?: return

    val parser = Parser(tokens)
    val program = parser.parse()
    if (parser.hadError) {
        return
    }

    for (e in program.expressions) {
        if (!interpreter.interpret(e)) {
            return   // error already printed; just show the prompt again
        }
    }
}