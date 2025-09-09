package com.eitanadler.arkhamcalcredux2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.MutableStateFlow

public class AddOneTextAndLabelFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val checked = MutableStateFlow(false)
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AddOneTextAndLabelContent(checked = checked.value, onCheckedChange = { checked.value = it })
            }
        }
    }
}

@Composable
internal fun AddOneTextAndLabelContent(
    checked: Boolean,
    onCheckedChange : ((Boolean) -> Unit)?
) {

    MaterialTheme {
        Row {
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
            Text(text = "Add Another One")

        }
    }
}