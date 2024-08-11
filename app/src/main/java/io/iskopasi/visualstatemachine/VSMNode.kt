package io.iskopasi.visualstatemachine

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class VSMNodeType(
    val color: Color,
    val textColor: Color,
    var label: String,
    var width: Dp,
    var height: Dp,
) {
    START(Color.White, Color.Black, "START", 0.dp, 0.dp),
    REGULAR(Color.DarkGray, Color.White, "", 0.dp, 0.dp),
    END(Color.Black, Color.White, "END", 0.dp, 0.dp),
}

class VSMNode(
    val id: Int = -1,
    val name: String = "",
    var x: MutableState<Float> = mutableFloatStateOf(0f),
    var y: MutableState<Float> = mutableFloatStateOf(0f)
) {
    companion object {
        val NONE = VSMNode(-1, "NONE")
    }

    val data = mutableMapOf<String, Any>()

    //    var nextState = NONE
    val nodes = mutableListOf<VSMNode>()

    init {
        "---> Created state $id $name".e
    }

    fun addChild(newId: Int, name: String, x: Float, y: Float): VSMNode {
        val node = VSMNode(
            newId, name,
            mutableFloatStateOf(x),
            mutableFloatStateOf(y),
        )

        "--> Adding new node ${node.id} to $id".e

        nodes.add(node)

        return node
    }

//    fun getNext(): VSMNode = nextState
}

class StateMachineController {
    var node = VSMNode.NONE
    val nodes = mutableStateListOf<VSMNode>()
    val nodeMap = mutableMapOf<Int, VSMNode>()

    fun create(name: String, x: Float, y: Float): VSMNode {
        val id = nodes.size

        node = if (node == VSMNode.NONE)
            VSMNode(
                id,
                name,
                mutableFloatStateOf(x),
                mutableFloatStateOf(y)
            )
        else
            node.addChild(id, name, x, y)

        "--> Creating node $id".e

        saveNode(node)

        return node
    }

    fun hasLeafs() = node.nodes.isNotEmpty()

    fun reset() {
        node = nodes[0]
    }

    fun getById(selectedId: Int) = nodeMap[selectedId]!!

    fun addChild(nodeId: Int, text: String, x: Float, y: Float) {
        "--> Adding node to $nodeId".e
        val node = getById(nodeId).addChild(nodes.size, text, x, y)

        saveNode(node)
    }

    private fun saveNode(node: VSMNode) {
        nodes.add(node)
        nodeMap[node.id] = node
    }
}