class Environment {
    private val values = HashMap<String, Any?>()

    // create or overwrite a variable
    fun define(name: String, value: Any?) {
        TODO("store value under name")
    }

    // read a variable; throw RuntimeError(name, "Undefined variable 'x'.") if missing
    fun get(name: Token): Any? {
        TODO("look up name.lexeme")
    }

    // x = value; (no var yet, so this also creates the variable)
    fun assign(name: Token, value: Any?) {
        TODO("store value under name.lexeme")
    }
}