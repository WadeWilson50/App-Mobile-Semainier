package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppTab
import com.example.ui.SemainierViewModel
import com.example.ui.components.AddRecipeDialog
import com.example.ui.components.RecipeDetailDialog
import com.example.ui.components.SelectMealDialog
import com.example.ui.components.SyncDialog
import com.example.ui.components.ToastBanner
import com.example.ui.screens.GroceriesScreen
import com.example.ui.screens.PlannerScreen
import com.example.ui.screens.RecipesScreen
import com.example.ui.theme.Cream50
import com.example.ui.theme.Cream100
import com.example.ui.theme.Cream200
import com.example.ui.theme.Emerald500
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Sage50
import com.example.ui.theme.Sage200
import com.example.ui.theme.Sage500
import com.example.ui.theme.Sage700
import com.example.ui.theme.SemainierTheme
import com.example.ui.theme.Stone500
import com.example.ui.theme.Stone600
import com.example.ui.theme.Stone900
import com.example.ui.theme.Terracotta100
import com.example.ui.theme.Terracotta500
import com.example.ui.theme.Terracotta600
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SemainierTheme {
                SemainierApp()
            }
        }
    }
}

@Composable
fun SemainierApp() {
    val context = LocalContext.current
    val application = context.applicationContext as android.app.Application
    val viewModel: SemainierViewModel = viewModel(
        factory = SemainierViewModel.Factory(application)
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var activeTab by rememberSaveable { mutableStateOf(AppTab.PLANNER) }
    val coroutineScope = rememberCoroutineScope()

    // Handle back press on non-home tabs
    BackHandler(enabled = activeTab != AppTab.PLANNER) {
        activeTab = AppTab.PLANNER
    }

    // Auto-dismiss toast after 2.6 seconds
    LaunchedEffect(state.toast?.id) {
        if (state.toast != null) {
            delay(2600L)
            viewModel.dismissToast()
        }
    }

    // SAF JSON export launcher
    val exportBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val json = viewModel.buildBackupJson()
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        out.write(json.toByteArray(Charsets.UTF_8))
                    }
                    viewModel.showToast("Fichier de sauvegarde téléchargé !")
                } catch (_: Exception) {
                    viewModel.showToast("Impossible d'enregistrer le fichier.", isInfo = true)
                }
            }
        }
    }

    // SAF JSON import launcher
    val importBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val content = context.contentResolver.openInputStream(uri)?.use { input ->
                    input.bufferedReader(Charsets.UTF_8).readText()
                }
                if (!content.isNullOrBlank()) {
                    viewModel.importTransferCode(content)
                }
            } catch (_: Exception) {
                viewModel.showToast("Impossible de lire le fichier.", isInfo = true)
            }
        }
    }

    fun copyToClipboard(label: String, text: String, successMessage: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
        viewModel.showToast(successMessage)
    }

    fun sharePlainText(subject: String, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, subject))
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream50)
    ) {
        val useRail = maxWidth >= 720.dp

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = Cream50,
                contentWindowInsets = WindowInsets.safeDrawing,
                topBar = {
                    SemainierTopBar(
                        onOpenSync = { viewModel.openSyncDialog() }
                    )
                },
                bottomBar = {
                    if (!useRail) {
                        SemainierBottomBar(
                            activeTab = activeTab,
                            uncheckedCount = state.uncheckedGroceryCount,
                            onSelectTab = { activeTab = it }
                        )
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (useRail) {
                        SemainierNavRail(
                            activeTab = activeTab,
                            uncheckedCount = state.uncheckedGroceryCount,
                            onSelectTab = { activeTab = it }
                        )
                    }

                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        when (activeTab) {
                            AppTab.PLANNER -> PlannerScreen(
                                state = state,
                                onSetGenAppliance = viewModel::setGenAppliance,
                                onSetGenStyle = viewModel::setGenStyle,
                                onToggleGenFavoritesOnly = viewModel::toggleGenFavoritesOnly,
                                onGenerateWeek = viewModel::generateFullWeek,
                                onResetDefaultWeek = viewModel::resetDefaultWeek,
                                onToggleSlotDisabled = viewModel::toggleSlotDisabled,
                                onUpdateSlotServings = viewModel::updateSlotServings,
                                onOpenRecipeDetail = viewModel::openRecipeDetail,
                                onToggleFavorite = viewModel::toggleFavorite,
                                onOpenSelectMeal = viewModel::openSelectMealModal,
                                onRegenerateSlot = viewModel::regenerateSingleSlot,
                                onRemoveSlotRecipe = viewModel::removeSlotRecipe,
                                onSwapSlotAction = viewModel::handleSwapAction,
                                onCancelSwapMode = viewModel::cancelSwapMode
                            )

                            AppTab.RECIPES -> RecipesScreen(
                                state = state,
                                onSearchChange = viewModel::setRecipeSearch,
                                onSetApplianceFilter = viewModel::setRecipeApplianceFilter,
                                onSetCategoryFilter = viewModel::setRecipeCategoryFilter,
                                onToggleFavoritesOnly = viewModel::toggleRecipeFavoritesOnly,
                                onToggleFavorite = viewModel::toggleFavorite,
                                onOpenAddRecipe = viewModel::openAddRecipeDialog,
                                onOpenRecipeDetail = viewModel::openRecipeDetail,
                                onDeleteCustomRecipe = viewModel::deleteCustomRecipe
                            )

                            AppTab.GROCERIES -> GroceriesScreen(
                                state = state,
                                onToggleCheck = viewModel::toggleGroceryCheck,
                                onUncheckAll = viewModel::uncheckAllGroceries,
                                onCopyList = {
                                    val text = viewModel.buildFormattedGroceryList()
                                    if (text.isBlank()) {
                                        viewModel.showToast(
                                            "Aucun article à copier (tout est coché ou la liste est vide).",
                                            isInfo = true
                                        )
                                    } else {
                                        copyToClipboard(
                                            "Liste de courses",
                                            text,
                                            "Liste de courses copiée dans le presse-papiers !"
                                        )
                                    }
                                },
                                onShareList = {
                                    val text = viewModel.buildFormattedGroceryList()
                                    if (text.isBlank()) {
                                        viewModel.showToast(
                                            "Aucun article à partager (tout est coché ou la liste est vide).",
                                            isInfo = true
                                        )
                                    } else {
                                        sharePlainText("Ma liste de courses", text)
                                    }
                                },
                                onAddManualItem = viewModel::addManualGroceryItem,
                                onRemoveManualItem = viewModel::removeManualGroceryItem
                            )
                        }
                    }
                }
            }

            // Toast notification overlay above bottom navigation
            ToastBanner(
                toast = state.toast,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = if (useRail) 20.dp else 92.dp, start = 16.dp, end = 16.dp)
            )
        }
    }

    // Select Meal Modal
    state.selectMealSlotKey?.let { slotKey ->
        SelectMealDialog(
            slotKey = slotKey,
            slots = state.slots,
            recipes = state.recipes,
            search = state.modalSearch,
            applianceFilter = state.modalApplianceFilter,
            categoryFilter = state.modalCategoryFilter,
            favoritesOnly = state.modalFavoritesOnly,
            onSearchChange = viewModel::setModalSearch,
            onApplianceFilterChange = viewModel::setModalApplianceFilter,
            onCategoryFilterChange = viewModel::setModalCategoryFilter,
            onToggleFavoritesOnly = viewModel::toggleModalFavoritesOnly,
            onToggleFavorite = viewModel::toggleFavorite,
            onSelectRecipe = viewModel::assignRecipeToSelectedSlot,
            onDismiss = viewModel::closeSelectMealModal
        )
    }

    // Recipe Detail Modal
    state.detailRecipeId?.let { recipeId ->
        val recipe = state.recipes.find { it.id == recipeId }
        if (recipe != null) {
            RecipeDetailDialog(
                recipe = recipe,
                servings = state.detailServings,
                onServingsChange = viewModel::setDetailServings,
                onToggleFavorite = { viewModel.toggleFavorite(recipe.id) },
                onDismiss = viewModel::closeRecipeDetail
            )
        }
    }

    // Add Custom Recipe Modal
    if (state.showAddRecipeDialog) {
        AddRecipeDialog(
            onSave = viewModel::saveCustomRecipe,
            onDismiss = viewModel::closeAddRecipeDialog,
            onValidationMessage = { msg -> viewModel.showToast(msg, isInfo = true) }
        )
    }

    // Sync / Transfer Modal
    if (state.showSyncDialog) {
        SyncDialog(
            transferCode = state.currentTransferCode,
            onShareCode = { code ->
                sharePlainText(
                    "Mon Semainier Gourmand",
                    "Voici notre menu de la semaine et la liste de courses :\n\n$code"
                )
            },
            onCopyCode = { code ->
                copyToClipboard(
                    "Code Semainier",
                    code,
                    "Code du menu copié ! Collez-le sur votre autre appareil."
                )
            },
            onImportCode = viewModel::importTransferCode,
            onExportJsonBackup = {
                val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                exportBackupLauncher.launch("mon-semainier-$dateStr.json")
            },
            onImportJsonBackup = {
                importBackupLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
            },
            onDismiss = viewModel::closeSyncDialog
        )
    }
}

