package io.iskopasi.visualstatemachine

import android.util.Log

val String.e: String
    get() {
        Log.e("-->", this)
        return this
    }