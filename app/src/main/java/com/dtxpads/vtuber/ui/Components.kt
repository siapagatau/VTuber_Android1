package com.dtxpads.vtuber.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dtxpads.vtuber.ui.theme.AppTextSize
import com.dtxpads.vtuber.ui.theme.ControlIconSize
import com.dtxpads.vtuber.ui.theme.PadTextColor
import com.dtxpads.vtuber.ui.theme.PanelColor
import com.dtxpads.vtuber.ui.theme.RecordRed
import com.dtxpads.vtuber.ui.theme.TextDim

/** Bentuk tombol aksi seragam: sudut 5dp seperti tombol aksi DTX. */
val ActionButtonShape = RoundedCornerShape(5.dp)

/**
 * Tombol aksi ala strip kontrol DTX: Material3 Button, latar PanelColor, ikon + label huruf besar
 * 12sp SemiBold. [caption] dipakai untuk menandai fitur yang BELUM tersedia (mis. "CP2") —
 * tombol demikian dibuat nonaktif, bukan pura-pura berfungsi.
 */
@Composable
fun ActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    active: Boolean = false,
    caption: String? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (active) RecordRed else PanelColor,
            contentColor = if (active) Color.White else PadTextColor,
            disabledContainerColor = PanelColor.copy(alpha = 0.45f),
            disabledContentColor = PadTextColor.copy(alpha = 0.5f)
        ),
        shape = ActionButtonShape,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        modifier = modifier.heightIn(min = 48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(ControlIconSize))
            Column {
                Text(
                    text = label,
                    fontSize = AppTextSize,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                if (caption != null) {
                    Text(text = caption, fontSize = 9.sp, color = TextDim, maxLines = 1)
                }
            }
        }
    }
}
