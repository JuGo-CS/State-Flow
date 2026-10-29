class Checker(private val statements: List<Statement>) {
    var hadError = false
        private set

    private val states = HashMap<String, Statement.State>()

    fun check() {
        // collect the states by name, rejecting duplicates
        for (s in statements) {
            if (s !is Statement.State) continue
            if (states.containsKey(s.name.lexeme)) {
                report(s.name, "State '${s.name.lexeme}' is already defined.")
            } else {
                states[s.name.lexeme] = s
            }
        }

        // START and END must exist
        val start = states["START"]
        if (start == null) reportGeneral("Program must define a state named START.")
        if (!states.containsKey("END")) reportGeneral("Program must define a state named END.")

        // nothing can pass parameters into START
        if (start != null && start.params.isNotEmpty()) {
            report(start.name, "START cannot have parameters.")
        }

        // check each state's gotos, in file order
        for (s in statements) {
            if (s is Statement.State) checkState(s)
        }
    }

    private fun checkState(state: Statement.State) {
        val isEnd = state.name.lexeme == "END"
        val gotos = mutableListOf<Statement.Goto>()
        collectGotos(state.body, gotos)

        // END must not leave; every other state must have a goto
        if (isEnd && gotos.isNotEmpty()) report(gotos[0].keyword, "END cannot use 'goto'.")
        if (!isEnd && gotos.isEmpty()) report(state.name, "State '${state.name.lexeme}' must have a 'goto'.")

        // each goto must name an existing state and pass the right number of arguments
        for (g in gotos) {
            val target = states[g.target.lexeme]
            if (target == null) {
                report(g.target, "Undefined state '${g.target.lexeme}'.")
                continue
            }
            if (target.params.size != g.arguments.size) {
                report(g.target, "State '${target.name.lexeme}' expects ${target.params.size} argument(s) but got ${g.arguments.size}.")
            }
        }
    }

    // finds gotos anywhere inside a state, including nested blocks, ifs and loops
    private fun collectGotos(body: List<Statement>, out: MutableList<Statement.Goto>) {
        for (s in body) {
            when (s) {
                is Statement.Goto -> out.add(s)
                is Statement.Block -> collectGotos(s.statements, out)
                is Statement.If -> {
                    collectGotos(listOf(s.thenBranch), out)
                    if (s.elseBranch != null) collectGotos(listOf(s.elseBranch), out)
                }
                is Statement.While -> collectGotos(listOf(s.body), out)
                else -> {}
            }
        }
    }

    private fun report(token: Token, message: String) {
        System.err.println("[line ${token.line}] Error at '${token.lexeme}': $message")
        hadError = true
    }

    // for errors about the whole program, which have no single line
    private fun reportGeneral(message: String) {
        System.err.println("Error: $message")
        hadError = true
    }
}