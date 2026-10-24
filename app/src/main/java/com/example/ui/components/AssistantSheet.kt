package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MadaCyan
import com.example.ui.theme.MadaDeepDark
import com.example.ui.theme.MadaElectricBlue
import com.example.ui.theme.MadaEmerald
import com.example.ui.theme.MadaSurfaceDark
import com.example.ui.theme.MadaSurfaceVariantDark
import com.example.ui.theme.MadaTextPrimary
import com.example.ui.theme.MadaTextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AssistantSheet(
    assistantMessage: String,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAsk: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MadaSurfaceDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
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

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Assistant Local MadaSearch",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MadaTextPrimary
                    )
                    Text(
                        text = "Confidentialité garantie • 100% sur l'appareil",
                        fontSize = 11.sp,
                        color = MadaEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Message Bubble
            Card(
                colors = CardDefaults.cardColors(containerColor = MadaSurfaceVariantDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = assistantMessage,
                    color = MadaTextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Actions Chips
            Text(
                text = "Suggestions rapides :",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MadaTextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SuggestionChip(
                    onClick = { onAsk("Résumer cette page") },
                    label = { Text("📄 Résumer la page", fontSize = 11.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MadaDeepDark,
                        labelColor = MadaCyan
                    )
                )

                SuggestionChip(
                    onClick = { onAsk("Organiser les onglets") },
                    label = { Text("📑 Grouper les onglets", fontSize = 11.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MadaDeepDark,
                        labelColor = MadaCyan
                    )
                )

                SuggestionChip(
                    onClick = { onAsk("Bilan DataSaver") },
                    label = { Text("⚡ Bilan économies", fontSize = 11.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MadaDeepDark,
                        labelColor = MadaCyan
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Input field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Poser une question...", fontSize = 13.sp, color = MadaTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MadaTextPrimary,
                        unfocusedTextColor = MadaTextPrimary,
                        focusedBorderColor = MadaCyan,
                        unfocusedBorderColor = Color(0xFF1E2D3D),
                        focusedContainerColor = MadaDeepDark,
                        unfocusedContainerColor = MadaDeepDark
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f).testTag("assistant_input")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (query.isNotBlank()) {
                            onAsk(query)
                            query = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MadaElectricBlue)
                        .testTag("assistant_send")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Envoyer",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
