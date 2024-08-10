package io.iskopasi.visualstatemachine

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.geometry.Offset


class VSMState(
    val id: Int = -1,
    val name: String = "",
    var x: MutableState<Float> = mutableFloatStateOf(0f),
    var y: MutableState<Float> = mutableFloatStateOf(0f)
) {
    companion object {
        val NONE = VSMState(-1, "NONE")
    }

    val data = mutableMapOf<String, Any>()
    var nextState = NONE

    init {
        "---> Created state $id $name".e
    }

    fun addNext(name: String, x: Float, y: Float): VSMState {
        nextState = VSMState(id + 1, name, mutableFloatStateOf(x), mutableFloatStateOf(y))

        return nextState
    }

    fun getNext(): VSMState = nextState
}

class StateMachineController {
    var first = VSMState.NONE
    var state = VSMState.NONE
    val nodes = mutableListOf<VSMState>()

    fun create(name: String, x: Float, y: Float): VSMState {
        state = VSMState(0, name, mutableFloatStateOf(x), mutableFloatStateOf(y))
        first = state

        return state
    }

    fun addNext(name: String, coords: Offset): VSMState {
        state = if (state == VSMState.NONE) create(name, coords.x, coords.y)
        else state.addNext(name, coords.x, coords.y)

        nodes.add(state)

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