import kotlin.math.abs
import kotlin.math.floor

class Interpreter {
    // outermost scope, global variables live here
    private val globals = Environment()

    // scope currently in use
    private var environment = globals

    // all states by name, so goto can find its target
    private val states = HashMap<String, Statement.State>()

    // state running now, null in the REPL
    private var currentState: Token? = null

    // thrown by goto to leave the current state and enter another
    private class GotoSignal(val target: Statement.State, val arguments: List<Any?>) : RuntimeException()
    
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
                left is String || right is String -> stringify(left) + stringify(right)
                else -> throw RuntimeError(op, "Operands must be two numbers or at least one string.")
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

    // run a whole program, false means a runtime error
    fun runProgram(statements: List<Statement>): Boolean {
        try {
            // remember every state by name
            for (s in statements) {
                if (s is Statement.State) states[s.name.lexeme] = s
            }

            // define globals first, in file order
            for (s in statements) {
                if (s is Statement.Global) defineGlobal(s)
            }

            // enter START
            val start = states["START"]
            if (start == null) {
                System.err.println("Runtime error: no START state.")
                return false
            }
            runStates(start)
            return true
        } catch (error: RuntimeError) {
            reportRuntimeError(error)
            return false
        } finally {
            // reset even after an error
            environment = globals
            currentState = null
        }
    }

    // enter a state, run its body, follow each goto until END finishes
    private fun runStates(start: Statement.State) {
        var state = start
        var args: List<Any?> = emptyList()

        while (true) {
            currentState = state.name

            // new scope for every entry into a state, parameters filled in
            val stateEnv = Environment(globals)
            for ((i, param) in state.params.withIndex()) {
                stateEnv.define(param.lexeme, args[i])
            }

            try {
                executeBlock(state.body, stateEnv)
            } catch (jump: GotoSignal) {
                // goto: switch to the target state and loop again
                state = jump.target
                args = jump.arguments
                continue
            }

            // only END may finish without a goto
            if (state.name.lexeme == "END") return
            throw RuntimeError(state.name, "State '${state.name.lexeme}' finished without a goto.")
        }
    }

    // REPL: run one line, a bare expression prints its value
    fun runRepl(statements: List<Statement>): Boolean {
        try {
            for (s in statements) {
                if (s is Statement.Expression) {
                    println(stringify(evaluate(s.expression)))
                } else {
                    execute(s)
                }
            }
            return true
        } catch (error: RuntimeError) {
            reportRuntimeError(error)
            return false
        } finally {
            // never stay inside a block after an error
            environment = globals
        }
    }

    private fun defineGlobal(stmt: Statement.Global) {
        // same name twice in the global scope is an error
        if (globals.isDefinedHere(stmt.name.lexeme)) {
            throw RuntimeError(stmt.name, "Variable '${stmt.name.lexeme}' is already declared in this scope.")
        }
        val value = if (stmt.initializer != null) evaluate(stmt.initializer) else null
        globals.define(stmt.name.lexeme, value)
    }

    // run statements in a new scope, then always go back to the old one
    private fun executeBlock(statements: List<Statement>, blockEnv: Environment) {
        val previous = environment
        try {
            environment = blockEnv
            for (s in statements) execute(s)
        } finally {
            // runs even if a goto or an error cut the block short
            environment = previous
        }
    }

    private fun execute(stmt: Statement) {
        when (stmt) {
            // evaluate and throw the value away
            is Statement.Expression -> evaluate(stmt.expression)

            // evaluate and print
            is Statement.Print -> println(stringify(evaluate(stmt.expression)))

            // var creates a binding in the current scope, no initializer means nil
            is Statement.Var -> {
                if (environment.isDefinedHere(stmt.name.lexeme)) {
                    throw RuntimeError(stmt.name, "Variable '${stmt.name.lexeme}' is already declared in this scope.")
                }
                val value = if (stmt.initializer != null) evaluate(stmt.initializer) else null
                environment.define(stmt.name.lexeme, value)
            }

            // a block gets its own scope inside the current one
            is Statement.Block -> executeBlock(stmt.statements, Environment(environment))

            is Statement.If -> {
                if (isTruthy(evaluate(stmt.condition))) {
                    execute(stmt.thenBranch)
                } else if (stmt.elseBranch != null) {
                    execute(stmt.elseBranch)
                }
            }

            is Statement.While -> {
                while (isTruthy(evaluate(stmt.condition))) execute(stmt.body)
            }

            // a failed requires stops the program
            is Statement.Requires -> {
                if (!isTruthy(evaluate(stmt.condition))) {
                    val where = currentState?.let { " in state '${it.lexeme}'" } ?: ""
                    throw RuntimeError(stmt.keyword, "Requirement not met$where.")
                }
            }

            // goto: find the target, check the arguments, then jump
            is Statement.Goto -> {
                if (currentState == null) {
                    throw RuntimeError(stmt.keyword, "'goto' can only be used inside a state.")
                }
                val target = states[stmt.target.lexeme]
                    ?: throw RuntimeError(stmt.target, "Undefined state '${stmt.target.lexeme}'.")
                val args = stmt.arguments.map { evaluate(it) }
                if (args.size != target.params.size) {
                    throw RuntimeError(
                        stmt.target,
                        "State '${target.name.lexeme}' expects ${target.params.size} argument(s) but got ${args.size}."
                    )
                }
                throw GotoSignal(target, args)
            }

            // globals are defined before START and states are entered through goto,
            // so nothing to do when they show up inside a body
            is Statement.Global -> {}
            is Statement.State -> {}
        }
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
            val isWhole = !value.isInfinite() && !value.isNaN() && value == floor(value)
            if (isWhole && abs(value) < 1e15) return value.toLong().toString()
            return value.toString()
        }
        return value.toString()
    }

    private fun reportRuntimeError(error: RuntimeError) {
        System.err.println("[line ${error.token.line}] Runtime error: ${error.message}")
    }
}