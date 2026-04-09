package com.example.playlistmaker.media.ui

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Surface
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Tab
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TabRow
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.Lifecycle
import com.example.playlistmaker.R
import com.example.playlistmaker.media.favorite.ui.FavoriteTracksFragment
import com.example.playlistmaker.media.playlists.ui.PlaylistsFragment
import com.example.playlistmaker.root.ui.Header
import com.example.playlistmaker.root.ui.PlaylistMakerTheme
import kotlinx.coroutines.launch

@Composable
fun Media() {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Header(text = stringResource(R.string.media))
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Tab(
                selected = pagerState.currentPage == 0,
                selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                unselectedContentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                text = { Text(text = stringResource(R.string.favorite_tracks)) },
            )

            Tab(
                selected = pagerState.currentPage == 1,
                selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                unselectedContentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
                text = { Text(text = stringResource(R.string.playlists)) },
            )
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when(page) {
                0 -> FragmentPage(FavoriteTracksFragment())
                1 -> FragmentPage(PlaylistsFragment())
            }
        }
    }
}

@SuppressLint("ContextCastToActivity")
@Composable
fun FragmentPage(fragment: Fragment) {
    val activity = LocalContext.current as AppCompatActivity
    val fragmentManager = activity.supportFragmentManager
    val containerId = remember { View.generateViewId() }

    AndroidView(
        factory = { context ->
            FragmentContainerView(context).apply {
                id = containerId
                if (fragmentManager.findFragmentById(containerId) == null) {
                    fragmentManager.beginTransaction()
                        .add(containerId, fragment)
                        .commit()
                }
            }
        },
        update = { _ ->
            val currentFragment = fragmentManager.findFragmentById(containerId)
            if (currentFragment != null) {
                fragmentManager.beginTransaction()
                    .setMaxLifecycle(currentFragment, Lifecycle.State.RESUMED)
                    .commit()
            }
        }
    )
}

@Preview(name = "main", device = "spec:parent=pixel_9,navigation=buttons", showSystemUi = true)
@Composable
fun PreviewMedia() {
    PlaylistMakerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            Media()
        }
    }
}

@Preview(name = "main", device = "spec:parent=pixel_9,navigation=buttons", showSystemUi = true)
@Composable
fun PreviewMediaDark() {
    PlaylistMakerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            Media()
        }
    }
}