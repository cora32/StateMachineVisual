package io.iskopasi.visualstatemachine

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.max


@Composable
fun ConnectionsBackground(model: UIModel) {
    val cellSizePx = cellSize.px
    val textMeasurer = rememberTextMeasurer()
    val halfOfNode = remember { cellSizePx / 2f }
    val pointRadius = remember { 5 * cellSizePx / 6f }
    val wingRadius = 8.dp.px
    val selectedWidth = remember {
        2.dp
    }
    val defaultWidth = remember {
        0.4.dp
    }

    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer()
            .drawWithCache {
                onDrawBehind {
                    // Drawing graphics
                    for (node in model.nodes) {
                        // Draw arrows
                        if (node.children.isNotEmpty()) {
                            val textLayoutResult =
                                textMeasurer.measure(node.name.value, nodeTextStyle)

                            drawArrows(
                                node,
                                xOffset = max(textLayoutResult.size.width / 2f, halfOfNode),
                                yOffset = halfOfNode,
                                defaultHalfOfNode = halfOfNode,
                                pointRadius,
                                wingRadius,
                                textMeasurer = textMeasurer,
                                strokeWidth = if (model.isSelected(node)) selectedWidth.toPx() else defaultWidth.toPx()
                            )
                        }
                    }
                }
            }) {

    }
}

@Composable
fun MapBackground() {
    val cellSizePx = cellSize.px
    val pointColor = remember {
        Color(0xFFC2C2C2)
    }
    val pointList: MutableList<Offset> = remember {
        mutableListOf()
    }
    val width = mapSize.px
    val height = mapSize.px
    val points = remember(mapSize) {
        pointList.clear()
        var x = 0f
        var y = 0f

        // Fill background dots data
        while (x < width) {
            while (y < height) {
                pointList.add(Offset(x, y))
                y += cellSizePx
            }

            x += cellSizePx
            y = 0f
        }

        pointList
    }

    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer()
            .drawWithCache {
                onDrawBehind {
                    // Drawing background dots
                    if (points.isNotEmpty()) {
                        "--> drawPoints".e
                        drawPoints(
                            points,
                            pointMode = PointMode.Companion.Points,
                            color = pointColor,
                            strokeWidth = Stroke.DefaultMiter
                        )
                    }
                }
            }) {

    }
}

@Composable
fun ScalableField(
    model: UIModel,
    block: @Composable BoxScope.() -> Unit
) {
    val cellSizePx = cellSize.px
    var scale by remember { mutableFloatStateOf(1f) }
//    var rotation by remember { mutableFloatStateOf(0f) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
//        rotation += rotationChange
        model.globalOffset.value += offsetChange * scale
    }
    val backgroundColor = when (model.mode.value) {
        Modes.Remove -> Color(0xFF320000)
        Modes.Connect -> Color(0xFF0E3140)
        Modes.Select -> Color(0xFF2D2D2D)
    }

    Box(modifier = Modifier
        .wrapContentSize(unbounded = true, align = Alignment.Center)
        .size(mapSize)
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
//                rotationZ = rotation,
            translationX = model.globalOffset.value.x
            translationY = model.globalOffset.value.y
        }
        .background(backgroundColor)
        .transformable(state = state)
        .pointerInput(1, 2) {
            detectTapGestures { offset ->
                model.hideMenu()

                val remainingX = offset.x % cellSizePx
                val x = offset.x - remainingX

                val remainingY = offset.y % cellSizePx
                val y = offset.y - remainingY

                model.createNode(coord = Offset(x, y))
            }
        }) {
        block()
    }
}

@Composable
fun RecompositionIsolator(content: @Composable () -> Unit) {
    Box {
        content()
    }
}

private fun DrawScope.drawArrows(
    node: VSMNode,
    xOffset: Float,
    yOffset: Float,
    defaultHalfOfNode: Float,
    pointRadius: Float,
    wingRadius: Float,
    textMeasurer: TextMeasurer,
    strokeWidth: Float
) {
    val firstOffset = Offset(
        node.x.value,
        node.y.value,
    )

    for (child in node.children) {
        val secondOffset = Offset(
            child.x.value,
            child.y.value,
        )

        val textLayoutResult = textMeasurer.measure(child.name.value, nodeTextStyle)

        val fixedFirstX = firstOffset.x + xOffset
        val fixedFirstY = firstOffset.y + yOffset
        val fixedSecondX = secondOffset.x + max(textLayoutResult.size.width / 2f, defaultHalfOfNode)
        val fixedSecondY = secondOffset.y + yOffset

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
            end = newSecondOffset,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = newSecondOffset,
            end = leftWingOffset,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = newSecondOffset,
            end = rightWingOffset,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}