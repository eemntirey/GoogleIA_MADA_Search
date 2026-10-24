package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DataSaverStats
import com.example.data.model.QuickLink
import com.example.ui.theme.MadaBorder
import com.example.ui.theme.MadaCyan
import com.example.ui.theme.MadaDeepDark
import com.example.ui.theme.MadaElectricBlue
import com.example.ui.theme.MadaEmerald
import com.example.ui.theme.MadaSurfaceDark
import com.example.ui.theme.MadaSurfaceHighlight
import com.example.ui.theme.MadaSurfaceVariantDark
import com.example.ui.theme.MadaTextPrimary
import com.example.ui.theme.MadaTextSecondary

@Composable
fun MadagascarHubView(
    quickLinks: List<QuickLink>,
    dataSaverStats: DataSaverStats,
    isDataSaverEnabled: Boolean,
    onNavigate: (String) -> Unit,
    onAssistantClick: () -> Unit,
    onToggleDataSaver: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MadaDeepDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Header
        item {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(MadaElectricBlue, MadaCyan)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🇲🇬", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "MadaSearch",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MadaTextPrimary
            )

            Text(
                text = "MadagascarBrowser — Rapide, Économe & Privé",
                fontSize = 13.sp,
                color = MadaCyan,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // DataSaver HUD Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MadaSurfaceDark),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MadaBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("datasaver_hub_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0C344E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = MadaCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Mode DataSaver",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MadaTextPrimary
                                )
                                Text(
                                    text = if (isDataSaverEnabled) "Actif • Économie maximale" else "Désactivé",
                                    fontSize = 11.sp,
                                    color = if (isDataSaverEnabled) MadaEmerald else MadaTextSecondary
                                )
                            }
                        }

                        Button(
                            onClick = onToggleDataSaver,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDataSaverEnabled) MadaElectricBlue else MadaSurfaceVariantDark
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isDataSaverEnabled) "Activé" else "Activer",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatPill(
                            label = "Données Économisées",
                            value = dataSaverStats.totalSavedFormatted,
                            color = MadaCyan
                        )
                        StatPill(
                            label = "Publicités Bloquées",
                            value = dataSaverStats.adsBlocked.toString(),
                            color = MadaEmerald
                        )
                        StatPill(
                            label = "Traqueurs Bloqués",
                            value = dataSaverStats.trackersBlocked.toString(),
                            color = Color(0xFFFBBF24)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
        }

        // Portails Madagascar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Accès Rapide Madagascar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MadaTextPrimary
                )

                Text(
                    text = "Connexion optimisée",
                    fontSize = 11.sp,
                    color = MadaTextSecondary
                )
            }
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(quickLinks) { link ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MadaSurfaceDark)
                            .clickable { onNavigate(link.url) }
                            .padding(8.dp)
                            .testTag("quicklink_${link.title}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MadaSurfaceVariantDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = link.iconEmoji, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = link.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MadaTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Smart Assistant Action Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MadaSurfaceVariantDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAssistantClick() }
                    .testTag("assistant_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MadaElectricBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Assistant Local MadaSearch",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MadaTextPrimary
                        )
                        Text(
                            text = "Résumez vos pages et organisez vos onglets sans cloud",
                            fontSize = 11.sp,
                            color = MadaTextSecondary
                        )
                    }

                    Text(
                        text = "Ouvrir ➔",
                        fontSize = 12.sp,
                        color = MadaCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = MadaTextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
