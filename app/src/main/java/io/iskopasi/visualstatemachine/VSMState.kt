package io.iskopasi.visualstatemachine


class VSMState(
    val id: Int = -1,
    val name: String = ""
) {
    companion object {
        val NONE = VSMState(-1, "NONE")
    }

    var nextState = NONE

    init {
        "---> Created state $id $name".e
    }

    fun addNext(name: String): VSMState {
        nextState = VSMState(id + 1, name)

        return nextState
    }

    fun getNext(): VSMState = nextState
}

class StateMachineController {
    var first = VSMState.NONE
    var state = VSMState.NONE

    fun create(name: String): VSMState {
        state = VSMState(0, name)
        first = state

        return state
    }

    fun addNext(name: String): VSMState {
        state = state.addNext(name)

        return state
    }

    fun advance(): VSMState {
        val nextState = state.getNext()
        "--> Advancing from state ${state.name} to ${nextState.name}".e
        state = nextState

        return state
    }

    fun hasNext() = state.getNext() != VSMState.NONE

    fun reset() {
        state = first
    }
}