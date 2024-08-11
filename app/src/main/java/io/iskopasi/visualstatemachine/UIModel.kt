package io.iskopasi.visualstatemachine

import android.app.Application
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel

class UIModel(context: Application) : AndroidViewModel(context) {
    val cellSize = 30.dp
    val ctrl = StateMachineController()
    var selectedId = mutableIntStateOf(-1)
    val nodes
        get() = ctrl.nodes

    fun selectNode(node: VSMNode) {
        if (selectedId.intValue != node.id) {
            if (node.id != 0 && node.id != nodes.size - 1) {
                selectedId.intValue = node.id
            }
        } else
            selectedId.intValue = -1
    }

    fun getNodeType(node: VSMNode): VSMNodeType {
        return when {
            node.id == 0 -> VSMNodeType.START.apply {
                width = cellSize * 2f
                height = cellSize / 2f
            }

            node.id == nodes.size - 1 -> VSMNodeType.END.apply {
                width = cellSize * 2f
                height = cellSize / 2f
            }

            node.nodes.size >= 2 -> VSMNodeType.SELECTOR.apply {
                label = node.name
                width = cellSize
                height = cellSize
            }

            node.nodes.size == 1 -> VSMNodeType.REGULAR.apply {
                label = node.name
                width = cellSize
                height = cellSize
            }

            node.nodes.size == 0 -> VSMNodeType.TERMINAL.apply {
                label = node.name
                width = cellSize
                height = cellSize
            }

            else -> VSMNodeType.REGULAR.apply {
                label = node.name
                width = cellSize
                height = cellSize
            }
        }
    }

    fun createNode(text: String, coord: Offset) {
        if (selectedId.intValue != -1) {
            // If selected - add child
            ctrl.addChild(selectedId.intValue, text, coord.x, coord.y)
        } else {
            // Otherwise create new node
            ctrl.create(text, coord.x, coord.y)
        }
    }
}