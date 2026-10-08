class AstPrinter {
    fun print(expr: Expr): String = when (expr) {
        is Expr.Literal -> stringify(expr.value)
        is Expr.Grouping -> parenthesize("group", expr.expression)
        is Expr.Binary -> parenthesize(expr.operator.lexeme, expr.left, expr.right)
        is Expr.Unary -> parenthesize(expr.operator.lexeme, expr.right)
        is Expr.Variable -> expr.name.lexeme
        is Expr.Assign -> parenthesize("= " + expr.name.lexeme, expr.value)
    }
 
    private fun parenthesize(name: String, vararg exprs: Expr): String {
        val builder = StringBuilder()
        builder.append('(').append(name)
        for (e in exprs) {
            builder.append(' ')
            builder.append(print(e))
        }
        builder.append(')')
        return builder.toString()
    }
 
    private fun stringify(value: Any?): String = when (value) {
        null -> "nil"
        is Double -> value.toString()
        is Boolean -> value.toString()
        is String -> value
        else -> value.toString()
    }
}