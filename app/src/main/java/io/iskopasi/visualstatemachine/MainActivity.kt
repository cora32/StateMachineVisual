package io.iskopasi.visualstatemachine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import io.iskopasi.visualstatemachine.ui.theme.VisualStateMachineTheme


class MainActivity : ComponentActivity() {
    private val model: UIModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            VisualStateMachineTheme {
                Scaffold(
                    containerColor = Color(0xFF5E5E5E),
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) { MainField(model) }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                        ) {
                            ControlButtons(model)
                        }
                    }

                    // Remove node Dialog
                    if (model.dialogData.value != null)
                        ShowDialog(model.dialogData.value!!.node, {
                            model.removeNode(model.dialogData.value!!.node)
                            model.hideDialog()
                            model.hideMenu()
                        }, {
                            model.hideDialog()
                        })
                }
            }
        }
    }

    @Composable
    fun ShowDialog(node: VSMNode, onConfirmation: () -> Unit, onDismissRequest: () -> Unit) {
        AlertDialog(
            icon = {
                Icon(Icons.Rounded.Warning, contentDescription = "Are you sure")
            },
            title = {
                Text(text = stringResource(R.string.remove_node_dialog_title, node.name.value))
            },
            text = {
                Text(text = stringResource(R.string.remove_node_dialog_body, node.name.value))
            },
            onDismissRequest = {
                onDismissRequest()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmation()
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}