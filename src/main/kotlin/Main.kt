import kotlin.system.exitProcess

fun main(args: Array<String>) {
    when {
        args.size == 2 && args[0] == "--tokenize" -> tokenizeFile(args[1])
        args.size == 1 -> println("Hello, maayong buntag!")
        args.isEmpty() -> runPrompt()
        else -> {
            exitProcess(64)
        }
    }
}

fun tokenizeFile(path: String) {
    val source = try {
        java.io.File(path).readText()
    } catch (e: java.io.IOException) {
        System.err.println("Could not read file: $path")
        exitProcess(66)
    }

    val hadError = runScanner(source)
    if (hadError) exitProcess(65)

    exitProcess(0)
}


fun runPrompt() {
    
    print("> ")
    var line = readLine()
    while (line != null) {
        runScanner(line)
        print("> ")
        line = readLine()
    }
    println()
}

// returns true if scanning failed
fun runScanner(source: String): Boolean {
    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()

    if (!scanner.hadError) {
        for (token in tokens) {
            println(token)
        }
    }

    return scanner.hadError
}