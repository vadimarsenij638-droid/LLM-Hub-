package com.llmhub.llmhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.llmhub.llmhub.ai.GeminiHelper
import com.llmhub.llmhub.utils.TTSManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var ttsManager: TTSManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Ініціалізуємо TTS для голосу Діани
        ttsManager = TTSManager(this)

        setContent {
            LlmHubTheme { // Зберігаємо оригінальну тему (dark/light)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DianaMainScreen(ttsManager = ttsManager)
                }
            }
        }
    }

    override fun onDestroy() {
        ttsManager.shutdown()
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DianaMainScreen(ttsManager: TTSManager) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var isListening by remember { mutableStateOf(false) }

    // Анімація пульсації кнопки
    val scale by animateFloatAsState(
        targetValue = if (isListening) 1.35f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f),
        label = "pulse"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Діана",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Divider()
                NavigationDrawerItem(
                    label = { Text("Текстовий режим") },
                    selected = false,
                    onClick = { /* Пізніше підключимо старий чат */ }
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Версія 1.0 • 2025",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Діана") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Меню")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                // Велика кругла кнопка мікрофона
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .scale(scale)
                        .background(Color(0xFF9C27B0), CircleShape) // Фіолетовий — стильний жіночий колір
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = { offset ->
                                    isListening = true
                                    ttsManager.speak("Слухаю тебе...")
                                    val pressed = tryAwaitRelease()
                                    isListening = false

                                    if (pressed) {
                                        ttsManager.speak("Думаю...")
                                        scope.launch {
                                            // Тестовий запит — пізніше замінимо на реальний розпізнаний текст
                                            val response = GeminiHelper.generateResponse(
                                                "Привіт! Ти Діана, розумний голосовий помічник-дівчинка. Представся коротко українською."
                                            )
                                            ttsManager.speak(response)
                                        }
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Говорити з Діаною",
                        tint = Color.White,
                        modifier = Modifier.size(110.dp)
                    )
                }

                // Підказка зверху
                Text(
                    text = "Натисни і утримуй, щоб поговорити з Діаною",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 80.dp)
                )

                // Кнопка текстового режиму знизу
                Button(
                    onClick = { /* Пізніше відкриємо старий чат */ },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(32.dp)
                ) {
                    Text("Текстовий режим")
                }
            }
        }
    }
}
