package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SecurityStatus
import com.example.ui.theme.MadaBorder
import com.example.ui.theme.MadaCyan
import com.example.ui.theme.MadaElectricBlue
import com.example.ui.theme.MadaEmerald
import com.example.ui.theme.MadaSurfaceVariantDark
import com.example.ui.theme.MadaTextPrimary
import com.example.ui.theme.MadaTextSecondary
import com.example.ui.theme.MadaWarning

@Composable
fun OmniboxBar(
    currentUrl: String,
    isLoading: Boolean,
    securityStatus: SecurityStatus,
    tabCount: Int,
    isDataSaverActive: Boolean,
    dataSaverSavings: String,
    isBookmarked: Boolean,
    onNavigate: (String) -> Unit,
    onReload: () -> Unit,
    onTabsClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onDataSaverClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var textInput by remember { mutableStateOf(currentUrl) }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(currentUrl) {
        if (!isFocused) {
            textInput = if (currentUrl == "madasearch://home") "" else currentUrl
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Omnibox Input Field
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(MadaSurfaceVariantDark)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Security Icon
                    Icon(
                        imageVector = when (securityStatus) {
                            SecurityStatus.SECURE_HTTPS -> Icons.Default.Lock
                            SecurityStatus.WARNING -> Icons.Default.Warning
                            else -> Icons.Default.Lock
                        },
                        contentDescription = "Sécurité",
                        tint = when (securityStatus) {
                            SecurityStatus.SECURE_HTTPS -> MadaEmerald
                            SecurityStatus.WARNING -> MadaWarning
                            else -> MadaCyan
                        },
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        if (textInput.isEmpty() && !isFocused) {
                            Text(
                                text = "Rechercher ou saisir une adresse...",
                                color = MadaTextSecondary,
                                fontSize = 14.sp
                            )
                        }

                        BasicTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = MadaTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            cursorBrush = SolidColor(MadaCyan),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(onGo = {
                                focusManager.clearFocus()
                                onNavigate(textInput)
                            }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { isFocused = it.isFocused }
                                .testTag("omnibox_input")
                        )
                    }

                    if (textInput.isNotEmpty() && isFocused) {
                        IconButton(
                            onClick = { textInput = "" },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Effacer",
                                tint = MadaTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = onReload,
                            modifier = Modifier.size(28.dp).testTag("reload_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Actualiser",
                                tint = MadaTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // DataSaver Pill Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDataSaverActive) Color(0xFF0C344E) else MadaBorder)
                    .clickable { onDataSaverClick() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("datasaver_toggle")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⚡",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = if (isDataSaverActive) dataSaverSavings else "OFF",
                        color = if (isDataSaverActive) MadaCyan else MadaTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Bookmark Button
            IconButton(
                onClick = onBookmarkClick,
                modifier = Modifier.size(36.dp).testTag("bookmark_button")
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Favori",
                    tint = if (isBookmarked) Color(0xFFFBBF24) else MadaTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Tabs Switcher Button
            IconButton(
                onClick = onTabsClick,
                modifier = Modifier.size(36.dp).testTag("tabs_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MadaSurfaceVariantDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tabCount.toString(),
                        color = MadaCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
