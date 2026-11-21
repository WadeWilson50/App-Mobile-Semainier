package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.SemainierConfig
import com.example.ui.ToastEvent
import com.example.ui.theme.Amber50
import com.example.ui.theme.Amber200
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber800
import com.example.ui.theme.Cream100
import com.example.ui.theme.Indigo50
import com.example.ui.theme.Indigo200
import com.example.ui.theme.Indigo800
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Sage700
import com.example.ui.theme.Stone400
import com.example.ui.theme.Stone600
import com.example.ui.theme.Stone800
import com.example.ui.theme.Terracotta50
import com.example.ui.theme.Terracotta100
import com.example.ui.theme.Terracotta700

@Composable
fun ApplianceBadge(
    appliance: String,
    modifier: Modifier = Modifier
) {
    val meta = SemainierConfig.applianceMeta(appliance)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(meta.bgColor)
            .border(1.dp, meta.borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(
            text = "${meta.icon} $appliance",
            color = meta.textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CategoryBadge(
    category: String,
    modifier: Modifier = Modifier
) {
    val isTrad = category == "Traditionnel"
    val bg = if (isTrad) Amber50 else Terracotta50
    val text = if (isTrad) Amber800 else Terracotta700
    val border = if (isTrad) Amber200 else Terracotta100
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = category,
            color = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
fun MomentBadge(
    moment: String,
    modifier: Modifier = Modifier
) {
    if (moment != "midi" && moment != "soir") return
    val isMidi = moment == "midi"
    val bg = if (isMidi) Amber50 else Indigo50
    val text = if (isMidi) Amber800 else Indigo800
    val border = if (isMidi) Amber200 else Indigo200
    val label = if (isMidi) "☀️ Midi" else "🌙 Soir"
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
fun TimeBadge(
    minutes: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Cream100)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = "⏱ $minutes min",
            color = Stone600,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
fun FavoriteStarButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
    iconSize: Int = 20
) {
    var justActivated by remember { mutableStateOf(false) }
    LaunchedEffect(isFavorite) {
        if (isFavorite) {
            justActivated = true
        } else {
            justActivated = false
        }
    }
    val scale by animateFloatAsState(
        targetValue = if (justActivated) 1.0f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "star_scale"
    )

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
            contentDescription = stringResource(R.string.cd_favorite_toggle),
            tint = if (isFavorite) Amber400 else Stone400,
            modifier = Modifier
                .size(iconSize.dp)
                .scale(scale)
        )
    }
}

@Composable
fun ToastBanner(
    toast: ToastEvent?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = toast != null,
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 },
        modifier = modifier
    ) {
        if (toast != null) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (toast.isInfo) Stone800 else Sage700,
                contentColor = PureWhite,
                shadowElevation = 6.dp,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (toast.isInfo) Icons.Filled.Info else Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = toast.message,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = PureWhite
                    )
                }
            }
        }
    }
}
