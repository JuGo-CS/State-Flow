// Thrown by the evaluator when a valid tree cannot be computed
// (e.g. -"hi", "a" - 1). Carries the token so we can report the line number.
// Caught in exactly one place: Interpreter.interpret().
class RuntimeError(val token: Token, message: String) : RuntimeException(message)