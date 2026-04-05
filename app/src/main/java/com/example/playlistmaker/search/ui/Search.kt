package com.example.playlistmaker.search.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.root.ui.Header
import com.example.playlistmaker.root.ui.YpBlack
import com.example.playlistmaker.root.ui.YpBlue
import com.example.playlistmaker.root.ui.YpGray
import com.example.playlistmaker.root.ui.YpLightGray
import com.example.playlistmaker.root.ui.YpWhite
import com.example.playlistmaker.root.ui.yandexDisplayFonts


@Composable
fun Search() {
    var text by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 48.dp)
    ) {
        Header(stringResource(R.string.search))
        SearchTextField(text) { newText -> text = newText }
        SearchError{Log.d("123", "123")}
    }
}

@Composable
fun SearchTextField(
    query: String,
    onValueChanged: (String) -> Unit
) {
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
                    color = YpLightGray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(start = 14.dp, top = 10.dp, bottom = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search),
                contentDescription = "Поиск",
                tint = YpGray,
                modifier = Modifier.size(16.dp)
            )

            BasicTextField(
                value = query,
                onValueChange = onValueChanged,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                singleLine = true,
                cursorBrush = SolidColor(YpBlue),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search),
                            style = TextStyle(
                                color = YpGray,
                                fontSize = 16.sp,
                                fontStyle = FontStyle.Normal,
                                fontFamily = yandexDisplayFonts,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
fun SearchResult() {

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
            contentDescription = stringResource(R.string.search_is_empty)
        )
        Text(
            stringResource(R.string.search_is_empty), style = TextStyle(
                color = YpBlack,
                fontSize = 18.sp,
                fontStyle = FontStyle.Normal,
                fontFamily = yandexDisplayFonts,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(top = 16.dp)
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
                color = YpBlack,
                fontSize = 20.sp,
                fontStyle = FontStyle.Normal,
                fontFamily = yandexDisplayFonts,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            stringResource(R.string.search_no_internet_load), style = TextStyle(
                color = YpBlack,
                fontSize = 20.sp,
                fontStyle = FontStyle.Normal,
                fontFamily = yandexDisplayFonts,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(top = 16.dp)
        )
        Button(onClick = onClick, modifier = Modifier.padding(top = 24.dp),) {
            Text(
                stringResource(R.string.search_update), style = TextStyle(
                    color = YpWhite,
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Normal,
                    fontFamily = yandexDisplayFonts,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Preview(name = "main", device = "spec:parent=pixel_9,navigation=buttons", showSystemUi = true)
@Composable
fun Preview() {
    Search()
}