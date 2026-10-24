package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MadaCyan
import com.example.ui.theme.MadaElectricBlue
import com.example.ui.theme.MadaEmerald
import com.example.ui.theme.MadaSurfaceDark
import com.example.ui.theme.MadaTextPrimary
import com.example.ui.theme.MadaTextSecondary

@Composable
fun BrowserBottomBar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    isDataSaverEnabled: Boolean,
    isAdBlockEnabled: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onHome: () -> Unit,
    onOpenBookmarksHistory: () -> Unit,
    onOpenAssistant: () -> Unit,
    onToggleDataSaver: () -> Unit,
    onToggleAdBlock: () -> Unit,
    onNewPrivateTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back
            IconButton(
                onClick = onBack,
                enabled = canGoBack,
                modifier = Modifier.testTag("nav_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Précédent",
                    tint = if (canGoBack) MadaTextPrimary else MadaTextSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Forward
            IconButton(
                onClick = onForward,
                enabled = canGoForward,
                modifier = Modifier.testTag("nav_forward")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Suivant",
                    tint = if (canGoForward) MadaTextPrimary else MadaTextSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Home
            IconButton(
                onClick = onHome,
                modifier = Modifier.testTag("nav_home")
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Accueil",
                    tint = MadaCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Bookmarks & History
            IconButton(
                onClick = onOpenBookmarksHistory,
                modifier = Modifier.testTag("nav_bookmarks")
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmarks,
                    contentDescription = "Favoris et Historique",
                    tint = MadaTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Assistant
            IconButton(
                onClick = onOpenAssistant,
                modifier = Modifier.testTag("nav_assistant")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Assistant IA",
                    tint = MadaElectricBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            // More Menu
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier.testTag("nav_more")
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Plus d'options",
                    tint = MadaTextPrimary,
                    modifier = Modifier.size(20.dp)
                )

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(MadaSurfaceDark)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Nouvel onglet privé",
                                color = Color(0xFFFBBF24),
                                fontSize = 13.sp
                            )
                        },
                        onClick = {
                            showMenu = false
                            onNewPrivateTab()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isDataSaverEnabled) "⚡ Désactiver DataSaver" else "⚡ Activer DataSaver",
                                color = if (isDataSaverEnabled) MadaEmerald else MadaTextPrimary,
                                fontSize = 13.sp
                            )
                        },
                        onClick = {
                            showMenu = false
                            onToggleDataSaver()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isAdBlockEnabled) "🛡️ Bloqueur de pub : Actif" else "🛡️ Bloqueur de pub : Inactif",
                                color = if (isAdBlockEnabled) MadaCyan else MadaTextPrimary,
                                fontSize = 13.sp
                            )
                        },
                        onClick = {
                            showMenu = false
                            onToggleAdBlock()
                        }
                    )
                }
            }
        }
    }
}
