// Unwinding nested blocks and loops during an early return.
internal class ReturnSignal(val value: Any?) : RuntimeException()
