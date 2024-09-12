package io.iskopasi.visualstatemachine

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel

enum class Modes {
    Remove,
    Connect,
    Select,
}

class UIModel(context: Application) : AndroidViewModel(context) {
    private val vibrator by lazy {
        ContextCompat.getSystemService(
            context.applicationContext,
            Vibrator::class.java
        )
    }
    private val ctrl = StateMachineController()
    val globalOffset = mutableStateOf(Offset.Zero)
    var selectedId = mutableIntStateOf(-1)
    val nodes
        get() = ctrl.nodes
    val menuData = mutableStateOf<MenuData?>(null)
    val dialogData = mutableStateOf<DialogData?>(null)
    val mode = mutableStateOf<Modes>(Modes.Select)

    fun isSelected(node: VSMNode) = selectedId.intValue == node.id

    fun onNodeClick(node: VSMNode) {
        "onNodeClick: ${mode.value}; ${node.id}".e

        when (mode.value) {
            Modes.Remove -> removeNode(node)
            Modes.Connect -> connectNode(node)
            Modes.Select -> selectNode(node)
            else -> selectNode(node)
        }
    }

    private fun connectNode(node: VSMNode) {
        // if no node selected - select it
        if (selectedId.intValue == -1) {
            selectNode(node)
        } else if (selectedId.intValue != node.id) {
            val success = ctrl.addChildToParent(node, selectedId.intValue)

            // Child will fail to be added if parent already has this child.
            // Select this child instead.
            if (!success) {
                selectNode(node)
            }
        } else {
            deselectNode(node)
        }
    }

    fun selectNode(node: VSMNode) {
        if (selectedId.intValue != node.id) {
            "Selected node: ${node.id}".e
            selectedId.intValue = node.id
        } else {
            deselectNode(node)
        }
    }

    private fun deselectNode(node: VSMNode) {
        "Deselected node: ${node.id}".e
        selectedId.intValue = -1
    }

    fun getNodeType(node: VSMNode) = ctrl.getType(node)

    fun createNode(text: String = "", coord: Offset) {
        when (mode.value) {
            Modes.Select -> ctrl.createNode(text, coord.x, coord.y)
            Modes.Connect -> {
                if (selectedId.intValue != -1) {
                    // If selected and is in connect mode - add child to selected
                    ctrl.createNewChild(selectedId.intValue, text, coord.x, coord.y)
                } else {
                    // Otherwise create new node
                    val newNode = ctrl.createNode(text, coord.x, coord.y)

                    // If in Connect mode, select new node
                    if (mode.value == Modes.Connect) {
                        selectedId.intValue = newNode.id
                    }
                }
            }

            Modes.Remove -> {/* no-op */
            }
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
        menuData.value = null
    }

    fun showDeleteDialog(node: VSMNode) {
        dialogData.value = DialogData(node)
    }

    fun hideDialog() {
        dialogData.value = null
    }

    fun toggleRemoveMode() {
        if (mode.value == Modes.Remove)
            toggleSelectMode()
        else
            mode.value = Modes.Remove
    }

    fun toggleConnectMode() {
        if (mode.value == Modes.Connect)
            toggleSelectMode()
        else
            mode.value = Modes.Connect
    }

    fun toggleSelectMode() {
        mode.value = Modes.Select
    }

    fun resetGlobalPosition() {
        globalOffset.value = Offset.Zero
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