sealed class Statement {
    data class Expression(val expression: Expr) : Statement()
    data class Print(val expression: Expr) : Statement()
    data class Var(val name: Token, val initializer: Expr?) : Statement()
    data class Block(val statements: List<Statement>) : Statement()
    data class If(val condition: Expr, val thenBranch: Statement, val elseBranch: Statement?) : Statement()
    data class While(val condition: Expr, val body: Statement) : Statement()

    // global <name> = <expr>
    data class Global(val name: Token, val initializer: Expr?) : Statement()

    // state <name>(<params>) { <body> } or empty params
    data class State(val name: Token, val params: List<Token>, val body: List<Statement>) : Statement()

    // goto <target>(<arguments>);
    data class Goto(val keyword: Token, val target: Token, val arguments: List<Expr>) : Statement()

    // requires(<condition>);
    data class Requires(val condition: Expr) : Statement()
}