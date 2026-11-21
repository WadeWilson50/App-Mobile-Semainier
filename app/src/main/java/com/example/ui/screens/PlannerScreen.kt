package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DayConfig
import com.example.model.MealSlot
import com.example.model.Recipe
import com.example.model.SemainierConfig
import com.example.ui.SemainierUiState
import com.example.ui.components.ApplianceBadge
import com.example.ui.components.FavoriteStarButton
import com.example.ui.theme.Amber50
import com.example.ui.theme.Amber200
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber900
import com.example.ui.theme.Cream50
import com.example.ui.theme.Cream100
import com.example.ui.theme.Cream200
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Red600
import com.example.ui.theme.Sage50
import com.example.ui.theme.Sage100
import com.example.ui.theme.Sage600
import com.example.ui.theme.Sage700
import com.example.ui.theme.Stone100
import com.example.ui.theme.Stone200
import com.example.ui.theme.Stone300
import com.example.ui.theme.Stone400
import com.example.ui.theme.Stone500
import com.example.ui.theme.Stone600
import com.example.ui.theme.Stone700
import com.example.ui.theme.Stone800
import com.example.ui.theme.Stone900
import com.example.ui.theme.Terracotta50
import com.example.ui.theme.Terracotta100
import com.example.ui.theme.Terracotta500
import com.example.ui.theme.Terracotta600
import com.example.ui.theme.Terracotta700