@Composable
private fun SemainierTopBar(
    onOpenSync: () -> Unit
) {
    Surface(
        color = PureWhite,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Terracotta500),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.MenuBook,
                        contentDescription = stringResource(R.string.cd_app_logo),
                        tint = PureWhite,
                        modifier = Modifier.size(23.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app_full_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 19.sp,
                        color = Stone900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Sage50,
                            border = BorderStroke(1.dp, Sage200),
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .clickable(onClick = onOpenSync)
                                .testTag("open_sync_dialog_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Emerald500)
                                )
                                Text(
                                    text = "📱 ${stringResource(R.string.sync_chip_label)}",
                                    color = Sage700,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Cream200)
                    .padding(top = 1.dp)
            )
        }
    }
}

@Composable
private fun SemainierBottomBar(
    activeTab: AppTab,
    uncheckedCount: Int,
    onSelectTab: (AppTab) -> Unit
) {
    Surface(
        color = PureWhite,
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, Cream200)
    ) {
        NavigationBar(
            containerColor = PureWhite,
            tonalElevation = 0.dp
        ) {
            AppTab.entries.forEach { tab ->
                val selected = tab == activeTab
                NavigationBarItem(
                    selected = selected,
                    onClick = { onSelectTab(tab) },
                    icon = {
                        TabIcon(tab = tab, selected = selected, uncheckedCount = uncheckedCount)
                    },
                    label = {
                        Text(
                            text = tab.label,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Terracotta600,
                        selectedTextColor = Terracotta600,
                        indicatorColor = Terracotta100,
                        unselectedIconColor = Stone600,
                        unselectedTextColor = Stone600
                    ),
                    modifier = Modifier.testTag("nav_${tab.route}")
                )
            }
        }
    }
}

