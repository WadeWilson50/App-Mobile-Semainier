package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.SemainierDatabase
import com.example.data.SemainierRepository
import com.example.data.TransferPayloadCodec
import com.example.model.AggregatedGroceryItem
import com.example.model.Ingredient
import com.example.model.MealSlot
import com.example.model.Recipe
import com.example.model.SemainierConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab(val route: String, val label: String) {
    PLANNER("planner", "Planning"),
    RECIPES("recipes", "Recettes"),
    GROCERIES("groceries", "Courses")
}

data class ToastEvent(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val isInfo: Boolean = false
)

private data class FilterAndModalState(
    val genAppliance: String = "all",
    val genStyle: String = "all",
    val genFavoritesOnly: Boolean = false,
    val swapSourceSlotKey: String? = null,
    val recipeSearch: String = "",
    val recipeApplianceFilter: String = "all",
    val recipeCategoryFilter: String = "all",
    val recipeFavoritesOnly: Boolean = false,
    val selectMealSlotKey: String? = null,
    val modalSearch: String = "",
    val modalApplianceFilter: String = "all",
    val modalCategoryFilter: String = "all",
    val modalFavoritesOnly: Boolean = false,
    val detailRecipeId: String? = null,
    val detailServings: Int = 3,
    val showAddRecipeDialog: Boolean = false,
    val showSyncDialog: Boolean = false,
    val currentTransferCode: String = "",
    val toast: ToastEvent? = null
)

data class SemainierUiState(
    val recipes: List<Recipe> = emptyList(),
    val slots: List<MealSlot> = emptyList(),
    val groceries: List<AggregatedGroceryItem> = emptyList(),
    val uncheckedGroceryCount: Int = 0,
    val favoriteCount: Int = 0,
    val genAppliance: String = "all",
    val genStyle: String = "all",
    val genFavoritesOnly: Boolean = false,
    val swapSourceSlotKey: String? = null,
    val recipeSearch: String = "",
    val recipeApplianceFilter: String = "all",
    val recipeCategoryFilter: String = "all",
    val recipeFavoritesOnly: Boolean = false,
    val selectMealSlotKey: String? = null,
    val modalSearch: String = "",
    val modalApplianceFilter: String = "all",
    val modalCategoryFilter: String = "all",
    val modalFavoritesOnly: Boolean = false,
    val detailRecipeId: String? = null,
    val detailServings: Int = 3,
    val showAddRecipeDialog: Boolean = false,
    val showSyncDialog: Boolean = false,
    val currentTransferCode: String = "",
    val toast: ToastEvent? = null
)

