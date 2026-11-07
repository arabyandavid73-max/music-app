package com.example.musicapp

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppRoot()
            }
        }
    }
}

sealed class Screen {
    object Login : Screen()
    object Player : Screen()
}

@Composable
fun AppRoot() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
        },
        label = "ScreenTransition"
    ) { screen ->
        when (screen) {
            is Screen.Login -> LoginScreen(onLoginSuccess = { currentScreen = Screen.Player })
            is Screen.Player -> MusicPlayerScreen(onLogout = { currentScreen = Screen.Login })
        }
    }
}

@Composable
fun SmoothElevatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF6C5CE7),
    contentColor: Color = Color.White,
    content: @Composable RowScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ButtonScale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 12.dp,
        label = "ButtonShadow"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(24.dp),
                ambientColor = backgroundColor.copy(alpha = 0.5f),
                spotColor = backgroundColor
            )
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                content()
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F0C20), Color(0xFF1B143F), Color(0xFF080614))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = Color(0xFF8A4FFF)
                ),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16122C))
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = null,
                    tint = Color(0xFFA278FF),
                    modifier = Modifier.size(56.dp)
                )

                Text(
                    text = "WaveBeat",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Войдите, чтобы слушать музыку",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Логин") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFA278FF),
                        unfocusedBorderColor = Color.DarkGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Пароль") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFA278FF),
                        unfocusedBorderColor = Color.DarkGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                SmoothElevatedButton(
                    onClick = {
                        if (username.isNotBlank() && password.isNotBlank()) {
                            onLoginSuccess()
                        } else {
                            Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFF8A4FFF)
                ) {
                    Text("Войти", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun MusicPlayerScreen(onLogout: () -> Unit) {
    var isPlaying by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableFloatStateOf(0.25f) }

    val mediaPlayer = remember {
        MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            try {
                setDataSource("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
                prepareAsync()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "VinylSpin")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    val vinylScale by animateFloatAsState(
        targetValue = if (isPlaying) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "VinylScale"
    )

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1E1738), Color(0xFF0F0C20), Color(0xFF05030A))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onLogout) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Выход", tint = Color.White)
            }
            Text("СЕЙЧАС ИГРАЕТ", color = Color.Gray, fontSize = 12.sp, letterSpacing = 2.sp)
            IconButton(onClick = { }) {
                Icon(Icons.Rounded.FavoriteBorder, contentDescription = null, tint = Color.White)
            }
        }

        Box(
            modifier = Modifier
                .size(260.dp)
                .scale(vinylScale)
                .shadow(
                    elevation = if (isPlaying) 32.dp else 12.dp,
                    shape = CircleShape,
                    spotColor = Color(0xFFA278FF),
                    ambientColor = Color(0xFFA278FF)
                )
                .rotate(if (isPlaying) rotationAngle else 0f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF2C254A), Color(0xFF120E22), Color.Black)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8A4FFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.MusicNote, contentDescription = null, tint = Color.White)
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Cyberpunk Odyssey",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Neon Dreamer",
                color = Color.Gray,
                fontSize = 16.sp
            )
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = currentProgress,
                onValueChange = { currentProgress = it },
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFA278FF),
                    activeTrackColor = Color(0xFFA278FF),
                    inactiveTrackColor = Color.DarkGray
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("1:15", color = Color.Gray, fontSize = 12.sp)
                Text("4:20", color = Color.Gray, fontSize = 12.sp)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlRoundButton(icon = Icons.Rounded.SkipPrevious, size = 48.dp) { }

            SmoothElevatedButton(
                onClick = {
                    isPlaying = !isPlaying
                    if (isPlaying) {
                        try { mediaPlayer.start() } catch (_: Exception) {}
                    } else {
                        try { mediaPlayer.pause() } catch (_: Exception) {}
                    }
                },
                modifier = Modifier.size(72.dp),
                backgroundColor = Color(0xFF8A4FFF)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (isPlaying) "Пауза" else "Играть",
                    modifier = Modifier.size(36.dp)
                )
            }

            ControlRoundButton(icon = Icons.Rounded.SkipNext, size = 48.dp) { }
        }
    }
}

@Composable
fun ControlRoundButton(
    icon: ImageVector,
    size: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "ControlScale"
    )

    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .shadow(6.dp, CircleShape, spotColor = Color.White.copy(alpha = 0.2f))
            .clip(CircleShape)
            .background(Color(0xFF221C3E))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White)
    }
}
