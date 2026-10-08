class Parser(private val tokens: List<Token>) {
    private var current = 0

    var hadError = false
        private set

    private class ParseError : RuntimeException()

    // Top level: only `global` and `state` declarations are legal here
    fun parse(): List<Statement> {
        val statements = mutableListOf<Statement>()
        while (!isAtEnd()) {
            val stmt = topLevelDeclaration()
            if (stmt != null) {
                statements.add(stmt)
            }
        }
        return statements
    }

    private fun topLevelDeclaration(): Statement? {
        return try {
            if (match(TokenType.GLOBAL)) return globalDeclaration()
            if (match(TokenType.STATE)) return stateDeclaration()
            throw error(peek(), "Expect 'global' or 'state' declaration at top level.")
        } catch (error: ParseError) {
            synchronize()
            null
        }
    }

    // global <name> = <expr>;
    private fun globalDeclaration(): Statement {
        val name = consume(TokenType.IDENTIFIER, "Expect variable name after 'global'.")
        var initializer: Expr? = null
        if (match(TokenType.EQUAL)) {
            initializer = expression()
        }
        consume(TokenType.SEMICOLON, "Expect ';' after global variable declaration.")
        return Statement.Global(name, initializer)
    }

    // declarations inside state body
    private fun declaration(): Statement? {
        return try {
            if (match(TokenType.VAR)) return varDeclaration()
            statement()
        } catch (error: ParseError) {
            synchronize()
            null
        }
    }

    // state <name>( <params> )? { <body> } or state <name> { <body> } if no parameters
    private fun stateDeclaration(): Statement {
        val name = consume(TokenType.IDENTIFIER, "Expect state name.")

        val params = mutableListOf<Token>()
        if (match(TokenType.LEFT_PAREN)) {
            if (!check(TokenType.RIGHT_PAREN)) {
                do {
                    if (params.size >= 255) {
                        throw error(peek(), "Can't have more than 255 parameters.")
                    }
                    params.add(consume(TokenType.IDENTIFIER, "Expect parameter name."))
                } while (match(TokenType.COMMA))
            }
            consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.")
        }

        consume(TokenType.LEFT_BRACE, "Expect '{' before state body.")
        val body = block()
        return Statement.State(name, params, body)
    }

    private fun varDeclaration(): Statement {
        val name = consume(TokenType.IDENTIFIER, "Expect variable name.")
        var initializer: Expr? = null
        if (match(TokenType.EQUAL)) {
            initializer = expression()
        }
        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.")
        return Statement.Var(name, initializer)
    }


    // EXPRESSIONS

    private fun expression(): Expr = assignment()

    private fun assignment(): Expr {
        val expr = equality()

        if (match(TokenType.EQUAL)) {
            val equals = previous()
            val value = assignment() // right-associative: a = b = c
            if (expr is Expr.Variable) {
                return Expr.Assign(expr.name, value)
            }
            error(equals, "Invalid assignment target.")
        }

        return expr
    }

    private fun equality(): Expr {
        var expr = comparison()
        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            val operator = previous()
            val right = comparison()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun comparison(): Expr {
        var expr = term()
        while (match(
                TokenType.GREATER, TokenType.GREATER_EQUAL,
                TokenType.LESS, TokenType.LESS_EQUAL
            )
        ) {
            val operator = previous()
            val right = term()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun term(): Expr {
        var expr = factor()
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val operator = previous()
            val right = factor()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun factor(): Expr {
        var expr = unary()
        while (match(TokenType.SLASH, TokenType.STAR)) {
            val operator = previous()
            val right = unary()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expr {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            val operator = previous()
            val right = unary()
            return Expr.Unary(operator, right)
        }
        return primary()
    }

    private fun primary(): Expr {
        if (match(TokenType.FALSE)) return Expr.Literal(false)
        if (match(TokenType.TRUE)) return Expr.Literal(true)
        if (match(TokenType.NIL)) return Expr.Literal(null)

        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return Expr.Literal(previous().literal)
        }

        if (match(TokenType.IDENTIFIER)) {
            return Expr.Variable(previous())
        }

        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return Expr.Grouping(expr)
        }

        throw error(peek(), "Expect expression.")
    }

    // ---- helpers ----

    private fun peek(): Token = tokens[current]

    private fun previous(): Token = tokens[current - 1]

    private fun isAtEnd(): Boolean = peek().type == TokenType.EOF

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun check(type: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().type == type
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw error(peek(), message)
    }

    private fun error(token: Token, message: String): ParseError {
        hadError = true
        if (token.type == TokenType.EOF) {
            System.err.println("[line ${token.line}] Error at end: $message")
        } else {
            System.err.println("[line ${token.line}] Error at '${token.lexeme}': $message")
        }
        return ParseError()
    }

    private fun synchronize() {
        advance()
        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return
            when (peek().type) {
                TokenType.STATE, TokenType.GLOBAL, TokenType.FUN, TokenType.VAR,
                TokenType.FOR, TokenType.IF, TokenType.WHILE, TokenType.PRINT,
                TokenType.RETURN, TokenType.GOTO, TokenType.REQUIRES -> return
                else -> {}
            }
            advance()
        }
    }
}