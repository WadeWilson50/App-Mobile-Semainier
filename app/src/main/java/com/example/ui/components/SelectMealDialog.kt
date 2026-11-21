package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.MealSlot
import com.example.model.Recipe
import com.example.model.SemainierConfig
import com.example.ui.theme.Amber50
import com.example.ui.theme.Amber200
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber800
import com.example.ui.theme.Cream50
import com.example.ui.theme.Cream100
import com.example.ui.theme.Cream200
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Stone400
import com.example.ui.theme.Stone500
import com.example.ui.theme.Stone700
import com.example.ui.theme.Stone800
import com.example.ui.theme.Stone900
import com.example.ui.theme.Terracotta500
import com.example.ui.theme.Terracotta700

@Composable
fun SelectMealDialog(
    slotKey: String,
    slots: List<MealSlot>,
    recipes: List<Recipe>,
    search: String,
    applianceFilter: String,
    categoryFilter: String,
    favoritesOnly: Boolean,
    onSearchChange: (String) -> Unit,
    onApplianceFilterChange: (String) -> Unit,
    onCategoryFilterChange: (String) -> Unit,
    onToggleFavoritesOnly: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onSelectRecipe: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val parts = slotKey.split("_")
    val dayObj = SemainierConfig.DAYS.find { it.key == parts.getOrNull(0) }
    val mealObj = SemainierConfig.MEAL_TYPES.find { it.key == parts.getOrNull(1) }
    val servings = slots.find { it.slotKey == slotKey }?.servings ?: dayObj?.defaultServings ?: 3
    val currentMealKey = parts.getOrNull(1)

    val filteredRecipes = remember(
        recipes, search, applianceFilter, categoryFilter, favoritesOnly, currentMealKey
    ) {
        val q = search.lowercase().trim()
        recipes.filter { r ->
            if (favoritesOnly && !r.isFavorite) return@filter false
            val matchSearch = q.isEmpty() ||
                r.name.lowercase().contains(q) ||
                r.ingredients.any { it.name.lowercase().contains(q) }
            if (!matchSearch) return@filter false
            if (applianceFilter != "all" && r.appliance != applianceFilter) return@filter false
            when (categoryFilter) {
                "all" -> true
                "fast" -> r.time <= 15
                else -> r.category == categoryFilter
            }
        }.sortedWith(
            compareByDescending<Recipe> { it.isFavorite }
                .thenByDescending { if (currentMealKey != null && it.moment == currentMealKey) 1 else 0 }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = PureWhite,
            border = BorderStroke(1.dp, Cream200),
            shadowElevation = 8.dp,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .widthIn(max = 620.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.86f)
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Cream50)
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${dayObj?.label ?: ""} ${mealObj?.label ?: ""}".trim(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = Stone900
                        )
                        Text(
                            text = "Prévu pour $servings personnes • Sélectionnez une recette",
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone500
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_select_meal_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.cd_close),
                            tint = Stone500
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Cream200))

                // Filters
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = search,
                            onValueChange = onSearchChange,
                            singleLine = true,
                            placeholder = {
                                Text(
                                    text = "Filtrer les plats ou ingrédients…",
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = null,
                                    tint = Stone400,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Cream50,
                                unfocusedContainerColor = Cream50,
                                focusedBorderColor = Terracotta500,
                                unfocusedBorderColor = Cream200
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("modal_meal_search_input")
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (favoritesOnly) Amber400 else Amber50,
                            border = BorderStroke(1.dp, if (favoritesOnly) Amber400 else Amber200),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(onClick = onToggleFavoritesOnly)
                                .testTag("modal_meal_favorites_toggle")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (favoritesOnly) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                    contentDescription = null,
                                    tint = if (favoritesOnly) Stone900 else Amber800,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Favoris",
                                    color = if (favoritesOnly) Stone900 else Amber800,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "all" to "Tous appareils",
                            "Cookeo" to "🥘 Cookeo",
                            "Thermomix TM6" to "🌪️ TM6",
                            "Airfryer" to "🍟 Airfryer",
                            "Poêle & Casserole" to "🍳 Poêle"
                        ).forEach { (key, label) ->
                            val active = applianceFilter == key
                            Surface(
                                shape = RoundedCornerShape(9.dp),
                                color = if (active) Terracotta500 else Cream100,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9.dp))
                                    .clickable { onApplianceFilterChange(key) }
                            ) {
                                Text(
                                    text = label,
                                    color = if (active) PureWhite else Stone700,
                                    fontSize = 12.sp,
                                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "all" to "Tous styles",
                            "Traditionnel" to "🇫🇷 Traditionnel",
                            "Convivial" to "🍕 Convivial",
                            "fast" to "⚡ ≤ 15 min"
                        ).forEach { (key, label) ->
                            val active = categoryFilter == key
                            Surface(
                                shape = RoundedCornerShape(9.dp),
                                color = if (active) Stone800 else Cream100,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9.dp))
                                    .clickable { onCategoryFilterChange(key) }
                            ) {
                                Text(
                                    text = label,
                                    color = if (active) PureWhite else Stone700,
                                    fontSize = 12.sp,
                                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Cream200))

                // Options List
                if (filteredRecipes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (favoritesOnly) "Aucun favori ne correspond à cette recherche." else "Aucune recette ne correspond à ces filtres.",
                            color = Stone500,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = filteredRecipes, key = { it.id }) { recipe ->
                            SelectMealOptionRow(
                                recipe = recipe,
                                onToggleFavorite = { onToggleFavorite(recipe.id) },
                                onSelect = { onSelectRecipe(recipe.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SelectMealOptionRow(
    recipe: Recipe,
    onToggleFavorite: () -> Unit,
    onSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (recipe.isFavorite) Amber50.copy(alpha = 0.35f) else PureWhite,
        border = BorderStroke(1.dp, if (recipe.isFavorite) Amber200 else Cream200),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onSelect)
            .testTag("select_meal_option_${recipe.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FavoriteStarButton(
                isFavorite = recipe.isFavorite,
                onClick = onToggleFavorite,
                testTag = "modal_fav_${recipe.id}",
                iconSize = 18
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = recipe.name,
                    color = Stone900,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ApplianceBadge(appliance = recipe.appliance)
                    MomentBadge(moment = recipe.moment)
                    Text(
                        text = "${recipe.category} • ⏱ ${recipe.time} min",
                        color = Terracotta700,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(9.dp),
                color = PureWhite,
                border = BorderStroke(1.dp, Cream200)
            ) {
                Text(
                    text = "Choisir",
                    color = Stone700,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
