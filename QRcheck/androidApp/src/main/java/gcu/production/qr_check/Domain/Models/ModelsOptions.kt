@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.Models

import androidx.lifecycle.MutableLiveData

infix fun <T: Any?> MutableLiveData<T>.default(initialState: T) =
    apply {
        value = initialState
    }

infix fun <T: Any?> MutableLiveData<T>.set(newState: T) =
    apply {
        value = newState
    }

fun <T: Any?> MutableLiveData<T>.update() =
    apply {
        value = value
    }