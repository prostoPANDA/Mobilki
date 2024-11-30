package com.example.fuckingcoil

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder


class MainActivity : ComponentActivity() {

    private lateinit var gifCountPreferences: GifCountPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gifCountPreferences = GifCountPreferences(this)

        setContent {
            MainOn(gifCountPreferences)
        }
    }
}

class GifCountPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("GifCountPrefs", Context.MODE_PRIVATE)

    fun saveGifCount(count: Int) {
        sharedPreferences.edit().putInt("gifCount", count).apply()
    }

    fun getGifCount(): Int {
        return sharedPreferences.getInt("gifCount", 0)
    }
}

sealed class ImageState {
    object Loading : ImageState()
    object Success : ImageState()
    object Error : ImageState()

    companion object {
        val saver = Saver<MutableState<ImageState>, Int>(
            save = { state ->
                when (state.value) {
                    Loading -> 0
                    Success -> 1
                    Error -> 2
                }
            },
            restore = { value ->
                when (value) {
                    0 -> mutableStateOf(Loading)
                    1 -> mutableStateOf(Success)
                    2 -> mutableStateOf(Error)
                    else -> mutableStateOf(Loading)
                }
            }
        )
    }
}


@Composable
fun MainOn(gcp: GifCountPreferences) {

    val mainUrl = "https://cataas.com/cat/gif"

    val gifCount = rememberSaveable { mutableIntStateOf(gcp.getGifCount()) }

    val columns = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT)
        2 else 3

    val gifEnabledLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (SDK_INT >= 28) {
                add(AnimatedImageDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyVerticalGrid (
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(gifCount.intValue) { gifIndex->

                    val reload = rememberSaveable { mutableStateOf(false) }

                    key (reload.value) {

                        val state = rememberSaveable(saver = ImageState.saver) { mutableStateOf<ImageState>(ImageState.Loading) }

                        val painter = rememberAsyncImagePainter(
                            model = "$mainUrl?_$gifIndex",
                            imageLoader = gifEnabledLoader,
                            onSuccess = { state.value = ImageState.Success },
                            onError = { state.value = ImageState.Error }
                        )

                        when (state.value) {
                            is ImageState.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .align(Alignment.Center)
                                    )
                                }
                            }
                            is ImageState.Success -> {
                                Image(
                                    painter = painter,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp),
                                )
                            }
                            is ImageState.Error -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .padding(4.dp)
                                        .background(Color.Gray)
                                        .clickable {
                                            reload.value = !reload.value
                                        }
                                ) {
                                    Text(
                                        "TRY AGAIN",
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Button(
                onClick = {
                    gifCount.intValue++
                    gcp.saveGifCount(gifCount.intValue)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RectangleShape
            ) {
                Text("GET A CAT")
            }
        }
    }
}