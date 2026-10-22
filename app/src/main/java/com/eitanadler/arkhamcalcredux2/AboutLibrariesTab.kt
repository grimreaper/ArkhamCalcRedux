package com.eitanadler.arkhamcalcredux2

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryBadges

@Composable
internal fun LibrariesTab(modifier: Modifier = Modifier) {
    val libraries by produceLibraries(R.raw.aboutlibraries)
    LibrariesContainer(
        modifier = modifier.fillMaxSize(),
        badges =
        LibraryBadges(
            version = true,
            author = true,
            description = true,
            license = true,
            funding = true,
        ),
        libraries = libraries,
    )
}
