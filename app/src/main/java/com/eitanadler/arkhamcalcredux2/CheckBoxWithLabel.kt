package com.eitanadler.arkhamcalcredux2

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow

public class AllModifierOptions(context: Context, attrs: AttributeSet) :
    AbstractComposeView(context, attrs) {

    private val _mandyCheckBox = MutableStateFlow(false)
    private val _skidsOnesCheckBox = MutableStateFlow(false)
    private val _rerollOnesCheckBox = MutableStateFlow(false)
    private val _addOneCheckBox = MutableStateFlow(false)
    private val _shotgunCheckBox = MutableStateFlow(false)

    private var mandyEvent: (() -> Unit)? = null
    private var skidsEvent: (() -> Unit)? = null
    private var rerollOnesEvent: (() -> Unit)? = null
    private var addOneEvent: (() -> Unit)? = null
    private var shotgunEvent: (() -> Unit)? = null

    public fun isMandyChecked(): Boolean {
        return _mandyCheckBox.value
    }

    public fun isSkidsChecked(): Boolean {
        return _skidsOnesCheckBox.value
    }

    public fun isRerollChecked(): Boolean {
        return _rerollOnesCheckBox.value
    }

    public fun isAddOneChecked(): Boolean {
        return _addOneCheckBox.value
    }

    public fun isShotgunChecked(): Boolean {
        return _shotgunCheckBox.value
    }

    public fun setMandyChecked(new: Boolean) {
        _mandyCheckBox.value = new
    }

    public fun setSkidsChecked(new: Boolean) {
        _skidsOnesCheckBox.value = new
    }

    public fun setRerollChecked(new: Boolean) {
        _rerollOnesCheckBox.value = new
    }

    public fun setAddOneChecked(new: Boolean) {
        _addOneCheckBox.value = new
    }

    public fun setShotgunChecked(new: Boolean) {
        _shotgunCheckBox.value = new
    }

    public fun setMandyOnChangedEvent(event: () -> Unit) {
        mandyEvent = event
    }

    public fun setSkidsOnChangedEvent(event: () -> Unit) {
        skidsEvent = event
    }

    public fun setRerollOnChangedEvent(event: () -> Unit) {
        rerollOnesEvent = event
    }

    public fun setAddOneOnChangedEvent(event: () -> Unit) {
        addOneEvent = event
    }

    public fun setShotgunOnChangedEvent(event: () -> Unit) {
        shotgunEvent = event
    }

    /**
     * Start the help activity with the specified topic opened. The topic passed
     * into this method must exist in the help.xml 'topics' array.
     */
    private fun startHelpActivity(context: Context, topic: String) {
        val helpIntent = Intent(context, ArkhamCalcHelp::class.java)
        helpIntent.putExtra(ArkhamCalcHelp.BUNDLE_TOPIC, topic)
        context.startActivity(helpIntent)
    }


    @Composable
    override fun Content() {
        val mandyCheckBox = _mandyCheckBox.collectAsState()
        val skidsOnesCheckBox = _skidsOnesCheckBox.collectAsState()
        val rerollOnesCheckBox = _rerollOnesCheckBox.collectAsState()
        val addOneCheckBox = _addOneCheckBox.collectAsState()
        val shotgunCheckBox = _shotgunCheckBox.collectAsState()


        Column {
            val context = LocalContext.current
            AddOneTextAndLabelContent(
                checked = mandyCheckBox.value,
                onCheckedChange = {
                    _mandyCheckBox.value = it
                    mandyEvent?.invoke()
                },
                label = "Mandy",
                modifier = Modifier
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            startHelpActivity(context, "Mandy")
                        }
                    ),
            )
            AddOneTextAndLabelContent(
                checked = skidsOnesCheckBox.value,
                onCheckedChange = {
                    _skidsOnesCheckBox.value = it
                    skidsEvent?.invoke()
                },
                label = "Skids",
                modifier = Modifier
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            startHelpActivity(context, "Skids")
                        }
                    ),

                )
            AddOneTextAndLabelContent(
                checked = rerollOnesCheckBox.value,
                onCheckedChange = {
                    _rerollOnesCheckBox.value = it
                    rerollOnesEvent?.invoke()
                },
                label = "Reroll Ones",
                modifier = Modifier
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            startHelpActivity(context, "Reroll Ones")
                        }
                    ),
            )
            AddOneTextAndLabelContent(
                checked = addOneCheckBox.value,
                onCheckedChange = {
                    _addOneCheckBox.value = it
                    addOneEvent?.invoke()
                },
                label = "Add One",
                modifier = Modifier
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            startHelpActivity(context, "Add One")
                        }
                    ),
            )
            AddOneTextAndLabelContent(
                checked = shotgunCheckBox.value,
                onCheckedChange = {
                    _shotgunCheckBox.value = it
                    shotgunEvent?.invoke()
                },
                label = "Shotgun",
                modifier = Modifier
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            startHelpActivity(context, "Shotgun")
                        }
                    ),
            )
        }
    }
}

@Composable
internal fun AddOneTextAndLabelContent(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(text = label, modifier = modifier) // wrong place for it, but leave for now
    }

}