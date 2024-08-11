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
    SELECTOR(Color.Green, Color.Black, "", 0.dp, 0.dp),
    TERMINAL(Color.Red, Color.White, "", 0.dp, 0.dp),
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
    val nodes = mutableListOf<VSMNode>()

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
}

class StateMachineController {
    var currentNode = VSMNode.NONE
    val nodes = mutableStateListOf<VSMNode>()
    val nodeMap = mutableMapOf<Int, VSMNode>()

    fun create(name: String, x: Float, y: Float): VSMNode {
        val id = nodes.size

        currentNode = if (currentNode == VSMNode.NONE) {
            VSMNode(
                id,
                name,
                mutableFloatStateOf(x),
                mutableFloatStateOf(y)
            )
        } else {
            currentNode.addChild(id, name, x, y)
        }

        "--> Creating node $id".e

        saveNode(currentNode)

        return currentNode
    }

    fun hasLeafs() = currentNode.nodes.isNotEmpty()

    fun reset() {
        currentNode = nodes[0]
    }

    fun getById(selectedId: Int) = nodeMap[selectedId]!!

    fun addChild(nodeId: Int, text: String, x: Float, y: Float) {
        val node = getById(nodeId).addChild(nodes.size, text, x, y)
        currentNode = node

        saveNode(node)
    }

    private fun saveNode(node: VSMNode) {
        nodes.add(node)
        nodeMap[node.id] = node
    }
}