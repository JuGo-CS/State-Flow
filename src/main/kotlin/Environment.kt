class Environment {
    private val values = HashMap<String, Any?>()

    // create or overwrite a variable
    fun define(name: String, value: Any?) {
        values[name] = value
    }

    // read a variable; throw RuntimeError(name, "Undefined variable 'x'.") if missing
    fun get(name: Token): Any? {
        if (values.containsKey(name.lexeme)) {
            return values[name.lexeme]
        }
        throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
    }

    // x = value; (no var yet, so this also creates the variable)
    fun assign(name: Token, value: Any?) {
        values[name.lexeme] = value
    }
}