@Composable
private fun SemainierNavRail(
    activeTab: AppTab,
    uncheckedCount: Int,
    onSelectTab: (AppTab) -> Unit
) {
    NavigationRail(
        containerColor = PureWhite,
        header = { Spacer(Modifier.size(8.dp)) }
    ) {
        AppTab.entries.forEach { tab ->
            val selected = tab == activeTab
            NavigationRailItem(
                selected = selected,
                onClick = { onSelectTab(tab) },
                icon = {
                    TabIcon(tab = tab, selected = selected, uncheckedCount = uncheckedCount)
                },
                label = {
                    Text(
                        text = tab.label,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Terracotta600,
                    selectedTextColor = Terracotta600,
                    indicatorColor = Terracotta100,
                    unselectedIconColor = Stone600,
                    unselectedTextColor = Stone600
                ),
                modifier = Modifier.testTag("nav_${tab.route}")
            )
        }
    }
}

@Composable
private fun TabIcon(
    tab: AppTab,
    selected: Boolean,
    uncheckedCount: Int
) {
    when (tab) {
        AppTab.PLANNER -> Icon(
            imageVector = if (selected) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
            contentDescription = tab.label
        )
        AppTab.RECIPES -> Icon(
            imageVector = if (selected) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
            contentDescription = tab.label
        )
        AppTab.GROCERIES -> BadgedBox(
            badge = {
                if (uncheckedCount > 0) {
                    Badge(
                        containerColor = Sage500,
                        contentColor = PureWhite
                    ) {
                        Text(
                            text = uncheckedCount.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = if (selected) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                contentDescription = tab.label
            )
        }
    }
}
