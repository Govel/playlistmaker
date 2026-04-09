package com.example.playlistmaker.search.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.playlistmaker.R
import com.example.playlistmaker.root.ui.Header
import com.example.playlistmaker.root.ui.PlaylistMakerTheme
import com.example.playlistmaker.root.ui.YpBlack
import com.example.playlistmaker.root.ui.YpBlue
import com.example.playlistmaker.root.ui.yandexDisplayFonts
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.model.SearchState
import com.example.playlistmaker.util.LocalUtils


@Composable
fun Search(
    historyTracks: List<Track>,
    state: SearchState,
    textFieldText: String,
    searchTextField: (String) -> Unit,
    updateResult: () -> Unit,
    onTrackClick: (Track) -> Unit,
    clearHistory: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Header(stringResource(R.string.search))
        SearchTextField(
            query = textFieldText,
            onValueChanged = searchTextField,
            onSearch = { query ->
                searchTextField(query)
                updateResult()
            },
            onClear = {
                searchTextField("")},)

        if (textFieldText.isEmpty() &&
            historyTracks.isNotEmpty() &&
            state is SearchState.StandBy) {
            SearchHistory(
                tracks = historyTracks,
                onClick = onTrackClick,
                clearHistory = clearHistory
            )
        }
        Render(state = state, onTrackClick = onTrackClick, onUpdateClick = updateResult)
    }
}


@Composable
fun SearchTextField(
    query: String, onValueChanged: (String) -> Unit, onSearch: (String) -> Unit, onClear: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Box(
        modifier = Modifier
            .height(52.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.onBackground, shape = RoundedCornerShape(8.dp)
                )
                .padding(start = 12.dp, top = 10.dp, bottom = 10.dp, end = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier.size(16.dp)
            )
            BasicTextField(
                value = query,
                onValueChange = onValueChanged,
                textStyle = TextStyle(
                    color = YpBlack,
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Normal,
                    fontFamily = yandexDisplayFonts,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                singleLine = true,
                cursorBrush = SolidColor(YpBlue),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSearch(query)
                        keyboardController?.hide()
                    }

                ),
                decorationBox = { innerTextField ->
                    Box {
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search), style = TextStyle(
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    fontSize = 16.sp,
                                    fontStyle = FontStyle.Normal,
                                    fontFamily = yandexDisplayFonts,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )
            if (query.isNotEmpty()) {
                Icon(
                    painter = painterResource(id = R.drawable.clean_the_search_query),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable(onClick = {
                            onClear()
                        })
                )
            }
        }
    }
}

@Composable
fun SearchProgressBar() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(44.dp),
            color = YpBlue,
            strokeWidth = 4.dp,
        )
    }
}

