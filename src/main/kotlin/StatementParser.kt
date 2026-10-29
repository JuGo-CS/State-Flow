class StatementParser(tokens: List<Token>) : Parser(tokens) {

    // actual top level: only `global` and `state` declarations are legal here
    fun parseStatements(): List<Statement> {
        val statements = mutableListOf<Statement>()
        while (!isAtEnd()) {
            val stmt = topLevelDeclaration()
            if (stmt != null) {
                statements.add(stmt)
            }
        }
        return statements
    }

    fun parseRepl(): List<Statement> {
        val statements = mutableListOf<Statement>()
        while (!isAtEnd()) {
            val stmt = declaration()
            if (stmt != null) statements.add(stmt)
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

    // STATEMENTS
    private fun statement(): Statement {
        if (match(TokenType.PRINT)) return printStatement()
        if (match(TokenType.LEFT_BRACE)) return Statement.Block(block())
        if (match(TokenType.IF)) return ifStatement()
        if (match(TokenType.WHILE)) return whileStatement()
        if (match(TokenType.FOR)) return forStatement()
        if (match(TokenType.GOTO)) return gotoStatement()
        if (match(TokenType.REQUIRES)) return requiresStatement()
        return expressionStatement()
    }

    // goto <target>( <arguments> )?; target must be an identifier 
    private fun gotoStatement(): Statement {
        val keyword = previous()
        val target = consume(TokenType.IDENTIFIER, "Expect state name after 'goto'.")

        val arguments = mutableListOf<Expr>()
        if (match(TokenType.LEFT_PAREN)) {
            if (!check(TokenType.RIGHT_PAREN)) {
                do {
                    if (arguments.size >= 255) {
                        throw error(peek(), "Can't have more than 255 arguments.")
                    }
                    arguments.add(expression())
                } while (match(TokenType.COMMA))
            }
            consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.")
        }

        consume(TokenType.SEMICOLON, "Expect ';' after goto statement.")
        return Statement.Goto(keyword, target, arguments)
    }

    // requires( <condition> );
    private fun requiresStatement(): Statement {
        val keyword = previous()
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'requires'.")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Expect ')' after requires condition.")
        consume(TokenType.SEMICOLON, "Expect ';' after requires statement.")
        return Statement.Requires(keyword, condition)
    }

    private fun printStatement(): Statement {
        val value = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after value.")
        return Statement.Print(value)
    }

    // calls expressions 
    private fun expressionStatement(): Statement {
        val expr = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after expression.")
        return Statement.Expression(expr)
    }

    private fun block(): List<Statement> {
        val statements = mutableListOf<Statement>()
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            val stmt = declaration()
            if (stmt != null) statements.add(stmt)
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.")
        return statements
    }

    private fun ifStatement(): Statement {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.")

        val thenBranch = statement()
        var elseBranch: Statement? = null
        if (match(TokenType.ELSE)) {
            elseBranch = statement()
        }
        return Statement.If(condition, thenBranch, elseBranch)
    }

    private fun whileStatement(): Statement {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.")
        val body = statement()
        return Statement.While(condition, body)
    }

    // for (<init>; <condition>; <increment>) <body>
    private fun forStatement(): Statement {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'for'.")

        val initializer: Statement? = when {
            match(TokenType.SEMICOLON) -> null
            match(TokenType.VAR) -> varDeclaration()
            else -> expressionStatement()
        }

        var condition: Expr? = null
        if (!check(TokenType.SEMICOLON)) {
            condition = expression()
        }
        consume(TokenType.SEMICOLON, "Expect ';' after loop condition.")

        var increment: Expr? = null
        if (!check(TokenType.RIGHT_PAREN)) {
            increment = expression()
        }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.")

        var body = statement()

        if (increment != null) {
            body = Statement.Block(listOf(body, Statement.Expression(increment)))
        }
        body = Statement.While(condition ?: Expr.Literal(true), body)
        if (initializer != null) {
            body = Statement.Block(listOf(initializer, body))
        }
        return body
    }

}