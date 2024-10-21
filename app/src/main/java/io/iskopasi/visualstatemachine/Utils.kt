package io.iskopasi.visualstatemachine

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

val String.e: String
    get() {
        Log.e("-->", this)
        return this
    }

fun Int.toRadians(): Float = this.toFloat().toRadians()

fun Float.toRadians(): Float = this * (PI / 180).toFloat()

fun Float.toX(centerX: Float, distance: Float): Float = centerX + distance * cos(this)

fun Float.toY(centerY: Float, distance: Float): Float = centerY + distance * sin(this)

fun ViewModel.ui(block: suspend CoroutineScope.() -> Unit): Job = viewModelScope.launch(
    Dispatchers.Main
) {
    block(this)
}

fun ViewModel.bg(block: suspend CoroutineScope.() -> Unit): Job = viewModelScope.launch(
    Dispatchers.IO
) {
    block(this)
}

fun bg(block: suspend CoroutineScope.() -> Unit): Job = CoroutineScope(Dispatchers.IO).launch {
    block(this)
}

fun ui(block: suspend CoroutineScope.() -> Unit): Job = CoroutineScope(Dispatchers.Main).launch {
    block(this)
}