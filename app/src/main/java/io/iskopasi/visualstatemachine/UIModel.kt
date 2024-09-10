package io.iskopasi.visualstatemachine

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel

class UIModel(context: Application) : AndroidViewModel(context) {
    private val vibrator by lazy {
        ContextCompat.getSystemService(
            context.applicationContext,
            Vibrator::class.java
        )
    }
    private val ctrl = StateMachineController()
    val cellSize = 30.dp
    var selectedId = mutableIntStateOf(-1)
    val nodes
        get() = ctrl.nodes
    val menuData = mutableStateOf<MenuData?>(null)
    val dialogData = mutableStateOf<DialogData?>(null)

    fun selectNode(node: VSMNode) {
        if (selectedId.intValue != node.id) {
            "Selected node: ${node.id}".e
//            if (node.id != 0 && node.id != nodes.size - 1) {
//                selectedId.intValue = node.id
//            }
            selectedId.intValue = node.id
        } else {
            "Deselected node: ${node.id}".e
            selectedId.intValue = -1
        }
    }

    fun getNodeType(node: VSMNode): VSMNodeType {
        return when {
//            node.id == 0 -> VSMNodeType.START.apply {
//                width = cellSize * 2f
//                height = cellSize / 2f
//            }

//            node.id == nodes.size - 1 -> VSMNodeType.END.apply {
//                width = cellSize * 2f
//                height = cellSize / 2f
//            }

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

    fun createNode(text: String = "", coord: Offset) {
        if (selectedId.intValue != -1) {
            // If selected - add child
            ctrl.addChild(selectedId.intValue, text, coord.x, coord.y)
        } else {
            // Otherwise create new node
            ctrl.create(text, coord.x, coord.y)
        }
    }

    fun removeNode(node: VSMNode) {
        ctrl.remove(node)
    }

    fun showMenu(node: VSMNode) {
        // Vibrate on long press
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(50L, 255))
        } else {
            vibrator?.vibrate(50L)
        }

        menuData.value = MenuData(node, node.x.value, node.y.value)
    }

    fun hideMenu() {
        "Hiding menu".e
        menuData.value = null
    }

    fun showDeleteDialog(node: VSMNode) {
        dialogData.value = DialogData(node)
    }

    fun hideDialog() {
        dialogData.value = null
    }
}

data class MenuData(
    val node: VSMNode,
    val x: Float,
    val y: Float,
) {
    fun save(newName: String) {
        node.name.value = newName
    }
}

data class DialogData(
    val node: VSMNode,
)