@Composable
fun PlannerScreen(
    state: SemainierUiState,
    onSetGenAppliance: (String) -> Unit,
    onSetGenStyle: (String) -> Unit,
    onToggleGenFavoritesOnly: () -> Unit,
    onGenerateWeek: () -> Unit,
    onResetDefaultWeek: () -> Unit,
    onToggleSlotDisabled: (String) -> Unit,
    onUpdateSlotServings: (String, Int) -> Unit,
    onOpenRecipeDetail: (String, Int) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenSelectMeal: (String) -> Unit,
    onRegenerateSlot: (String) -> Unit,
    onRemoveSlotRecipe: (String) -> Unit,
    onSwapSlotAction: (String) -> Unit,
    onCancelSwapMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val recipeMap = remember(state.recipes) { state.recipes.associateBy { it.id } }
    val slotMap = remember(state.slots) { state.slots.associateBy { it.slotKey } }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val columns = when {
            maxWidth >= 1100.dp -> 4
            maxWidth >= 720.dp -> 2
            else -> 1
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = { GridItemSpan(columns) }) {
                PlannerActionBar(
                    genAppliance = state.genAppliance,
                    genStyle = state.genStyle,
                    genFavoritesOnly = state.genFavoritesOnly,
                    onSetGenAppliance = onSetGenAppliance,
                    onSetGenStyle = onSetGenStyle,
                    onToggleGenFavoritesOnly = onToggleGenFavoritesOnly,
                    onGenerateWeek = onGenerateWeek,
                    onResetDefaultWeek = onResetDefaultWeek
                )
            }

            if (state.swapSourceSlotKey != null) {
                item(span = { GridItemSpan(columns) }) {
                    SwapModeBanner(
                        sourceSlotKey = state.swapSourceSlotKey,
                        onCancel = onCancelSwapMode
                    )
                }
            }

            items(
                items = SemainierConfig.DAYS,
                key = { it.key }
            ) { day ->
                DayCard(
                    day = day,
                    midiSlot = slotMap["${day.key}_midi"] ?: MealSlot(
                        slotKey = "${day.key}_midi",
                        dayKey = day.key,
                        mealKey = "midi",
                        recipeId = null,
                        servings = day.defaultServings,
                        disabled = SemainierConfig.DEFAULT_DISABLED_SLOTS.contains("${day.key}_midi")
                    ),
                    soirSlot = slotMap["${day.key}_soir"] ?: MealSlot(
                        slotKey = "${day.key}_soir",
                        dayKey = day.key,
                        mealKey = "soir",
                        recipeId = null,
                        servings = day.defaultServings,
                        disabled = false
                    ),
                    recipeMap = recipeMap,
                    swapSourceSlotKey = state.swapSourceSlotKey,
                    onToggleSlotDisabled = onToggleSlotDisabled,
                    onUpdateSlotServings = onUpdateSlotServings,
                    onOpenRecipeDetail = onOpenRecipeDetail,
                    onToggleFavorite = onToggleFavorite,
                    onOpenSelectMeal = onOpenSelectMeal,
                    onRegenerateSlot = onRegenerateSlot,
                    onRemoveSlotRecipe = onRemoveSlotRecipe,
                    onSwapSlotAction = onSwapSlotAction
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlannerActionBar(
    genAppliance: String,
    genStyle: String,
    genFavoritesOnly: Boolean,
    onSetGenAppliance: (String) -> Unit,
    onSetGenStyle: (String) -> Unit,
    onToggleGenFavoritesOnly: () -> Unit,
    onGenerateWeek: () -> Unit,
    onResetDefaultWeek: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, Cream200),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterDropdownChip(
                    selectedValue = genAppliance,
                    options = listOf(
                        "all" to "Tous appareils (Varié)",
                        "Cookeo" to "🥘 Cookeo",
                        "Thermomix TM6" to "🌪️ Thermomix TM6",
                        "Airfryer" to "🍟 Airfryer",
                        "Poêle & Casserole" to "🍳 Poêle & Casserole"
                    ),
                    onSelect = onSetGenAppliance,
                    testTag = "gen_appliance_dropdown"
                )

                FilterDropdownChip(
                    selectedValue = genStyle,
                    options = listOf(
                        "all" to "Tous les styles",
                        "fast" to "⚡ Express (≤ 15 min)",
                        "Traditionnel" to "🇫🇷 Traditionnel",
                        "Convivial" to "🍕 Convivial"
                    ),
                    onSelect = onSetGenStyle,
                    testTag = "gen_style_dropdown"
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (genFavoritesOnly) Amber50 else Amber50.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, Amber200),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onToggleGenFavoritesOnly)
                        .testTag("gen_favorites_toggle")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Checkbox(
                            checked = genFavoritesOnly,
                            onCheckedChange = null,
                            colors = CheckboxDefaults.colors(
                                checkedColor = Amber400,
                                checkmarkColor = Stone900,
                                uncheckedColor = Amber900.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "⭐ ${stringResource(R.string.planner_favor_favorites)}",
                            color = Amber900,
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 12.sp
                        )
                    }
                }

                Button(
                    onClick = onGenerateWeek,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Terracotta500,
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    modifier = Modifier.testTag("generate_week_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.planner_generate_week),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Sage50)
                        .border(1.dp, Sage100, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Semaine : 3 pers.",
                        color = Sage700,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Terracotta50)
                        .border(1.dp, Terracotta100, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Week-end : 4 pers.",
                        color = Terracotta700,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                OutlinedButton(
                    onClick = onResetDefaultWeek,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Cream200),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("reset_default_week_button")
                ) {
                    Text(
                        text = stringResource(R.string.planner_reset_default),
                        color = Stone600,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterDropdownChip(
    selectedValue: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
    testTag: String
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.find { it.first == selectedValue }?.second ?: options.first().second

    Box {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Cream50,
            border = BorderStroke(1.dp, Cream200),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .testTag(testTag)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = selectedLabel,
                    color = Stone700,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Stone500,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = PureWhite
        ) {
            options.forEach { (value, label) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            fontWeight = if (value == selectedValue) FontWeight.Bold else FontWeight.Normal,
                            color = if (value == selectedValue) Terracotta600 else Stone800
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelect(value)
                    }
                )
            }
        }
    }
}

@Composable
private fun SwapModeBanner(
    sourceSlotKey: String,
    onCancel: () -> Unit
) {
    val parts = sourceSlotKey.split("_")
    val dayLabel = SemainierConfig.DAYS.find { it.key == parts.getOrNull(0) }?.label ?: ""
    val mealLabel = SemainierConfig.MEAL_TYPES.find { it.key == parts.getOrNull(1) }?.label ?: ""

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Terracotta50,
        border = BorderStroke(1.5.dp, Terracotta500),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.SwapHoriz,
                    contentDescription = null,
                    tint = Terracotta600,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Déplacement depuis $dayLabel $mealLabel : touchez un autre créneau",
                    color = Terracotta700,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            TextButton(
                onClick = onCancel,
                modifier = Modifier.testTag("cancel_swap_button")
            ) {
                Text("Annuler", color = Terracotta700, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DayCard(
    day: DayConfig,
    midiSlot: MealSlot,
    soirSlot: MealSlot,
    recipeMap: Map<String, Recipe>,
    swapSourceSlotKey: String?,
    onToggleSlotDisabled: (String) -> Unit,
    onUpdateSlotServings: (String, Int) -> Unit,
    onOpenRecipeDetail: (String, Int) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenSelectMeal: (String) -> Unit,
    onRegenerateSlot: (String) -> Unit,
    onRemoveSlotRecipe: (String) -> Unit,
    onSwapSlotAction: (String) -> Unit
) {
    val headerBg = if (day.isWeekend) Terracotta500 else Sage600
    val badgeText = if (day.isWeekend) "WE • 4 pers." else "Sem. • 3 pers."

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, Cream200),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBg)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = day.label,
                    style = MaterialTheme.typography.titleLarge,
                    color = PureWhite
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.Black.copy(alpha = 0.16f))
                        .padding(horizontal = 9.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = PureWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MealSlotCard(
                    slot = midiSlot,
                    mealLabel = "Midi",
                    mealIcon = "☀️",
                    recipe = midiSlot.recipeId?.let { recipeMap[it] },
                    swapSourceSlotKey = swapSourceSlotKey,
                    onToggleSlotDisabled = onToggleSlotDisabled,
                    onUpdateSlotServings = onUpdateSlotServings,
                    onOpenRecipeDetail = onOpenRecipeDetail,
                    onToggleFavorite = onToggleFavorite,
                    onOpenSelectMeal = onOpenSelectMeal,
                    onRegenerateSlot = onRegenerateSlot,
                    onRemoveSlotRecipe = onRemoveSlotRecipe,
                    onSwapSlotAction = onSwapSlotAction
                )
                MealSlotCard(
                    slot = soirSlot,
                    mealLabel = "Soir",
                    mealIcon = "🌙",
                    recipe = soirSlot.recipeId?.let { recipeMap[it] },
                    swapSourceSlotKey = swapSourceSlotKey,
                    onToggleSlotDisabled = onToggleSlotDisabled,
                    onUpdateSlotServings = onUpdateSlotServings,
                    onOpenRecipeDetail = onOpenRecipeDetail,
                    onToggleFavorite = onToggleFavorite,
                    onOpenSelectMeal = onOpenSelectMeal,
                    onRegenerateSlot = onRegenerateSlot,
                    onRemoveSlotRecipe = onRemoveSlotRecipe,
                    onSwapSlotAction = onSwapSlotAction
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MealSlotCard(
    slot: MealSlot,
    mealLabel: String,
    mealIcon: String,
    recipe: Recipe?,
    swapSourceSlotKey: String?,
    onToggleSlotDisabled: (String) -> Unit,
    onUpdateSlotServings: (String, Int) -> Unit,
    onOpenRecipeDetail: (String, Int) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenSelectMeal: (String) -> Unit,
    onRegenerateSlot: (String) -> Unit,
    onRemoveSlotRecipe: (String) -> Unit,
    onSwapSlotAction: (String) -> Unit
) {
    val isSwapSource = swapSourceSlotKey == slot.slotKey
    val isSwapTarget = swapSourceSlotKey != null && !isSwapSource

    if (slot.disabled) {
        val borderColor = if (isSwapTarget) Terracotta500 else Stone300
        val bgColor = if (isSwapTarget) Terracotta50 else Stone100.copy(alpha = 0.8f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(bgColor)
                .drawBehind {
                    drawRoundRect(
                        color = borderColor,
                        style = Stroke(
                            width = if (isSwapTarget) 4f else 3f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                        ),
                        cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
                    )
                }
                .then(
                    if (isSwapTarget) {
                        Modifier.clickable { onSwapSlotAction(slot.slotKey) }
                    } else Modifier
                )
                .padding(12.dp)
                .testTag("slot_${slot.slotKey}")
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$mealIcon $mealLabel",
                        color = Stone500,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Stone200.copy(alpha = 0.8f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isSwapTarget) "DÉPOSER ICI" else stringResource(R.string.planner_slot_free).uppercase(),
                            color = if (isSwapTarget) Terracotta700 else Stone600,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = if (isSwapTarget) "Touchez pour déplacer ici" else stringResource(R.string.planner_no_meal_planned),
                    color = if (isSwapTarget) Terracotta700 else Stone500,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                if (!isSwapTarget) {
                    OutlinedButton(
                        onClick = { onToggleSlotDisabled(slot.slotKey) },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Stone200),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = PureWhite),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("activate_slot_${slot.slotKey}")
                    ) {
                        Text(
                            text = stringResource(R.string.planner_activate_slot),
                            color = Stone700,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        return
    }

    if (recipe != null) {
        val cardBorder = when {
            isSwapSource -> BorderStroke(2.dp, Terracotta500)
            isSwapTarget -> BorderStroke(1.5.dp, Terracotta300OrFallback)
            recipe.isFavorite -> BorderStroke(1.dp, Amber200)
            else -> BorderStroke(1.dp, Cream200)
        }
        val cardBg = when {
            isSwapSource -> Terracotta50
            else -> Cream50.copy(alpha = 0.85f)
        }

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = cardBg,
            border = cardBorder,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .combinedClickable(
                    onClick = {
                        if (isSwapTarget) {
                            onSwapSlotAction(slot.slotKey)
                        } else if (isSwapSource) {
                            onSwapSlotAction(slot.slotKey)
                        } else {
                            onOpenRecipeDetail(recipe.id, slot.servings)
                        }
                    },
                    onLongClick = { onSwapSlotAction(slot.slotKey) }
                )
                .testTag("slot_${slot.slotKey}")
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onSwapSlotAction(slot.slotKey) }
                                .padding(4.dp)
                                .testTag("swap_grip_${slot.slotKey}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DragIndicator,
                                contentDescription = stringResource(R.string.cd_swap_slot),
                                tint = if (isSwapSource) Terracotta600 else Stone400,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "$mealIcon $mealLabel",
                            color = Stone700,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FavoriteStarButton(
                            isFavorite = recipe.isFavorite,
                            onClick = { onToggleFavorite(recipe.id) },
                            testTag = "slot_fav_${slot.slotKey}",
                            iconSize = 18
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(9.dp))
                                .background(PureWhite)
                                .border(1.dp, Cream200, RoundedCornerShape(9.dp))
                                .padding(horizontal = 2.dp, vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onUpdateSlotServings(slot.slotKey, -1) }
                                    .testTag("servings_minus_${slot.slotKey}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Remove,
                                    contentDescription = "Moins de convives",
                                    tint = Stone600,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = "${slot.servings}p",
                                color = Stone800,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 3.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onUpdateSlotServings(slot.slotKey, 1) }
                                    .testTag("servings_plus_${slot.slotKey}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Plus de convives",
                                    tint = Stone600,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = recipe.name,
                    color = Stone900,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ApplianceBadge(appliance = recipe.appliance)
                    Text(
                        text = "⏱ ${recipe.time}m",
                        color = Stone500,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Cream200.copy(alpha = 0.8f))
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onOpenSelectMeal(slot.slotKey) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("choose_meal_${slot.slotKey}")
                    ) {
                        Text(
                            text = if (isSwapTarget) "Échanger ici" else "Choisir",
                            color = if (isSwapTarget) Terracotta600 else Stone600,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onRegenerateSlot(slot.slotKey) },
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("regen_slot_${slot.slotKey}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = stringResource(R.string.cd_regenerate_slot),
                                tint = Stone500,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        IconButton(
                            onClick = { onRemoveSlotRecipe(slot.slotKey) },
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("clear_slot_${slot.slotKey}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.cd_clear_slot),
                                tint = Stone400,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Case 3: Active & Empty
        val borderColor = if (isSwapTarget) Terracotta500 else Stone300
        val bgColor = if (isSwapTarget) Terracotta50 else PureWhite
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(bgColor)
                .drawBehind {
                    drawRoundRect(
                        color = borderColor,
                        style = Stroke(
                            width = if (isSwapTarget) 4f else 3f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                        ),
                        cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
                    )
                }
                .then(
                    if (isSwapTarget) {
                        Modifier.clickable { onSwapSlotAction(slot.slotKey) }
                    } else Modifier
                )
                .padding(12.dp)
                .testTag("slot_${slot.slotKey}")
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$mealIcon $mealLabel",
                        color = Stone600,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${slot.servings} pers.",
                        color = Stone400,
                        fontSize = 12.sp
                    )
                }

                if (isSwapTarget) {
                    Text(
                        text = "Touchez pour déplacer ici",
                        color = Terracotta700,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    )
                } else {
                    OutlinedButton(
                        onClick = { onOpenSelectMeal(slot.slotKey) },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Cream200),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Cream50),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("empty_choose_meal_${slot.slotKey}")
                    ) {
                        Text(
                            text = stringResource(R.string.planner_choose_dish),
                            color = Stone700,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = stringResource(R.string.planner_mark_no_meal),
                        color = Stone400,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleSlotDisabled(slot.slotKey) }
                            .padding(vertical = 4.dp)
                            .testTag("disable_slot_${slot.slotKey}")
                    )
                }
            }
        }
    }
}

private val Terracotta300OrFallback = Color(0xFFEBA185)
