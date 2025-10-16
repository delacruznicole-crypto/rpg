@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.rpg

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

fun Double.format(digits: Int) = "%.${digits}f".format(this)

@Composable
fun GameScreen(viewModel: GameViewModel = viewModel()) {

    val playerHealth by viewModel.playerHealth
    val enemyHealth by viewModel.enemyHealth

    // Gamer-style background gradient
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0D0D0D),
            Color(0xFF1B1B2F),
            Color(0xFF162447)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚔️ RPG Battle Simulator", color = Color.Cyan) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF111122)
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .background(backgroundGradient)
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Character Health Cards ---
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CharacterCard("Player", playerHealth, Color(0xFF00FFAA))
                CharacterCard("Enemy", enemyHealth, Color(0xFFFF5555))
            }

            Spacer(Modifier.height(20.dp))

            // --- Cooldown Display ---
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CooldownColumn(
                    "Player",
                    viewModel.playerAttackRemaining.value,
                    viewModel.playerBlockRemaining.value,
                    viewModel.playerHealRemaining.value,
                    viewModel.playerPowerStrikeRemaining.value,
                    Color(0xFF00FFAA)
                )
                CooldownColumn(
                    "Enemy",
                    viewModel.enemyAttackRemaining.value,
                    viewModel.enemyBlockRemaining.value,
                    viewModel.enemyHealRemaining.value,
                    viewModel.enemyPowerStrikeRemaining.value,
                    Color(0xFFFF5555)
                )
            }

            Spacer(Modifier.height(20.dp))

            // --- Reset Button ---
            Button(
                onClick = { viewModel.resetGame() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1F4068),
                    contentColor = Color.White
                )
            ) {
                Text("Reset Game")
            }

            Spacer(Modifier.height(16.dp))

            // --- Battle Log ---
            Text("Battle Log", fontWeight = FontWeight.Bold, color = Color.Cyan)
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(viewModel.combatLogs.size) { index ->
                    Text(
                        viewModel.combatLogs[index],
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CharacterCard(name: String, health: Int, color: Color) {
    Card(
        Modifier
            .width(150.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF222244))
    ) {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(name, fontWeight = FontWeight.Bold, color = color)
            LinearProgressIndicator(
                progress = health / 100f,
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Text("$health / 100 HP", color = Color.White)
        }
    }
}

@Composable
fun CooldownColumn(
    title: String,
    attack: Long,
    block: Long,
    heal: Long,
    powerStrike: Long,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontWeight = FontWeight.Bold, color = color)
        Text("Attack: ${(attack / 1000.0).format(1)}s", color = Color.White)
        Text("Block: ${(block / 1000.0).format(1)}s", color = Color.White)
        Text("Heal: ${(heal / 1000.0).format(1)}s", color = Color.White)
        Text("Power Strike: ${(powerStrike / 1000.0).format(1)}s", color = Color.White)
    }
}
