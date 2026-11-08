// Lab 5 call protocol shared by user-defined and native functions.
interface Callable {
    fun arity(): Int
    fun call(interpreter: Interpreter, arguments: List<Any?>): Any?
}

class UserFunction(
    private val declaration: Statement.Function,
    private val closure: Environment,
) : Callable {
    override fun arity(): Int = declaration.params.size

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? =
        TODO("Create a call environment, bind parameters, execute the body, and return its result.")
}

class NativeFunction(private val expectedArity: Int) : Callable {
    override fun arity(): Int = expectedArity

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? =
        TODO("Implement the native operation.")
}