@Composable
fun SearchHistory(tracks: List<Track>, onClick: (Track) -> Unit, clearHistory: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.height(52.dp)) {
            Text(
                text = stringResource(R.string.looking_for), style = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Normal,
                    fontFamily = yandexDisplayFonts,
                    fontWeight = FontWeight.Medium
                ), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
        ) {
            items(
                items = tracks,
                key = { it.trackId }
            ) { track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clickable(onClick = { onClick(track) }),
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(track.artworkUrl100)
                            .crossfade(true).build(),
                        contentDescription = "artwork url",
                        placeholder = painterResource(R.drawable.placeholder_cover),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(45.dp)
                            .padding(start = 12.dp)
                            .aspectRatio(1f)
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = track.trackName, style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 16.sp,
                                fontStyle = FontStyle.Normal,
                                fontFamily = yandexDisplayFonts,
                                fontWeight = FontWeight.Normal
                            ), maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = track.artistName,
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontSize = 10.sp,
                                    fontStyle = FontStyle.Normal,
                                    fontFamily = yandexDisplayFonts,
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Icon(
                                painter = painterResource(R.drawable.dot),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = LocalUtils().dateFormat(track.trackTimeMillis),
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontSize = 10.sp,
                                    fontStyle = FontStyle.Normal,
                                    fontFamily = yandexDisplayFonts,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                    Icon(
                        painter = painterResource(R.drawable.arrowforward),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(24.dp)
                    )
                }
            }
        }
        Button(
            onClick = clearHistory,
            modifier = Modifier
                .padding(top = 24.dp, bottom = 12.dp)
                .height(36.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary),
            shape = RoundedCornerShape(54.dp)

        ) {
            Text(
                stringResource(R.string.search_clear_history), style = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Normal,
                    fontFamily = yandexDisplayFonts,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun SearchResult(tracks: List<Track>, onClick: (Track) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            items(
                items = tracks,
                key = { it.trackId }
            ) { track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clickable(onClick = { onClick(track) }),
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(track.artworkUrl100)
                            .crossfade(true).build(),
                        contentDescription = "artwork url",
                        placeholder = painterResource(R.drawable.placeholder_cover),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(45.dp)
                            .padding(start = 12.dp)
                            .aspectRatio(1f),
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = track.trackName, style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 16.sp,
                                fontStyle = FontStyle.Normal,
                                fontFamily = yandexDisplayFonts,
                                fontWeight = FontWeight.Normal
                            ), maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = track.artistName,
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontSize = 10.sp,
                                    fontStyle = FontStyle.Normal,
                                    fontFamily = yandexDisplayFonts,
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Icon(
                                painter = painterResource(R.drawable.dot),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = LocalUtils().dateFormat(track.trackTimeMillis),
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontSize = 10.sp,
                                    fontStyle = FontStyle.Normal,
                                    fontFamily = yandexDisplayFonts,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                    Icon(
                        painter = painterResource(R.drawable.arrowforward),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchEmpty() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 102.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.placeholder_empty_search),
            contentDescription = stringResource(R.string.search_is_empty),
        )
        Text(
            stringResource(R.string.search_is_empty), style = TextStyle(
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
fun SearchError(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 102.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.placeholder_no_internet),
            contentDescription = stringResource(R.string.search_no_internet)
        )
        Text(
            stringResource(R.string.search_no_internet), style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp,
                fontStyle = FontStyle.Normal,
                fontFamily = yandexDisplayFonts,
                fontWeight = FontWeight.Medium
            ), modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            stringResource(R.string.search_no_internet_load),
            style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp,
                fontStyle = FontStyle.Normal,
                fontFamily = yandexDisplayFonts,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(top = 16.dp, start = 24.dp, end = 24.dp),
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onClick,
            modifier = Modifier
                .padding(top = 24.dp)
                .height(36.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary),
            shape = RoundedCornerShape(54.dp)
        ) {
            Text(
                stringResource(R.string.search_update), style = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Normal,
                    fontFamily = yandexDisplayFonts,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun Render(state: SearchState, onTrackClick: (Track) -> Unit, onUpdateClick: () -> Unit) {
    when (state) {
        is SearchState.Loading -> SearchProgressBar()
        is SearchState.Content -> SearchResult(state.tracks, onTrackClick)
        is SearchState.Empty -> SearchEmpty()
        is SearchState.Error -> SearchError(onClick = onUpdateClick)
        else -> {}
    }
}

@Preview(name = "main", device = "spec:parent=pixel_9,navigation=buttons", showSystemUi = true)
@Composable
fun Preview() {
    PlaylistMakerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            Search(
                state = SearchState.Error("Error"),
                textFieldText = "",
                searchTextField = {Log.d("","")},
                updateResult = {Log.d("","")},
                onTrackClick = {Log.d("","")},
                historyTracks = listOf(Track(123,"","",123,"","","","","","",true)),
                clearHistory = {}
            )
        }
    }
}

@Preview(name = "main", device = "spec:parent=pixel_9,navigation=buttons", showSystemUi = true)
@Composable
fun PreviewDark() {
    PlaylistMakerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            Search(
                state = SearchState.StandBy,
                textFieldText = "",
                searchTextField = {Log.d("","")},
                updateResult = {Log.d("","")},
                onTrackClick = {Log.d("","")},
                historyTracks = listOf(Track(123,"","",123,"","","","","","",true)),
                clearHistory = {}
            )
        }
    }
}