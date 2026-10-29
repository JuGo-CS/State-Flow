class Environment(private val enclosing: Environment? = null) {
    private val values = HashMap<String, Any?>()

    // create or overwrite a binding in THIS scope only (never looks outward)
    fun define(name: String, value: Any?) {
        values[name] = value
    }

    // true if this scope itself already has the name (does not look outward)
    fun isDefinedHere(name: String): Boolean = values.containsKey(name)

    // read a variable, searching outward through enclosing scopes
    fun get(name: Token): Any? {
        // found here
        if (values.containsKey(name.lexeme)) return values[name.lexeme]
        // not here: ask the surrounding scope
        if (enclosing != null) return enclosing.get(name)
        // nowhere
        throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
    }

    // update an existing variable, searching outward. Never creates one.
    fun assign(name: Token, value: Any?) {
        // found here: update it
        if (values.containsKey(name.lexeme)) {
            values[name.lexeme] = value
            return
        }
        // not here: update it in the surrounding scope
        if (enclosing != null) {
            enclosing.assign(name, value)
            return
        }
        // nowhere: assignment does not create a variable
        throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
    }
}