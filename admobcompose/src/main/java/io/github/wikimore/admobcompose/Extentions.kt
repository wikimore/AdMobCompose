package io.github.wikimore.admobcompose

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity

fun Context.getActivity(): Activity? = when (this) {
    is ComponentActivity -> this
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}