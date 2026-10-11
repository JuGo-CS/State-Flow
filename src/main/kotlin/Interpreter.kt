class Interpreter {
    private val environment = Environment()
    
    fun interpret(expr: Expr): Boolean {
        return try {
            val value = evaluate(expr)
            println(stringify(value))
            true
        } catch (error: RuntimeError) {
            reportRuntimeError(error)
            false
        }
    }

    fun evaluate(expr: Expr): Any? = when (expr) {
        is Expr.Literal -> evalLiteral(expr)
        is Expr.Grouping -> evalGrouping(expr)
        is Expr.Unary -> evalUnary(expr)
        is Expr.Binary -> evalBinary(expr)
        is Expr.Variable -> evalVariable(expr)
        is Expr.Assign -> evalAssign(expr)
    }

    private fun evalLiteral(expr: Expr.Literal): Any? {
        TODO("return expr.value")
    }

    private fun evalGrouping(expr: Expr.Grouping): Any? {
        TODO("evaluate the inner expression")
    }

    private fun evalUnary(expr: Expr.Unary): Any? {
        TODO("evaluate operand first; '-' needs a number, '!' uses isTruthy")
    }

    private fun evalBinary(expr: Expr.Binary): Any? {
        TODO("evaluate left, then right, then apply operator (+ - * / < <= > >= == !=)")
    }

    private fun evalVariable(expr: Expr.Variable): Any? {
        TODO("environment.get(expr.name)")
    }

    private fun evalAssign(expr: Expr.Assign): Any? {
        TODO("evaluate value, environment.assign, return the value")
    }

    // helpers

    // the language's truthiness rule (decide, then document in README)
    private fun isTruthy(value: Any?): Boolean {
        TODO("e.g. false and nil are falsey, everything else truthy")
    }

    // == and != work on any pair of types; use value equality, not reference
    private fun isEqual(a: Any?, b: Any?): Boolean {
        TODO("nil == nil, otherwise a == b")
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        TODO("throw RuntimeError(operator, \"Operand must be a number.\") if not Double")
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        TODO("throw RuntimeError(operator, \"Operands must be numbers.\") unless both Double")
    }

    // how a value prints (decide, then document in README):
    // numbers (trim ".0"?), nil, strings without quotes, booleans
    private fun stringify(value: Any?): String {
        TODO("format a runtime value for output")
    }

    private fun reportRuntimeError(error: RuntimeError) {
        System.err.println("[line ${error.token.line}] Runtime error: ${error.message}")
    }
}