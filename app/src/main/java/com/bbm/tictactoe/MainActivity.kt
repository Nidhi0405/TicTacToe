package com.bbm.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bbm.tictactoe.ui.theme.TicTacToeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class Win {
    PLAYER,
    AI,
    DRAW
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTacToeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TTTScreen()
                }
            }
        }
    }
}

@Composable
fun TTTScreen() {
    //true means player turn, false means AI turn
    val playerTurn = remember { mutableStateOf(true) }
    //true means player move, false means AI move, null means no move
    val moves = remember {
        mutableStateListOf<Boolean?>(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }

    val win = remember{
        mutableStateOf<Win?>(null)
    }

    val onTap: (Offset) -> Unit = {
        if (playerTurn.value && win.value==null) {
            val x = (it.x / 333).toInt()
            val y = (it.y / 333).toInt()
            val posInMoves = y * 3 + x
            if (moves[posInMoves] == null) {
                moves[posInMoves] = true
                playerTurn.value = false
                win.value= checkGameEnd(moves)
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Tic Tac Toe", fontSize = 30.sp, modifier = Modifier.padding((16.dp)))
        Header(playerTurn.value)
        Board(moves, onTap)
        if (!playerTurn.value && win.value==null) {
            CircularProgressIndicator(color = Color.Red, modifier = Modifier.padding(16.dp))
            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(key1 = Unit) {
                coroutineScope.launch {
                    delay(1500L)
                    while (true) {
                        val i = Random.nextInt(9)
                        if (moves[i] == null) {
                            moves[i] = false
                            playerTurn.value = true
                            win.value= checkGameEnd(moves)
                            break
                        }
                    }
                }
            }
        }
        if(win.value!=null){
            when(win.value){
                Win.PLAYER -> {
                    Text(text="Player has won \uD83C\uDF89", fontSize = 25.sp)
                }
                Win.AI -> {
                    Text(text="AI has won \uD83D\uDE24", fontSize = 25.sp)
                }
                Win.DRAW -> {
                    Text(text="It's a draw \uD83D\uDE33", fontSize = 25.sp)
                }
                else -> {}
            }
            Button(onClick = {
                playerTurn.value=true
                win.value=null
                for(i in 0..8){
                    moves[i]=null
                }
            }) {
                Text(text = ("Click to start over"))
            }
        }
    }

}

fun checkGameEnd(m: List<Boolean?>): Win? {
    var win: Win? = null
    if (
        (m[0] == true && m[1] == true && m[2] == true) ||
        (m[3] == true && m[4] == true && m[5] == true) ||
        (m[6] == true && m[7] == true && m[8] == true) ||
        (m[0] == true && m[3] == true && m[6] == true) ||
        (m[1] == true && m[4] == true && m[7] == true) ||
        (m[2] == true && m[5] == true && m[8] == true) ||
        (m[0] == true && m[4] == true && m[8] == true) ||
        (m[2] == true && m[4] == true && m[6] == true)
    )
        win = Win.PLAYER

    if (
        (m[0] == false && m[1] == false && m[2] == false) ||
        (m[3] == false && m[4] == false && m[5] == false) ||
        (m[6] == false && m[7] == false && m[8] == false) ||
        (m[0] == false && m[3] == false && m[6] == false) ||
        (m[1] == false && m[4] == false && m[7] == false) ||
        (m[2] == false && m[5] == false && m[8] == false) ||
        (m[0] == false && m[4] == false && m[8] == false) ||
        (m[2] == false && m[4] == false && m[6] == false)
    )
        win = Win.AI

    if (win==null) {
        var available = false
        for (i in 0..8) {
            if (m[i] == null)
                available = true
        }
        if (!available)
            win = Win.DRAW
    }
    return win
}

@Composable
fun Header(playerTurn: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val playerBoxColor = if (playerTurn) Color.Gray else Color.LightGray
        val aiBoxColor = if (playerTurn) Color.LightGray else Color.Gray

        Box(
            modifier = Modifier
                .width(100.dp)
                .background(playerBoxColor)
        ) {
            Text(
                text = "Player", modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.width(50.dp))

        Box(
            modifier = Modifier
                .width(100.dp)
                .background(aiBoxColor)
        ) {
            Text(
                text = "AI", modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun Board(moves: List<Boolean?>, onTap: (Offset) -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(32.dp)
            .background(Color.LightGray)
            .pointerInput(Unit) {
                detectTapGestures(onTap = onTap)
            }
    ) {
        Column(verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize(1f)) {
            Row(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth(1f)
                    .background(Color.Black)
            ) {}
            Row(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth(1f)
                    .background(Color.Black)
            ) {}
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize(1f)) {
            Column(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight(1f)
                    .background(Color.Black)
            ) {}
            Column(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight(1f)
                    .background(Color.Black)
            ) {}
        }
        Column(modifier = Modifier.fillMaxSize(1f)) {
            for (i in 0..2) {
                Row(modifier = Modifier.weight(1f)) {
                    for (j in 0..2) {
                        Column(modifier = Modifier.weight(1f)) {
                            GetComposableFromMove(move = moves[i * 3 + j])
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GetComposableFromMove(move: Boolean?) {
    when (move) {
        true -> Image(
            painter = painterResource(id = R.drawable.ic_x),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(1f),
            colorFilter = ColorFilter.tint(Color.Gray)
        )

        false -> Image(
            painter = painterResource(id = R.drawable.ic_o),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(1f),
            colorFilter = ColorFilter.tint(Color.Red)
        )

        null -> Image(
            painter = painterResource(id = R.drawable.ic_null),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(1f)
        )
    }
}

//https://www.youtube.com/watch?v=DQlabh1p2HI&list=PL2aB10mYqlf97jah9GKpzWUfc_zqWLpKA&index=2
//https://www.youtube.com/watch?v=-0zXgoby72Y&list=PL2aB10mYqlf97jah9GKpzWUfc_zqWLpKA&index=1