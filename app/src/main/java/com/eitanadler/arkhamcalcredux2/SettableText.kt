package com.eitanadler.arkhamcalcredux2

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.flow.MutableStateFlow

public class SettableText(context: Context, attrs: AttributeSet) :
    AbstractComposeView(context, attrs) {

    private val _text = MutableStateFlow("100%")
    private val _colour: MutableStateFlow<Color> = MutableStateFlow(Color.Black)

    public fun setText(new: String) {
        _text.value = new
    }

    public fun setTextColor(colour: Color) {
        _colour.value = colour
    }


    @Composable
    override fun Content() {
        val text = _text.collectAsState()
        val colour = _colour.collectAsState()

        MaterialTheme {
            Column {
                SettableTextContent(text = text.value, colour = colour.value)
            }
        }
    }
}

@Composable
internal fun SettableTextContent(
    text: String,
    colour: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        color = colour,
        modifier = modifier
    )
}