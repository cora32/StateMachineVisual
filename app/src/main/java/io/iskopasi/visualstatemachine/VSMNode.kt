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
    SINGLE(Color.Black, Color.White, "", 0.dp, 0.dp),
    ENTRY(Color.White, Color.Black, "", 0.dp, 0.dp),
    REGULAR(Color.DarkGray, Color.White, "", 0.dp, 0.dp),
    SELECTOR(Color.Green, Color.Black, "", 0.dp, 0.dp),
    TERMINAL(Color.Red, Color.White, "", 0.dp, 0.dp),
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
    val parents = mutableStateListOf<VSMNode>()
    val children = mutableStateListOf<VSMNode>()

    fun addParent(parent: VSMNode): Boolean {
        return if (!parents.contains(parent)) {
            "--> Adding new parent ${parent.id} to $id".e
            parents.add(parent)
        } else {
            "--> Parent ${parent.id} is already added to $id".e
            false
        }
    }

    fun addChild(child: VSMNode): Boolean {
        return if (!children.contains(child)) {
            "--> Adding new child ${child.id} to $id".e
            children.add(child)
        } else {
            "--> Child ${child.id} is already added to $id".e
            false
        }
    }

//    fun addChild(newId: Int, name: String, x: Float, y: Float): VSMNode {
//        val node = VSMNode(
//            newId, mutableStateOf(name),
//            mutableFloatStateOf(x),
//            mutableFloatStateOf(y),
//        ).apply {
//            parents.add(this@VSMNode)
//        }
//
//        "--> Adding new node ${node.id} to $id".e
//
//        children.add(node)
//
//        return node
//    }
}

class StateMachineController {
    var latestId = 0

    //    var lastNode = VSMNode.NONE
    val nodes = mutableStateListOf<VSMNode>()
    val nodeMap = mutableMapOf<Int, VSMNode>()

    fun createNode(text: String, x: Float, y: Float): VSMNode {
        val newNode = VSMNode(
            latestId,
            mutableStateOf(text.ifEmpty { latestId.toString() }),
            mutableFloatStateOf(x),
            mutableFloatStateOf(y)
        )

        "--> Creating newNode $latestId".e

        saveNode(newNode)

        return newNode
    }

    fun createNewChild(parentId: Int, text: String, x: Float, y: Float) {
        "--> Attempting to add child ${latestId} to parent $parentId".e

        val newNode = createNode(
            text.ifEmpty { latestId.toString() },
            x, y
        )

        addChildToParent(newNode, parentId)
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

    fun addChildToParent(child: VSMNode, parentId: Int): Boolean =
        nodeMap[parentId]?.let { parent ->
            "Adding child (${child.id}) to parent ($parentId)".e

            // Adding child to parent
            val success = parent.addChild(child)

            // Adding parent to child
            child.addParent(parent)

            success
        } ?: false

    fun getType(node: VSMNode) = when {
        node.children.size == 0 && node.parents.size == 0 -> VSMNodeType.SINGLE.apply {
            label = node.name.value
            width = cellSize
            height = cellSize
        }

        node.children.size > 0 && node.parents.size == 0 -> VSMNodeType.ENTRY.apply {
            label = node.name.value
            width = cellSize
            height = cellSize
        }

        node.children.size >= 2 -> VSMNodeType.SELECTOR.apply {
            label = node.name.value
            width = cellSize
            height = cellSize
        }

        node.children.size == 1 -> VSMNodeType.REGULAR.apply {
            label = node.name.value
            width = cellSize
            height = cellSize
        }

        node.children.size == 0 -> VSMNodeType.TERMINAL.apply {
            label = node.name.value
            width = cellSize
            height = cellSize
        }

        else -> VSMNodeType.REGULAR.apply {
            label = node.name.value
            width = cellSize
            height = cellSize
        }
    }
}