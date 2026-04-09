package com.example.playlistmaker.media.playlists.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.playlistmaker.R
import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist
import com.example.playlistmaker.media.playlists.ui.models.PlaylistsState
import com.example.playlistmaker.root.ui.PlaylistMakerTheme
import com.example.playlistmaker.root.ui.yandexDisplayFonts

@Composable
fun Playlists(
    state: PlaylistsState,
    onClickNewPlaylist: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    viewModel: PlaylistsViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClickNewPlaylist,
            modifier = Modifier
                .padding(top = 24.dp)
                .height(36.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary),
            shape = RoundedCornerShape(54.dp)
        ) {
            Text(
                text = stringResource(R.string.new_playlist),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Normal,
                    fontFamily = yandexDisplayFonts,
                    fontWeight = FontWeight.Medium,
                )
            )
        }
        Spacer(modifier = Modifier.padding(bottom = 8.dp))
        Render(
            state = state,
            onClick = onPlaylistClick,
            viewModel = viewModel
            )
    }
}

@Composable
fun PlaylistList(
    playlists: List<Playlist>,
    onClick: (Playlist) -> Unit,
    viewModel: PlaylistsViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(playlists) { item ->
            PlaylistCard(
                playlist = item,
                onClick = { onClick(item)},
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun PlaylistCard(
    playlist: Playlist,
    onClick: (Playlist) -> Unit,
    viewModel: PlaylistsViewModel
) {
    val imageUri = remember(playlist.imgPath) {
        playlist.imgPath.let { viewModel.getUriForCover(it) }
    }
    Column(modifier = Modifier.clickable(onClick = { onClick(playlist) })) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(imageUri ?: R.drawable.placeholder_cover)
                .crossfade(true).build(),
            placeholder = painterResource(R.drawable.placeholder_cover),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = playlist.name,
            style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 12.sp,
                fontStyle = FontStyle.Normal,
                fontFamily = yandexDisplayFonts,
                fontWeight = FontWeight.Normal,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = pluralStringResource(
                id = R.plurals.tracks,
                count = playlist.totalTracks,
                playlist.totalTracks
                ),
            style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 12.sp,
                fontStyle = FontStyle.Normal,
                fontFamily = yandexDisplayFonts,
                fontWeight = FontWeight.Normal,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PlaylistsEmpty() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 44.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.placeholder_empty_search),
            contentDescription = stringResource(R.string.search_is_empty)
        )
        Text(
            stringResource(R.string.playlists_is_empty), style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp,
                fontStyle = FontStyle.Normal,
                fontFamily = yandexDisplayFonts,
                fontWeight = FontWeight.Medium
            ), modifier = Modifier.padding(top = 16.dp), textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Render(state: PlaylistsState, onClick: (Playlist) -> Unit, viewModel: PlaylistsViewModel) {
    when (state) {
        is PlaylistsState.Empty -> PlaylistsEmpty()
        is PlaylistsState.Content -> PlaylistList(playlists = state.playlists, onClick = onClick, viewModel = viewModel)
    }
}

@Preview(name = "main", device = "spec:parent=pixel_9,navigation=buttons", showSystemUi = true)
@Composable
fun Preview() {
    PlaylistMakerTheme(darkTheme = true) {
    }
}