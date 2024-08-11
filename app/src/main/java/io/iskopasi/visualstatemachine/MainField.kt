package io.iskopasi.visualstatemachine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2


//inline val Int.dp: Dp
//    @Composable get() = with(LocalDensity.current) { this@dp.toDp() }

inline val Dp.px: Float
    @Composable get() = with(LocalDensity.current) { this@px.toPx() }

@Composable
fun MainField(model: UIModel) {
    var scale by remember { mutableFloatStateOf(1f) }
//    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
//        rotation += rotationChange
        offset += offsetChange
    }
    val background = remember { Color(0xFF292929) }
    val pointColor = remember {
        Color(0xFFC2C2C2)
    }
    val points: MutableList<Offset> = remember {
        mutableListOf()
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

    val stepPx = model.cellSize.px
    val half = remember { stepPx / 2f }
    val pointRadius = remember { 5 * stepPx / 6f }
    val wingRadius = 8.dp.px

    Box(
        modifier = Modifier
            .background(background)
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
//                rotationZ = rotation,
                translationX = offset.x,
                translationY = offset.y
            )
            .onSizeChanged {
                size = it

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
                    // Drawing background dots
                    if (points.isNotEmpty()) {
                        drawPoints(
                            points,
                            pointMode = PointMode.Companion.Points,
                            color = pointColor,
                            strokeWidth = Stroke.DefaultMiter
                        )
                    }

                    // Drawing arrows
                    for (node in model.nodes) {
                        if (model.selectedId.intValue == node.id)
                            drawSelectedBox(node, stepPx)
                        drawArrows(node, half, pointRadius, wingRadius)

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
                detectTapGestures { offset ->
                    val remainingX = offset.x % stepPx
                    val x = offset.x - remainingX

                    val remainingY = offset.y % stepPx
                    val y = offset.y - remainingY

                    model.createNode("${index++}", Offset(x, y))
                }
            }
    ) {
        for (node in model.nodes) {
            VSMNode(node, model)
        }
    }
}

private fun DrawScope.drawSelectedBox(node: VSMNode, stepPx: Float) {
    val selectedBoxOffset = 10.dp.toPx()
    val selectBoxSize = stepPx + selectedBoxOffset

    drawRect(
        color = Color.White,
        topLeft = Offset(
            node.x.value - selectedBoxOffset / 2f,
            node.y.value - selectedBoxOffset / 2f
        ),
        size = Size(selectBoxSize, selectBoxSize),
        style = Stroke(width = 2.dp.toPx())
    )
}

private fun DrawScope.drawArrows(
    node: VSMNode,
    half: Float,
    pointRadius: Float,
    wingRadius: Float
) {
    val firstOffset = Offset(
        node.x.value,
        node.y.value,
    )

    for (child in node.nodes) {
        val secondOffset = Offset(
            child.x.value,
            child.y.value,
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


        val newFirstOffset = Offset(
            firstX,
            firstY
        )
        val newSecondOffset = Offset(
            secondX,
            secondY,
        )
        val leftWingOffset = Offset(
            (radiansSecond - 40f.toRadians()).toX(newSecondOffset.x, wingRadius),
            (radiansSecond - 40.toRadians()).toY(newSecondOffset.y, wingRadius),
        )
        val rightWingOffset = Offset(
            (radiansSecond + 40.toRadians()).toX(newSecondOffset.x, wingRadius),
            (radiansSecond + 40.toRadians()).toY(newSecondOffset.y, wingRadius),
        )

        drawLine(
            color = Color.White,
            start = newFirstOffset,
            end = newSecondOffset
        )
        drawLine(
            color = Color.White,
            start = newSecondOffset,
            end = leftWingOffset
        )
        drawLine(
            color = Color.White,
            start = newSecondOffset,
            end = rightWingOffset
        )
    }
}

@Composable
fun VSMNode(node: VSMNode, model: UIModel) {
    val nodeType = model.getNodeType(node)
    val cellSizePx = model.cellSize.px

    var savedDeltaX = 0f
    var savedDeltaY = 0f
    var savedNodeX = node.x.value
    var savedNodeY = node.y.value

    Box(
        modifier = Modifier
            .width(nodeType.width)
            .height(nodeType.height)
            .offset {
                val xOffset = (model.cellSize.toPx() - nodeType.width.toPx()) / 2
                val yOffset = (model.cellSize.toPx() - nodeType.height.toPx()) / 2

                IntOffset(
                    (node.x.value.toInt() + xOffset).toInt(),
                    (node.y.value.toInt() + yOffset).toInt()
                )
            }
            .background(nodeType.color)
            .border(
                width = 1.dp,
                color = Color.White
            )
            .pointerInput(nodeType.width.value, nodeType.height.value) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)

                    savedDeltaX = 0f
                    savedDeltaY = 0f
                    savedNodeX = node.x.value
                    savedNodeY = node.y.value
                }
            }
            .pointerInput(nodeType.width.value, nodeType.height.value) {
                detectTapGestures {
                    model.selectNode(node)
                }
            }
            .pointerInput(nodeType.width.value, nodeType.height.value) {
                detectDragGestures { change, dragAmount ->
                    savedDeltaX += dragAmount.x
                    savedDeltaY += dragAmount.y

                    val newDeltaX = cellSizePx * (savedDeltaX / cellSizePx).toInt()
                    val newX = savedNodeX + newDeltaX
                    if (node.x.value != newX) {
                        node.x.value = newX
                    }

                    val newDeltaY = cellSizePx * (savedDeltaY / cellSizePx).toInt()
                    val newY = savedNodeY + newDeltaY
                    if (node.y.value != newY) {
                        node.y.value = newY
                    }
                }
            }) {
        Text(
            text = nodeType.label,
            textAlign = TextAlign.Center,
            color = nodeType.textColor,
            fontSize = 11.sp,
            overflow = TextOverflow.Visible,
            softWrap = false,
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false,
                ),
            ),
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}