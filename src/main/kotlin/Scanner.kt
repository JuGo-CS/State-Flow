class Scanner(private val source: String) {
    private val tokens = mutableListOf<Token>()
    private var start = 0
    private var current = 0
    private var line = 1
    var hadError = false
        private set

    companion object {
        private val keywords = mapOf(
            "and" to TokenType.AND,
            "else" to TokenType.ELSE,
            "false" to TokenType.FALSE,
            "fun" to TokenType.FUN,
            "for" to TokenType.FOR,
            "if" to TokenType.IF,
            "nil" to TokenType.NIL,
            "or" to TokenType.OR,
            "print" to TokenType.PRINT,
            "return" to TokenType.RETURN,
            "true" to TokenType.TRUE,
            "var" to TokenType.VAR,
            "while" to TokenType.WHILE,
            "state" to TokenType.STATE,
            "goto" to TokenType.GOTO,
            "requires" to TokenType.REQUIRES,
            "input" to TokenType.INPUT,
            "global" to TokenType.GLOBAL,
        )
    }

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }
        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    private fun scanToken() {
        val c = advance()
        when (c) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)

            '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)

            '/' -> {
                if (match('/')) {
                    // line comment: discard until newline
                    while (peek() != '\n' && !isAtEnd()) advance()
                } else {
                    addToken(TokenType.SLASH)
                }
            }
            
            ' ', '\r', '\t' -> { }
            '\n' -> line++

            '"' -> string()

            else -> {
                when {
                    c.isDigit() -> number()
                    isAlpha(c) -> identifier()
                    else -> reportError(line, "Unexpected character: '$c'")
                }
            }
        }
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()
        val text = source.substring(start, current)
        val type = keywords[text] ?: TokenType.IDENTIFIER
        addToken(type)
    }

    private fun number() {
        while (peek().isDigit()) advance()
        if (peek() == '.' && peekNext().isDigit()) {
            advance()
            while (peek().isDigit()) advance()
        }
        val value = source.substring(start, current).toDouble()
        addToken(TokenType.NUMBER, value)
    }

    private fun string() {
        val value = StringBuilder()
 
        while (peek() != '"' && !isAtEnd()) {
            val ch = peek()
            when {
                ch == '\n' -> {
                    line++
                    value.append(advance())
                }
                ch == '\\' -> {
                    advance() // consume the backslash
                    if (isAtEnd()) {
                        reportError(line, "Unterminated string.")
                        return
                    }
                    when (val escaped = advance()) {
                        'n' -> value.append('\n')
                        't' -> value.append('\t')
                        '\\' -> value.append('\\')
                        '"' -> value.append('"')
                        else -> {
                            reportError(line, "Invalid escape sequence: '\\$escaped'")
                            value.append(escaped)
                        }
                    }
                }
                else -> value.append(advance())
            }
        }
 
        if (isAtEnd()) {
            reportError(line, "Unterminated string.")
            return
        }
 
        advance() // consume the closing "
        addToken(TokenType.STRING, value.toString())
    }

    // --- helpers ---

    private fun isAtEnd(): Boolean = current >= source.length

    private fun advance(): Char {
        current++
        return source[current - 1]
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false
        current++
        return true
    }

    private fun peek(): Char {
        if (isAtEnd()) return '\u0000'
        return source[current]
    }

    private fun peekNext(): Char {
        if (current + 1 >= source.length) return '\u0000'
        return source[current + 1]
    }

    private fun isAlpha(c: Char): Boolean =
       c in 'a'..'z' || c in 'A'..'Z' || c == '_'

    private fun isAlphaNumeric(c: Char): Boolean =
        isAlpha(c) || c.isDigit()

    private fun addToken(type: TokenType, literal: Any? = null) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun reportError(line: Int, message: String) {
        System.err.println("[line $line] Error: $message")
        hadError = true
    }
}