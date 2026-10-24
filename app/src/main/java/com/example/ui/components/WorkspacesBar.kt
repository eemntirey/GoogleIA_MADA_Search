package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Workspace
import com.example.ui.theme.MadaBorder
import com.example.ui.theme.MadaSurfaceHighlight
import com.example.ui.theme.MadaTextPrimary
import com.example.ui.theme.MadaTextSecondary

@Composable
fun WorkspacesBar(
    workspaces: List<Workspace>,
    selectedWorkspaceId: String,
    onWorkspaceSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        workspaces.forEach { ws ->
            val isSelected = ws.id == selectedWorkspaceId
            val wsColor = Color(ws.colorHex)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) wsColor.copy(alpha = 0.25f) else Color.Transparent)
                    .clickable { onWorkspaceSelected(ws.id) }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("workspace_${ws.id}")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(wsColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = ws.name,
                        color = if (isSelected) MadaTextPrimary else MadaTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}
