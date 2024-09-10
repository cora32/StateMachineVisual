package io.iskopasi.visualstatemachine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.iskopasi.visualstatemachine.ui.theme.VisualStateMachineTheme

class MainActivity : ComponentActivity() {
    private val model: UIModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
//            val background = remember { Color(0xFF292929) }
//            var scale by remember { mutableFloatStateOf(1f) }
////    var rotation by remember { mutableFloatStateOf(0f) }
//            var offset by remember { mutableStateOf(Offset.Zero) }
//            val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
//                scale *= zoomChange
////        rotation += rotationChange
//                offset += offsetChange * scale
//            }
//            val points: MutableList<Offset> = remember {
//                mutableListOf()
//            }
//            var size by remember {
//                mutableStateOf(IntSize.Zero)
//            }
//            val textMeasurer = rememberTextMeasurer()
//            val stepPx = model.cellSize.px
//            val pointColor = remember {
//                Color(0xFFC2C2C2)
//            }

            VisualStateMachineTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
//                    .width(5000.dp)
//                    .height(5000.dp)
//                    .wrapContentSize(unbounded = true, align = Alignment.TopStart),
//                    .onSizeChanged {
//                        size = it
//
//                        points.clear()
//                        var x = 0f
//                        var y = 0f
//
//                        // Fill background dots data
//                        while (x < 5000f) {
//                            while (y < 5000f) {
//                                points.add(Offset(x, y))
//                                y += stepPx
//                            }
//
//                            x += stepPx
//                            y = 0f
//                        }
//                    }
//                    .graphicsLayer {
//                        scaleX = scale
//                        scaleY = scale
////                rotationZ = rotation,
//                        translationX = offset.x
//                        translationY = offset.y
//                    }
//                    .transformable(state = state)
//                    .drawWithCache {
//                        onDrawBehind {
//                            // Drawing background dots
//                            if (points.isNotEmpty()) {
//                                drawPoints(
//                                    points,
//                                    pointMode = PointMode.Companion.Points,
//                                    color = pointColor,
//                                    strokeWidth = Stroke.DefaultMiter
//                                )
//                            }
//                        }
//                    }

                ) { innerPadding ->
                    Box(
                        modifier = Modifier
//                        .width(IntrinsicSize.Max)
//                        .height(IntrinsicSize.Max)
//                        .background(background)
                            .padding(innerPadding)
                    )
                    MainField(model)

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
