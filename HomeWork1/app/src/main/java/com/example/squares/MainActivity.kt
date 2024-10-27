package com.example.squares

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.res.colorResource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.squares.SquaresViewModel

class SquaresViewModel: ViewModel() {
    var digits = mutableStateListOf<Int>()
        private set

    fun addSquare() {
        digits.add(digits.size)
    }
}

class MainActivity : ComponentActivity() {
    private val viewModel: SquaresViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrawSquares(viewModel)
        }
    }
}

@Composable
fun DrawSquares(viewModel: SquaresViewModel) {
    val cols = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT)
        3 else 4

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(cols),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(viewModel.digits) {
                    digit -> Square(digit, if (digit % 2 == 0) colorResource(id = R.color.red) else colorResource(id = R.color.blue))
                }
            }
            Button(
                onClick = {
                    viewModel.addSquare()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RectangleShape
            ) {
                Text("ADD SQUARE")
            }
        }
    }
}

@Composable
fun Square(number: Int, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(8.dp)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.white),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
