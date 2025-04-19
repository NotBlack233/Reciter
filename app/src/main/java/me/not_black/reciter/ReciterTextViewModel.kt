package me.not_black.reciter

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReciterTextViewModel : ViewModel() {
    private val _text = mutableStateOf("")
    val text: State<String> get() = _text

    fun updateText(t: String) {
        _text.value = t
    }
}