class SemainierViewModel(
    application: Application,
    private val repository: SemainierRepository
) : AndroidViewModel(application) {

    private val localUiState = MutableStateFlow(FilterAndModalState())

    val uiState: StateFlow<SemainierUiState> = combine(
        repository.recipesFlow,
        repository.mealSlotsFlow,
        repository.checkedKeysFlow,
        repository.manualGroceriesFlow,
        localUiState
    ) { recipes, slots, checkedKeys, manualItems, ui ->
        val groceries = SemainierRepository.buildAggregatedGroceries(
            recipes = recipes,
            slots = slots,
            checkedKeys = checkedKeys,
            manualItems = manualItems
        )
        SemainierUiState(
            recipes = recipes,
            slots = slots,
            groceries = groceries,
            uncheckedGroceryCount = groceries.count { !it.isChecked },
            favoriteCount = recipes.count { it.isFavorite },
            genAppliance = ui.genAppliance,
            genStyle = ui.genStyle,
            genFavoritesOnly = ui.genFavoritesOnly,
            swapSourceSlotKey = ui.swapSourceSlotKey,
            recipeSearch = ui.recipeSearch,
            recipeApplianceFilter = ui.recipeApplianceFilter,
            recipeCategoryFilter = ui.recipeCategoryFilter,
            recipeFavoritesOnly = ui.recipeFavoritesOnly,
            selectMealSlotKey = ui.selectMealSlotKey,
            modalSearch = ui.modalSearch,
            modalApplianceFilter = ui.modalApplianceFilter,
            modalCategoryFilter = ui.modalCategoryFilter,
            modalFavoritesOnly = ui.modalFavoritesOnly,
            detailRecipeId = ui.detailRecipeId,
            detailServings = ui.detailServings,
            showAddRecipeDialog = ui.showAddRecipeDialog,
            showSyncDialog = ui.showSyncDialog,
            currentTransferCode = ui.currentTransferCode,
            toast = ui.toast
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SemainierUiState()
    )

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    fun showToast(message: String, isInfo: Boolean = false) {
        localUiState.update {
            it.copy(toast = ToastEvent(id = System.nanoTime(), message = message, isInfo = isInfo))
        }
    }

    fun dismissToast() {
        localUiState.update { it.copy(toast = null) }
    }

    // Planner actions
    fun setGenAppliance(appliance: String) {
        localUiState.update { it.copy(genAppliance = appliance) }
    }

    fun setGenStyle(style: String) {
        localUiState.update { it.copy(genStyle = style) }
    }

    fun toggleGenFavoritesOnly() {
        localUiState.update { it.copy(genFavoritesOnly = !it.genFavoritesOnly) }
    }

    fun generateFullWeek() {
        val state = localUiState.value
        viewModelScope.launch {
            repository.generateFullWeek(
                applianceFilter = state.genAppliance,
                styleFilter = state.genStyle,
                favoritesOnly = state.genFavoritesOnly
            )
            localUiState.update { it.copy(swapSourceSlotKey = null) }
            showToast("Semaine générée (max 1 poisson, plats adaptés midi & soir) !")
        }
    }

    fun resetDefaultWeek() {
        viewModelScope.launch {
            repository.resetDefaultWeek()
            localUiState.update { it.copy(swapSourceSlotKey = null) }
            showToast("Semaine type réinitialisée (Lun, Mar et Jeu midi désactivés).")
        }
    }

    fun regenerateSingleSlot(slotKey: String) {
        val state = localUiState.value
        viewModelScope.launch {
            val name = repository.regenerateSingleSlot(
                slotKey = slotKey,
                applianceFilter = state.genAppliance,
                styleFilter = state.genStyle,
                favoritesOnly = state.genFavoritesOnly
            )
            if (name != null) {
                showToast("Remplacé par : $name")
            }
        }
    }

    fun toggleSlotDisabled(slotKey: String) {
        viewModelScope.launch {
            val nowDisabled = repository.toggleSlotDisabled(slotKey) ?: return@launch
            if (nowDisabled) {
                showToast("Créneau marqué comme libre (sans repas prévu).", isInfo = true)
            } else {
                showToast("Créneau réactivé ! Choisissez ou générez un plat.")
            }
        }
    }

    fun updateSlotServings(slotKey: String, delta: Int) {
        viewModelScope.launch {
            repository.updateSlotServings(slotKey, delta)
        }
    }

    fun removeSlotRecipe(slotKey: String) {
        viewModelScope.launch {
            repository.removeSlotRecipe(slotKey)
        }
    }

    fun handleSwapAction(slotKey: String) {
        val currentSource = localUiState.value.swapSourceSlotKey
        if (currentSource == null) {
            localUiState.update { it.copy(swapSourceSlotKey = slotKey) }
            showToast("Touchez un autre créneau pour échanger ou déplacer ce repas.", isInfo = true)
        } else if (currentSource == slotKey) {
            localUiState.update { it.copy(swapSourceSlotKey = null) }
        } else {
            viewModelScope.launch {
                val msg = repository.swapOrMoveSlots(currentSource, slotKey)
                localUiState.update { it.copy(swapSourceSlotKey = null) }
                if (msg != null) showToast(msg)
            }
        }
    }

    fun cancelSwapMode() {
        localUiState.update { it.copy(swapSourceSlotKey = null) }
    }

    // Select Meal Modal
    fun openSelectMealModal(slotKey: String) {
        localUiState.update {
            it.copy(
                selectMealSlotKey = slotKey,
                modalSearch = "",
                modalApplianceFilter = "all",
                modalCategoryFilter = "all",
                modalFavoritesOnly = false,
                swapSourceSlotKey = null
            )
        }
    }

    fun closeSelectMealModal() {
        localUiState.update { it.copy(selectMealSlotKey = null) }
    }

    fun setModalSearch(query: String) {
        localUiState.update { it.copy(modalSearch = query) }
    }

    fun setModalApplianceFilter(appliance: String) {
        localUiState.update { it.copy(modalApplianceFilter = appliance) }
    }

    fun setModalCategoryFilter(category: String) {
        localUiState.update { it.copy(modalCategoryFilter = category) }
    }

    fun toggleModalFavoritesOnly() {
        localUiState.update { it.copy(modalFavoritesOnly = !it.modalFavoritesOnly) }
    }

    fun assignRecipeToSelectedSlot(recipeId: String) {
        val slotKey = localUiState.value.selectMealSlotKey ?: return
        viewModelScope.launch {
            repository.assignRecipeToSlot(slotKey, recipeId)
            localUiState.update { it.copy(selectMealSlotKey = null) }
            showToast("Repas ajouté au planning !")
        }
    }

    // Recipe Detail Modal
    fun openRecipeDetail(recipeId: String, defaultServings: Int = 3) {
        localUiState.update {
            it.copy(detailRecipeId = recipeId, detailServings = defaultServings)
        }
    }

    fun closeRecipeDetail() {
        localUiState.update { it.copy(detailRecipeId = null) }
    }

    fun setDetailServings(servings: Int) {
        localUiState.update { it.copy(detailServings = servings) }
    }

    // Favorites & Recipe Book
    fun toggleFavorite(recipeId: String) {
        viewModelScope.launch {
            val result = repository.toggleFavorite(recipeId) ?: return@launch
            val (nowFav, name) = result
            if (nowFav) {
                showToast("⭐ Ajouté aux favoris : $name")
            } else {
                showToast("Retiré des favoris : $name", isInfo = true)
            }
        }
    }

    fun setRecipeSearch(query: String) {
        localUiState.update { it.copy(recipeSearch = query) }
    }

    fun setRecipeApplianceFilter(appliance: String) {
        localUiState.update { it.copy(recipeApplianceFilter = appliance) }
    }

    fun setRecipeCategoryFilter(category: String) {
        localUiState.update { it.copy(recipeCategoryFilter = category) }
    }

    fun toggleRecipeFavoritesOnly() {
        localUiState.update { it.copy(recipeFavoritesOnly = !it.recipeFavoritesOnly) }
    }

    fun openAddRecipeDialog() {
        localUiState.update { it.copy(showAddRecipeDialog = true) }
    }

    fun closeAddRecipeDialog() {
        localUiState.update { it.copy(showAddRecipeDialog = false) }
    }

    fun saveCustomRecipe(
        name: String,
        appliance: String,
        category: String,
        time: Int,
        moment: String,
        notes: String,
        ingredients: List<Ingredient>
    ) {
        val draft = Recipe(
            id = "custom-${System.currentTimeMillis()}",
            name = name.trim(),
            appliance = appliance,
            category = category,
            moment = moment.ifBlank { "any" },
            isFish = false,
            time = time.coerceIn(5, 180),
            notes = notes.trim(),
            ingredients = ingredients,
            isFavorite = false,
            isCustom = true
        )
        val finalRecipe = draft.copy(isFish = SemainierConfig.isFishRecipe(draft))
        viewModelScope.launch {
            repository.addCustomRecipe(finalRecipe)
            localUiState.update { it.copy(showAddRecipeDialog = false) }
            showToast("Recette \"${finalRecipe.name}\" ($appliance) ajoutée !")
        }
    }

    fun deleteCustomRecipe(recipeId: String) {
        viewModelScope.launch {
            repository.deleteCustomRecipe(recipeId)
            showToast("Recette supprimée.", isInfo = true)
        }
    }

    // Groceries
    fun toggleGroceryCheck(itemKey: String, currentlyChecked: Boolean) {
        viewModelScope.launch {
            repository.toggleGroceryCheck(itemKey, currentlyChecked)
        }
    }

    fun uncheckAllGroceries() {
        viewModelScope.launch {
            repository.uncheckAllGroceries()
            showToast("Tous les articles ont été décochés.")
        }
    }

    fun addManualGroceryItem(name: String, qty: String, aisle: String) {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) return
        viewModelScope.launch {
            repository.addManualGroceryItem(cleanName, qty, aisle)
            showToast("\"$cleanName\" ajouté à la liste.")
        }
    }

    fun removeManualGroceryItem(manualId: String) {
        viewModelScope.launch {
            repository.removeManualGroceryItem(manualId)
        }
    }

    fun buildFormattedGroceryList(): String {
        return TransferPayloadCodec.formatGroceryListText(uiState.value.groceries)
    }

    // Sync & Transfer Modal
    fun openSyncDialog() {
        viewModelScope.launch {
            val code = repository.exportTransferCode()
            localUiState.update {
                it.copy(showSyncDialog = true, currentTransferCode = code)
            }
        }
    }

    fun closeSyncDialog() {
        localUiState.update { it.copy(showSyncDialog = false) }
    }

    fun importTransferCode(rawInput: String) {
        viewModelScope.launch {
            val ok = repository.importTransferCode(rawInput)
            if (ok) {
                localUiState.update { it.copy(showSyncDialog = false) }
                showToast("Menu synchronisé avec succès sur cet appareil !")
            } else {
                showToast(
                    "Code invalide. Vérifiez d'avoir copié tout le code commençant par MENU-...",
                    isInfo = true
                )
            }
        }
    }

    suspend fun buildBackupJson(): String {
        val code = repository.exportTransferCode()
        return TransferPayloadCodec.buildBackupJson(code)
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = SemainierDatabase.getInstance(application)
            val repo = SemainierRepository(db.semainierDao())
            return SemainierViewModel(application, repo) as T
        }
    }
}
