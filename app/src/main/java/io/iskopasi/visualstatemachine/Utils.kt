package io.iskopasi.visualstatemachine

import android.util.Log
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