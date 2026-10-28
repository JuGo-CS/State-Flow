class Interpreter {
    private var environment = Environment()
    
    // entry point
    fun interpret(expr: Expr): Boolean {
        return try {
            // compute the value and print
            val value = evaluate(expr)
            println(stringify(value))
            true
        } catch (error: RuntimeError) {
            // report the runtime error and signal failure
            reportRuntimeError(error)
            false
        }
    }

    // check the node
    fun evaluate(expr: Expr): Any? = when (expr) {
        is Expr.Literal -> evalLiteral(expr)
        is Expr.Grouping -> evalGrouping(expr)
        is Expr.Unary -> evalUnary(expr)
        is Expr.Binary -> evalBinary(expr)
        is Expr.Variable -> evalVariable(expr)
        is Expr.Assign -> evalAssign(expr)
    }

    // return immediately if literal
    private fun evalLiteral(expr: Expr.Literal): Any? = expr.value

    // evaluate the inside of parenthesis
    private fun evalGrouping(expr: Expr.Grouping): Any? = evaluate(expr.expression)

    private fun evalUnary(expr: Expr.Unary): Any? {

        // evaluate the operand
        val operand = evaluate(expr.right)

        return when (expr.operator.type) {
    
            TokenType.MINUS -> {
                checkNumberOperand(expr.operator, operand)
                -(operand as Double)
            }
            TokenType.BANG -> !isTruthy(operand)

            // safety throw
            else -> throw RuntimeError(expr.operator, "Unknown unary operator.")
        }
    }

    private fun evalBinary(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)
        val op = expr.operator

        return when (op.type) {
            // add two numbers or join strings
            TokenType.PLUS -> when {
                left is Double && right is Double -> left + right
                left is String && right is String -> left + right
                else -> throw RuntimeError(op, "Operands must be two numbers or two strings.")
            }

            // arithmetic, both must be numbers
            TokenType.MINUS -> {
                checkNumberOperands(op, left, right)
                (left as Double) - (right as Double)
            }
            TokenType.STAR -> {
                checkNumberOperands(op, left, right)
                (left as Double) * (right as Double)
            }
            TokenType.SLASH -> {
                checkNumberOperands(op, left, right)
                if ((right as Double) == 0.0) throw RuntimeError(op, "Division by zero.")
                (left as Double) / right
            }

            // comparisons, both types must be numbers; return boolean
            TokenType.GREATER -> {
                checkNumberOperands(op, left, right)
                (left as Double) > (right as Double)
            }
            TokenType.GREATER_EQUAL -> {
                checkNumberOperands(op, left, right)
                (left as Double) >= (right as Double)
            }
            TokenType.LESS -> {
                checkNumberOperands(op, left, right)
                (left as Double) < (right as Double)
            }
            TokenType.LESS_EQUAL -> {
                checkNumberOperands(op, left, right)
                (left as Double) <= (right as Double)
            }

            // equality, any two values
            TokenType.EQUAL_EQUAL -> isEqual(left, right)
            TokenType.BANG_EQUAL -> !isEqual(left, right)
            else -> throw RuntimeError(op, "Unknown binary operator.")
        }
    }

    // look up value of variable by the name
    private fun evalVariable(expr: Expr.Variable): Any? = environment.get(expr.name)


    private fun evalAssign(expr: Expr.Assign): Any? {

        // compute right side and store
        val value = evaluate(expr.value)
        environment.assign(expr.name, value)

        return value
    }

    // helpers

    // falsey: nil, false, 0, ""   (everything else is truthy)
    private fun isTruthy(value: Any?): Boolean {
        if (value == null) return false
        if (value is Boolean) return value
        if (value is Double) return value != 0.0
        if (value is String) return value.isNotEmpty()

        return true
    }

    // == and != work on any pair of types; use value equality, not reference
    private fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) return true
        if (a == null || b == null) return false
        if (a is Double && b is Double) return a == b
        
        return a == b
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw RuntimeError(operator, "Operand must be a number.")
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operands must be numbers.")
    }

    // numbers (trim ".0"?), nil, strings without quotes, booleans
    private fun stringify(value: Any?): String {
        if (value == null) return "nil"
        if (value is Double) {
            val isWhole = !value.isInfinite() && !value.isNaN() && value == Math.floor(value)
            if (isWhole && Math.abs(value) < 1e15) return value.toLong().toString()
            return value.toString()
        }
        return value.toString()
    }

    private fun reportRuntimeError(error: RuntimeError) {
        System.err.println("[line ${error.token.line}] Runtime error: ${error.message}")
    }
}