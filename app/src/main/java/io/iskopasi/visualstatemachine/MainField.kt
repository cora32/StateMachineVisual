package io.iskopasi.visualstatemachine

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2


inline val Int.dp: Dp
    @Composable get() = with(LocalDensity.current) { this@dp.toDp() }

inline val Dp.px: Float
    @Composable get() = with(LocalDensity.current) { this@px.toPx() }

//@Preview
//@Composable
//fun MainField() {
//    var scale by remember { mutableStateOf(1f) }
//    var rotation by remember { mutableStateOf(0f) }
//    var offset by remember { mutableStateOf(Offset.Zero) }
//    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
//        scale *= zoomChange
//        rotation += rotationChange
//        offset += offsetChange
//    }
//    val background = remember { Color(0xFF292929) }
//
//    Box(
//        modifier = Modifier
//            .background(background)
//            .fillMaxSize()
//            .graphicsLayer(
//                scaleX = scale,
//                scaleY = scale,
//                rotationZ = rotation,
//                translationX = offset.x,
//                translationY = offset.y
//            )
//            .onSizeChanged {
//                size = it
//
//                points.clear()
//                for (x in (0..size.width) step step) {
//                    for (y in (0..size.height) step step) {
//                        points.add(Offset(x.toFloat(), y.toFloat()))
//                    }
//                }
//            })
//}

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
    var nodes by remember { mutableStateOf(listOf<VSMState>()) }
    val step = remember {
        50.dp
    }
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val textMeasurer = rememberTextMeasurer()
    var index = remember { 0 }
    val style = TextStyle(
        fontSize = 15.sp,
        color = Color.Black,
        background = Color.Red.copy(alpha = 0.2f)
    )
    val ctrl = remember {
        StateMachineController()
    }

    val stepPx = step.px
    val half = stepPx / 2f
    val pointRadius = 5 * stepPx / 6f

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

                "--> size: $size; ${step}; ${stepPx.toInt()}".e

                points.clear()
                var x = 0f
                var y = 0f
                // Fill background dots data
                while (x < size.width.toFloat()) {
                    while (y < size.height.toFloat()) {
                        points.add(Offset(x, y))
                        y += stepPx
                    }
                    x += stepPx
                    y = 0f
                }
            }
            .drawWithCache {
                onDrawBehind {
                    "---> onDrawBehind: ${nodes.size} nodes".e

                    // Drawing background dots
                    if (points.isNotEmpty()) {
                        drawPoints(
                            points,
                            pointMode = PointMode.Companion.Points,
                            color = pointColor,
                            strokeWidth = Stroke.DefaultMiter
                        )
                    }

                    var firstOffset = Offset.Zero
                    // Drawing arrows
                    for (node in nodes) {
                        val centerX = node.x.value + half
                        val centerY = node.y.value + half

                        var radians = 0f.toRadians()
                        drawCircle(
                            color = Color.White,
                            radius = 10f,
                            center = Offset(
                                radians.toX(centerX, stepPx),
                                radians.toY(centerY, stepPx)
                            )
                        )
                        radians = 90f.toRadians()
                        drawCircle(
                            color = Color.White,
                            radius = 10f,
                            center = Offset(
                                radians.toX(centerX, stepPx),
                                radians.toY(centerY, stepPx)
                            )
                        )
                        radians = 180f.toRadians()
                        drawCircle(
                            color = Color.White,
                            radius = 10f,
                            center = Offset(
                                radians.toX(centerX, stepPx),
                                radians.toY(centerY, stepPx)
                            )
                        )
                        radians = 270f.toRadians()
                        drawCircle(
                            color = Color.White,
                            radius = 10f,
                            center = Offset(
                                radians.toX(centerX, stepPx),
                                radians.toY(centerY, stepPx)
                            )
                        )


                        if (firstOffset == Offset.Zero) firstOffset = Offset(
                            node.x.value,
                            node.y.value,
                        )
                        else {
                            val secondOffset = Offset(
                                node.x.value,
                                node.y.value,
                            )

                            val fixedFirstX = firstOffset.x + half
                            val fixedFirstY = firstOffset.y + half
                            val fixedSecondX = secondOffset.x + half
                            val fixedSecondY = secondOffset.y + half

                            val radiansFirst =
                                atan2(fixedSecondY - fixedFirstY, fixedSecondX - fixedFirstX)
                            val radiansSecond =
                                atan2(fixedFirstY - fixedSecondY, fixedFirstX - fixedSecondX)

                            val firstX = radiansFirst.toX(fixedFirstX, pointRadius)
                            val firstY = radiansFirst.toY(fixedFirstY, pointRadius)

                            val secondX = radiansSecond.toX(fixedSecondX, pointRadius)
                            val secondY = radiansSecond.toY(fixedSecondY, pointRadius)

                            drawCircle(
                                color = Color.Yellow,
                                radius = 20f,
                                center = Offset(firstX, firstY)
                            )
                            drawCircle(
                                color = Color.Blue,
                                radius = 20f,
                                center = Offset(secondX, secondY)
                            )

                            val newFirstOffset = Offset(
                                firstX,
                                firstY
                            )
                            val newSecondOffset = Offset(
                                secondX,
                                secondY,
                            )

                            drawLine(
                                color = Color.White,
                                start = newFirstOffset,
                                end = newSecondOffset
                            )

                            firstOffset = secondOffset
                        }

//                        val textLayoutResult = textMeasurer.measure(node.name, style)
//                        drawText(
//                            textMeasurer = textMeasurer,
//                            text = node.name,
//                            style = style,
//                            topLeft = Offset(
//                                x = node.coords.x + step.value / 2f - textLayoutResult.size.width / 2,
//                                y = node.coords.y + step.value / 2f - textLayoutResult.size.height / 2,
//                            )
//                        )
                    }
                }
            }
            .transformable(state = state)
            .pointerInput(size.width, size.height) {
//                val size: IntSize = this.size

                detectTapGestures { offset ->
                    val remainingX = offset.x % stepPx
                    val x = offset.x - remainingX

                    val remainingY = offset.y % stepPx
                    val y = offset.y - remainingY

                    val coord = Offset(x, y)

                    ctrl.addNext("${index++}", coord)

                    "---> TAP: ${offset.x.dp} ${offset.y.dp}; adding: $coord".e

                    nodes = mutableListOf<VSMState>() + ctrl.nodes
                }
            }
    ) {
        for (node in nodes) {
            VSMNode(node, step)
        }
    }
}

@Composable
fun VSMNode(node: VSMState, size: Dp) {
    var boxSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    Box(
        modifier = Modifier
            .size(size)
            .offset {
                IntOffset(node.x.value.toInt(), node.y.value.toInt())
            }
            .background(Color.Red)
            .onSizeChanged {
                boxSize = it
            }
            .pointerInput(boxSize.width, boxSize.height) {
                detectDragGestures { change, dragAmount ->
                    "--> Dragging ${node.name} by $dragAmount".e
                    node.x.value += dragAmount.x
                    node.y.value += dragAmount.y
                }
            }) {
        Text(
            text = node.name,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}