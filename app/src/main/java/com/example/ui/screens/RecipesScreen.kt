package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.R
import com.example.model.Recipe
import com.example.ui.SemainierUiState
import com.example.ui.components.ApplianceBadge
import com.example.ui.components.CategoryBadge
import com.example.ui.components.FavoriteStarButton
import com.example.ui.components.TimeBadge
import com.example.ui.theme.Amber50
import com.example.ui.theme.Amber200
import com.example.ui.theme.Amber300
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber800
import com.example.ui.theme.Cream50
import com.example.ui.theme.Cream100
import com.example.ui.theme.Cream200
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Sage600
import com.example.ui.theme.Stone400
import com.example.ui.theme.Stone500
import com.example.ui.theme.Stone700
import com.example.ui.theme.Stone800
import com.example.ui.theme.Stone900
import com.example.ui.theme.Terracotta500
import com.example.ui.theme.Terracotta600

@Composable
fun RecipesScreen(
    state: SemainierUiState,
    onSearchChange: (String) -> Unit,
    onSetApplianceFilter: (String) -> Unit,
    onSetCategoryFilter: (String) -> Unit,
    onToggleFavoritesOnly: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenAddRecipe: () -> Unit,
    onOpenRecipeDetail: (String, Int) -> Unit,
    onDeleteCustomRecipe: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredRecipes = remember(
        state.recipes,
        state.recipeSearch,
        state.recipeApplianceFilter,
        state.recipeCategoryFilter,
        state.recipeFavoritesOnly
    ) {
        val search = state.recipeSearch.lowercase().trim()
        state.recipes.filter { r ->
            if (state.recipeFavoritesOnly && !r.isFavorite) return@filter false
            val matchSearch = search.isEmpty() ||
                r.name.lowercase().contains(search) ||
                r.ingredients.any { it.name.lowercase().contains(search) }
            if (!matchSearch) return@filter false
            if (state.recipeApplianceFilter != "all" && r.appliance != state.recipeApplianceFilter) {
                return@filter false
            }
            when (state.recipeCategoryFilter) {
                "all" -> true
                "fast" -> r.time <= 15
                else -> r.category == state.recipeCategoryFilter
            }
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val columns = when {
            maxWidth >= 1000.dp -> 3
            maxWidth >= 660.dp -> 2
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
                RecipesHeaderCard(onOpenAddRecipe = onOpenAddRecipe)
            }

            item(span = { GridItemSpan(columns) }) {
                RecipesFilterCard(
                    search = state.recipeSearch,
                    favoritesOnly = state.recipeFavoritesOnly,
                    favoriteCount = state.favoriteCount,
                    applianceFilter = state.recipeApplianceFilter,
                    categoryFilter = state.recipeCategoryFilter,
                    onSearchChange = onSearchChange,
                    onToggleFavoritesOnly = onToggleFavoritesOnly,
                    onSetApplianceFilter = onSetApplianceFilter,
                    onSetCategoryFilter = onSetCategoryFilter
                )
            }

            if (filteredRecipes.isEmpty()) {
                item(span = { GridItemSpan(columns) }) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = PureWhite,
                        border = BorderStroke(1.dp, Cream200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (state.recipeFavoritesOnly) {
                                "Aucune recette favorite ne correspond à cette recherche. Touchez ☆ sur vos plats préférés pour les ajouter !"
                            } else {
                                "Aucune recette trouvée pour ces filtres."
                            },
                            color = Stone500,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }
            } else {
                items(
                    items = filteredRecipes,
                    key = { it.id }
                ) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onToggleFavorite = { onToggleFavorite(recipe.id) },
                        onOpenDetail = { onOpenRecipeDetail(recipe.id, 3) },
                        onDelete = { onDeleteCustomRecipe(recipe.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipesHeaderCard(onOpenAddRecipe: () -> Unit) {
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
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.recipes_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Stone900
                )
                Text(
                    text = stringResource(R.string.recipes_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Stone500
                )
            }
            Button(
                onClick = onOpenAddRecipe,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Sage600,
                    contentColor = PureWhite
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                modifier = Modifier.testTag("add_recipe_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.recipes_add_button),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun RecipesFilterCard(
    search: String,
    favoritesOnly: Boolean,
    favoriteCount: Int,
    applianceFilter: String,
    categoryFilter: String,
    onSearchChange: (String) -> Unit,
    onToggleFavoritesOnly: () -> Unit,
    onSetApplianceFilter: (String) -> Unit,
    onSetCategoryFilter: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, Cream200),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            text = stringResource(R.string.recipes_search_hint),
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
                        .testTag("recipes_search_input")
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (favoritesOnly) Amber400 else Amber50,
                    border = BorderStroke(1.dp, if (favoritesOnly) Amber400 else Amber200),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onToggleFavoritesOnly)
                        .testTag("recipes_favorites_filter")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = if (favoritesOnly) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = if (favoritesOnly) Stone900 else Amber800,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Favoris ($favoriteCount)",
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
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "APPAREIL :",
                    color = Stone500,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                listOf(
                    "all" to "Tous",
                    "Cookeo" to "🥘 Cookeo",
                    "Thermomix TM6" to "🌪️ Thermomix TM6",
                    "Airfryer" to "🍟 Airfryer",
                    "Poêle & Casserole" to "🍳 Poêle & Casserole"
                ).forEach { (key, label) ->
                    val active = applianceFilter == key
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (active) Terracotta500 else Cream50,
                        border = if (active) null else BorderStroke(1.dp, Cream200),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSetApplianceFilter(key) }
                            .testTag("recipe_app_filter_$key")
                    ) {
                        Text(
                            text = label,
                            color = if (active) PureWhite else Stone700,
                            fontSize = 12.sp,
                            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Cream100)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STYLE :",
                    color = Stone500,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                listOf(
                    "all" to "Tous",
                    "Traditionnel" to "🇫🇷 Traditionnel",
                    "Convivial" to "🍕 Convivial",
                    "fast" to "⚡ Express (≤ 15 min)"
                ).forEach { (key, label) ->
                    val active = categoryFilter == key
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (active) Stone800 else Cream50,
                        border = if (active) null else BorderStroke(1.dp, Cream200),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSetCategoryFilter(key) }
                            .testTag("recipe_cat_filter_$key")
                    ) {
                        Text(
                            text = label,
                            color = if (active) PureWhite else Stone700,
                            fontSize = 12.sp,
                            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecipeCard(
    recipe: Recipe,
    onToggleFavorite: () -> Unit,
    onOpenDetail: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, if (recipe.isFavorite) Amber300 else Cream200),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onOpenDetail)
            .testTag("recipe_card_${recipe.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    ApplianceBadge(appliance = recipe.appliance)
                    CategoryBadge(category = recipe.category)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    TimeBadge(minutes = recipe.time)
                    FavoriteStarButton(
                        isFavorite = recipe.isFavorite,
                        onClick = onToggleFavorite,
                        testTag = "recipe_fav_${recipe.id}"
                    )
                    if (recipe.isCustom || recipe.id.startsWith("custom-")) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("delete_recipe_${recipe.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteOutline,
                                contentDescription = stringResource(R.string.cd_delete_recipe),
                                tint = Stone400,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = recipe.name,
                style = MaterialTheme.typography.headlineSmall,
                color = Stone900
            )

            Text(
                text = recipe.notes.ifBlank { "Recette familiale simple et rapide." },
                style = MaterialTheme.typography.bodySmall,
                color = Stone500,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Cream200)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${recipe.ingredients.size} ingrédients",
                    color = Stone500,
                    fontSize = 12.sp
                )
                Text(
                    text = "Voir la recette & quantités →",
                    color = Terracotta600,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
