package io.iskopasi.visualstatemachine

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

fun PointerInputScope.detectGesture() {

}

@Preview
@Composable
fun MainField() {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }

    val background = remember { Color(0xFF292929) }
    val pointColor = remember {
        Color(0xFFC2C2C2)
    }
    val points: MutableList<Offset> = remember {
        mutableListOf()
    }
    var nodes by remember { mutableStateOf(listOf<Offset>()) }
    val step = remember {
        20.dp.value.toInt()
    }
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    val ctrl = remember {
        StateMachineController().apply {
            create("Start")
            addNext("1")
            addNext("2")
            addNext("3")
            addNext("4")
            addNext("5")
            addNext("end")
        }
    }

    ctrl.reset()

    while (ctrl.hasNext()) {
        val state = ctrl.state
        "--> Currently in state: ${state.id} ${state.name}".e
        ctrl.advance()
    }

    Box(
        modifier = Modifier
            .background(background)
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation,
                translationX = offset.x,
                translationY = offset.y
            )
            .onSizeChanged {
                size = it

                points.clear()
                for (x in (0..size.width) step step) {
                    for (y in (0..size.height) step step) {
                        points.add(Offset(x.toFloat(), y.toFloat()))
                    }
                }
            }
            .drawWithCache {
                onDrawBehind {
                    "---> onDrawBehind: ${nodes.size}".e
                    if (points.isNotEmpty()) {
                        drawPoints(
                            points,
                            pointMode = PointMode.Companion.Points,
                            color = pointColor,
                            strokeWidth = Stroke.DefaultMiter
                        )
                    }

                    for (node in nodes) {
                        drawRect(
                            color = Color.Red,
                            topLeft = node,
                            size = Size(step.toFloat(), step.toFloat())
                        )
                    }
                }
            }
            .transformable(state = state)
            .pointerInput(size.width, size.height) {
                val size: IntSize = this.size

                detectTapGestures { offset ->
                    val nodeCoord = Offset(
                        offset.x.toInt() / 10 * 10f,
                        offset.y.toInt() / 10 * 10f
                    )
                    "---> TAP: ${offset.x} ${offset.y}; adding: $nodeCoord".e

                    nodes = nodes + nodeCoord
                }
            }
    )
}