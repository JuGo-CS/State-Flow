import kotlin.system.exitProcess

fun main(args: Array<String>) {
    when {
        args.size == 2 && args[0] == "--tokenize" -> runTokenFile(args[1])
        args.size == 2 && args[0] == "--parse" -> runParseFile(args[1])
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
    val tokens = scan(source)

    if (tokens == null) {
        exitProcess(65)
    }

    for (token in tokens) {
        println(token)
    }

    exitProcess(0)
}

// parser
fun runParseFile(path: String) {
    val source = readSourceFile(path)
    val tokens = scan(source)

    if (tokens == null) {
        exitProcess(65)
    }

    val parser = Parser(tokens)
    val exprs = parser.parse()

    if (parser.hadError) {
        exitProcess(65)
    }

    for (e in exprs) {
        println(AstPrinter().print(e))
    }
    exitProcess(0)
}


// REPL
fun runPrompt() {
    print("> ")
    var line = readLine()

    while (line != null) {
        parseAndPrintLine(line)
        print("> ")
        line = readLine()
    }

    println()
}

fun runScanner(source: String) {
    val tokens = scan(source)

    if (tokens == null) {
        return
    }

    for (token in tokens) {
        println(token)
    }
}

fun parseAndPrintLine(source: String) {
    val tokens = scan(source)
    if (tokens == null) {
        return
    }

    val parser = Parser(tokens)
    val exprs = parser.parse()
    if (parser.hadError) {
        return
    }

    for (e in exprs) {
        println(AstPrinter().print(e))
    }
}