package io.iskopasi.visualstatemachine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ControlButtons(model: UIModel) {
    // Select mode color selections
    val selectModeTextColor = if (model.mode.value == Modes.Select)
        selectModeTextColorActive
    else
        selectModeTextColorInactive
    val selectModeBgColor = if (model.mode.value == Modes.Select)
        selectModeBgColorActive
    else
        selectModeBgColorInactive

    // Connect mode color selections
    val connectModeTextColor = if (model.mode.value == Modes.Connect)
        connectModeTextColorActive
    else
        connectModeTextColorInactive
    val connectModeBgColor = if (model.mode.value == Modes.Connect)
        connectModeBgColorActive
    else
        connectModeBgColorInactive

    // Remove mode color selections
    val removeModeTextColor = if (model.mode.value == Modes.Remove)
        removeModeTextColorActive
    else
        removeModeTextColorInactive
    val removeModeBgColor = if (model.mode.value == Modes.Remove)
        removeModeBgColorActive
    else
        removeModeBgColorInactive

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Go home button
        Box(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 8.dp, bottom = 12.dp),
        ) {
            IconButton(
                onClick = {
                    model.resetGlobalPosition()
                },
                modifier = Modifier.size(54.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xB0151515),
                    contentColor = Color.White
                ),
            ) {
                Icon(Icons.Outlined.Place, "Home", tint = Color.White)
            }

        }
        // Control buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Delete mode
            TextButton(
                onClick = {
                    model.toggleRemoveMode()
                    model.hideMenu()
                },
                shape = RoundedCornerShape(0.dp),
                colors = ButtonColors(
                    containerColor = removeModeBgColor,
                    contentColor = removeModeTextColor,
                    disabledContentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                ),
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(.3f)
            ) {
                Text(
                    stringResource(R.string.remove),
                    textAlign = TextAlign.Center,
                )
            }
            // Connect mode
            TextButton(
                onClick = {
                    model.toggleConnectMode()
                    model.hideMenu()
                },
                shape = RoundedCornerShape(0.dp),
                colors = ButtonColors(
                    containerColor = connectModeBgColor,
                    contentColor = connectModeTextColor,
                    disabledContentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                ),
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(.6f)
            ) {
                Text(
                    stringResource(R.string.connect),
                    textAlign = TextAlign.Center,
                )
            }
            // Select mode
            TextButton(
                onClick = {
                    model.toggleSelectMode()
                    model.hideMenu()
                },
                shape = RoundedCornerShape(0.dp),
                colors = ButtonColors(
                    containerColor = selectModeBgColor,
                    contentColor = selectModeTextColor,
                    disabledContentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                ),
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(1f)
            ) {
                Text(
                    stringResource(R.string.select_mode),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}