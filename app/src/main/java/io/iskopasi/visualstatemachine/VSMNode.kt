package io.iskopasi.visualstatemachine

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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

data class VSMNode(
    val id: Int = -1,
    val name: MutableState<String> = mutableStateOf(""),
    var x: MutableState<Float> = mutableFloatStateOf(0f),
    var y: MutableState<Float> = mutableFloatStateOf(0f)
) {
    companion object {
        val NONE = VSMNode(-1, mutableStateOf("NONE"))
    }

    val data = mutableMapOf<String, Any>()
    val parents = mutableListOf<VSMNode>()
    val children = mutableListOf<VSMNode>()

    fun addChild(newId: Int, name: String, x: Float, y: Float): VSMNode {
        val node = VSMNode(
            newId, mutableStateOf(name),
            mutableFloatStateOf(x),
            mutableFloatStateOf(y),
        ).apply {
            parents.add(this@VSMNode)
        }

        "--> Adding new node ${node.id} to $id".e

        children.add(node)

        return node
    }
}

class StateMachineController {
    var latestId = 0

    //    var lastNode = VSMNode.NONE
    val nodes = mutableStateListOf<VSMNode>()
    val nodeMap = mutableMapOf<Int, VSMNode>()

    fun create(name: String, x: Float, y: Float): VSMNode {
        val newNode = VSMNode(
            latestId,
            mutableStateOf(latestId.toString()),
            mutableFloatStateOf(x),
            mutableFloatStateOf(y)
        )

//        lastNode = if (lastNode == VSMNode.NONE) {
//            VSMNode(
//                id,
//                mutableStateOf(name),
//                mutableFloatStateOf(x),
//                mutableFloatStateOf(y)
//            )
//        } else {
//            lastNode.addChild(id, name, x, y)
//        }

        "--> Creating newNode $latestId".e

        saveNode(newNode)

        return newNode
    }

//    fun hasLeafs() = lastNode.children.isNotEmpty()
//
//    fun reset() {
//        lastNode = nodes[0]
//    }

    fun getById(selectedId: Int): VSMNode {
        "--> Attempting to get node $selectedId".e

        return nodeMap[selectedId]!!
    }

    fun addChild(nodeId: Int, text: String, x: Float, y: Float) {
        "--> Attempting to add child ${latestId} to parent $nodeId".e

        val node = getById(nodeId).addChild(latestId, latestId.toString(), x, y)
//        lastNode = node

        saveNode(node)
    }

    private fun saveNode(node: VSMNode) {
        nodes.add(node)
        nodeMap[node.id] = node

        latestId++
    }

    fun remove(node: VSMNode) {
        "--> Deleting node: ${node.id} ${node.name.value}".e

        // Remove this child from parents
        node.parents.forEach {
            it.children.remove(node)
        }

        // Remove this parent from children
        node.children.forEach {
            it.parents.remove(node)
        }

        // Remove node
        nodes.remove(node)
        nodeMap.remove(node.id)
    }
}