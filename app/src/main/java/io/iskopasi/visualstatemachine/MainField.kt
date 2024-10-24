@file:OptIn(ExperimentalMaterial3Api::class)

package io.iskopasi.visualstatemachine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


inline val Dp.px: Float
    @Composable get() = with(LocalDensity.current) { this@px.toPx() }

val nodeTextStyle = TextStyle(
    platformStyle = PlatformTextStyle(
        includeFontPadding = false,
    ),
    textAlign = TextAlign.Center,
    fontSize = 11.sp,
)

@Composable
fun MainField(model: UIModel) {
    ScalableField(model) {
        MapBackground()
        ConnectionsBackground(model)
        RecompositionIsolator {
            // Redrawing nodes
            for (node in model.nodes) {
                VSMNode(node, model)
            }

            // Menu
            if (model.menuData.value != null) VSCMenu(model.menuData.value!!, model)
        }
    }
}

@Composable
fun VSCMenu(menuData: MenuData, model: UIModel) {
    var name by remember {
        mutableStateOf(menuData.node.name.value)
    }

    Box(modifier = Modifier
        .width(210.dp)
        .offset {
            IntOffset(menuData.x.toInt(), menuData.y.toInt())
        }
        .border(
            width = 2.dp,
            color = Color.White,
            shape = RoundedCornerShape(8.dp)
        )
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0xFF383838))
        .padding(top = 14.dp, start = 10.dp, end = 10.dp, bottom = 0.dp)
    ) {
        Column {
            // Node Id
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.id),
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = Color.White
                    ),
                )
                Text(
                    menuData.node.id.toString(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = Color.White
                    ),
                    modifier = Modifier.width(110.dp)
                )
            }
            // Node name
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.name),
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = Color.White
                    )
                )
                EText(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    modifier = Modifier.width(110.dp)
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            // Connect to button
            Box {
                TextButton(
                    onClick = {
                        model.enableConnectMode()
                        model.selectNode(menuData.node, forceSelect = true)
                        model.hideMenu()
                    },
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonColors(
                        containerColor = Color(0x2525B3FF),
                        contentColor = Color.White,
                        disabledContentColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    ),
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.connect),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            // Remove node button
            Box {
                TextButton(
                    onClick = {
                        model.showDeleteDialog(menuData.node)
                    },
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonColors(
                        containerColor = Color(0x25FF0000),
                        contentColor = Color.White,
                        disabledContentColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    ),
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.remove),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            // Buttons
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = {
                        menuData.save(name)
                        model.hideMenu()
                    },
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        disabledContentColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,

                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        stringResource(R.string.save),
                        textAlign = TextAlign.Center,
                    )
                }
                TextButton(
                    onClick = {
                        model.hideMenu()
                    },
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        disabledContentColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,

                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        stringResource(R.string.cancel),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

//private fun DrawScope.drawSelectedBox(node: VSMNode, stepPx: Float) {
//    val selectedBoxOffset = 10.dp.toPx()
//    val selectBoxSize = stepPx + selectedBoxOffset
//
//    drawRect(
//        color = Color.White,
//        topLeft = Offset(
//            node.x.value - selectedBoxOffset / 2f,
//            node.y.value - selectedBoxOffset / 2f
//        ),
//        size = Size(selectBoxSize, selectBoxSize),
//        style = Stroke(width = 2.dp.toPx())
//    )
//}

@Composable
fun VSMNode(node: VSMNode, model: UIModel) {
    val nodeType = model.getNodeType(node)
    val cellSizePx = cellSize.px

    var savedDeltaX = 0f
    var savedDeltaY = 0f
    var savedNodeX = node.x.value
    var savedNodeY = node.y.value

    val frameGap = remember { 6.dp }

    Box(
        modifier = Modifier
            .defaultMinSize(nodeType.width)
            .height(nodeType.height)
            .offset {
                val xOffset = (cellSize.toPx() - nodeType.width.toPx()) / 2
                val yOffset = (cellSize.toPx() - nodeType.height.toPx()) / 2

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
            .pointerInput(node.id, node.id) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)

                    savedDeltaX = 0f
                    savedDeltaY = 0f
                    savedNodeX = node.x.value
                    savedNodeY = node.y.value
                }
            }
            .pointerInput(node.id, node.id) {
                detectTapGestures(onLongPress = { offset ->
                    model.showMenu(node)
                }
                ) {
                    model.onNodeClick(node)
                }
            }
            .pointerInput(node.id, node.id) {
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
            }
            .drawWithCache {
                onDrawBehind {
                    // Draw selected frame
                    if (model.selectedId.intValue == node.id)
                        drawRect(
                            color = Color.White,
                            topLeft = Offset(
                                0f - frameGap.toPx(),
                                0f - frameGap.toPx()
                            ),
                            size = Size(
                                size.width + frameGap.toPx() * 2f,
                                size.height + frameGap.toPx() * 2f
                            ),
                            style = Stroke(width = 2.dp.toPx())
                        )
                }
            }
            .padding(4.dp)
    ) {
        Text(
            text = nodeType.label,
            textAlign = TextAlign.Center,
            color = nodeType.textColor,
            overflow = TextOverflow.Visible,
            softWrap = false,
            style = nodeTextStyle,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@Composable
fun EText(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    singleLine: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
//        visualTransformation = visualTransformation,
        interactionSource = interactionSource,
//        enabled = enabled,
        singleLine = singleLine,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 13.sp
        ),
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            value = value,
            visualTransformation = VisualTransformation.None,
            innerTextField = innerTextField,
            singleLine = singleLine,
            enabled = true,
            interactionSource = interactionSource,
            contentPadding = PaddingValues(0.dp),
            colors = TextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                focusedContainerColor = Color(0xFF686868),
                unfocusedContainerColor = Color(0xFF8E8E8E),

                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
        )
    }
}