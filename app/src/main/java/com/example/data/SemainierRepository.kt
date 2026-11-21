package com.example.data

import com.example.data.EntityMappers.toDomain
import com.example.data.EntityMappers.toEntity
import com.example.model.AggregatedGroceryItem
import com.example.model.DefaultCatalog
import com.example.model.ManualGroceryItem
import com.example.model.MealSlot
import com.example.model.Recipe
import com.example.model.SemainierConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.Collator
import java.util.Locale

class SemainierRepository(private val dao: SemainierDao) {

    val recipesFlow: Flow<List<Recipe>> = dao.observeRecipes()
        .map { list -> list.map { it.toDomain() } }

    val mealSlotsFlow: Flow<List<MealSlot>> = dao.observeMealSlots()
        .map { list -> list.map { it.toDomain() } }

    val checkedKeysFlow: Flow<Set<String>> = dao.observeGroceryChecks()
        .map { list -> list.filter { it.isChecked }.map { it.itemKey }.toSet() }

    val manualGroceriesFlow: Flow<List<ManualGroceryItem>> = dao.observeManualGroceries()
        .map { list -> list.map { it.toDomain() } }

    suspend fun ensureSeeded() {
        val recipeCount = dao.getRecipeCount()
        if (recipeCount == 0) {
            val defaultEntities = DefaultCatalog.DEFAULT_RECIPES.mapIndexed { index, recipe ->
                recipe.toEntity(sortOrder = index)
            }
            dao.upsertRecipes(defaultEntities)
        }

        val slotCount = dao.getMealSlotCount()
        if (slotCount == 0) {
            val emptyPlan = DefaultCatalog.buildDefaultEmptyPlan()
            val slotEntities = emptyPlan.mapIndexed { index, slot ->
                slot.toEntity(sortOrder = index)
            }
            dao.upsertMealSlots(slotEntities)
            generateFullWeek(applianceFilter = "all", styleFilter = "all", favoritesOnly = false)
        }
    }

    suspend fun toggleFavorite(recipeId: String): Pair<Boolean, String>? {
        val current = dao.getRecipesOnce().find { it.id == recipeId } ?: return null
        val newFav = !current.isFavorite
        dao.updateRecipeFavorite(recipeId, newFav)
        return newFav to current.name
    }

    suspend fun addCustomRecipe(recipe: Recipe) {
        val minOrder = dao.getMinRecipeSortOrder() ?: 0
        dao.upsertRecipe(recipe.toEntity(sortOrder = minOrder - 1))
    }

    suspend fun deleteCustomRecipe(recipeId: String) {
        dao.clearRecipeFromSlots(recipeId)
        dao.deleteRecipeById(recipeId)
    }

    suspend fun generateFullWeek(
        applianceFilter: String,
        styleFilter: String,
        favoritesOnly: Boolean
    ) {
        val recipes = dao.getRecipesOnce().map { it.toDomain() }
        if (recipes.isEmpty()) return

        val existingSlots = dao.getMealSlotsOnce().map { it.toDomain() }.associateBy { it.slotKey }
        val usedRecipeIds = mutableSetOf<String>()
        var lastAppliance: String? = null
        var fishMealsCount = 0

        val updatedSlots = mutableListOf<MealSlot>()

        SemainierConfig.DAYS.forEach { day ->
            SemainierConfig.MEAL_TYPES.forEach { meal ->
                val slotKey = "${day.key}_${meal.key}"
                val existingSlot = existingSlots[slotKey] ?: MealSlot(
                    slotKey = slotKey,
                    dayKey = day.key,
                    mealKey = meal.key,
                    recipeId = null,
                    servings = day.defaultServings,
                    disabled = SemainierConfig.DEFAULT_DISABLED_SLOTS.contains(slotKey)
                )

                if (existingSlot.disabled) {
                    updatedSlots.add(existingSlot)
                    return@forEach
                }

                var pool = getFilteredPool(recipes, styleFilter, applianceFilter, favoritesOnly, meal.key)
                if (pool.isEmpty() && favoritesOnly) {
                    pool = getFilteredPool(recipes, "all", applianceFilter, true, meal.key)
                }
                if (pool.isEmpty()) {
                    pool = getFilteredPool(recipes, styleFilter, applianceFilter, false, meal.key)
                }
                if (pool.isEmpty()) {
                    pool = getFilteredPool(recipes, "all", "all", false, meal.key)
                }
                if (pool.isEmpty()) {
                    pool = recipes
                }

                if (fishMealsCount >= 1) {
                    val nonFish = pool.filter { !SemainierConfig.isFishRecipe(it) }
                    if (nonFish.isNotEmpty()) pool = nonFish
                }

                var unusedPool = pool.filter { !usedRecipeIds.contains(it.id) }
                if (unusedPool.isEmpty()) unusedPool = pool

                var variedPool = if (applianceFilter == "all" && unusedPool.size > 1) {
                    unusedPool.filter { it.appliance != lastAppliance }
                } else {
                    unusedPool
                }
                if (variedPool.isEmpty()) variedPool = unusedPool

                val chosen = variedPool.random()
                usedRecipeIds.add(chosen.id)
                lastAppliance = chosen.appliance
                if (SemainierConfig.isFishRecipe(chosen)) {
                    fishMealsCount++
                }

                updatedSlots.add(
                    existingSlot.copy(
                        recipeId = chosen.id,
                        servings = existingSlot.servings.coerceIn(1, 12),
                        disabled = false
                    )
                )
            }
        }

        dao.upsertMealSlots(updatedSlots.mapIndexed { idx, slot -> slot.toEntity(idx) })
    }

    suspend fun regenerateSingleSlot(
        slotKey: String,
        applianceFilter: String,
        styleFilter: String,
        favoritesOnly: Boolean
    ): String? {
        val recipes = dao.getRecipesOnce().map { it.toDomain() }
        if (recipes.isEmpty()) return null
        val slots = dao.getMealSlotsOnce().map { it.toDomain() }
        val targetIndex = slots.indexOfFirst { it.slotKey == slotKey }
        if (targetIndex == -1) return null
        val targetSlot = slots[targetIndex]

        val otherSlotsHaveFish = slots.any { slot ->
            if (slot.slotKey == slotKey || slot.disabled || slot.recipeId == null) return@any false
            val rec = recipes.find { it.id == slot.recipeId }
            SemainierConfig.isFishRecipe(rec)
        }

        var pool = getFilteredPool(recipes, styleFilter, applianceFilter, favoritesOnly, targetSlot.mealKey)
        if (pool.isEmpty()) pool = getFilteredPool(recipes, styleFilter, applianceFilter, false, targetSlot.mealKey)
        if (pool.isEmpty()) pool = getFilteredPool(recipes, "all", "all", false, targetSlot.mealKey)
        if (pool.isEmpty()) pool = recipes

        if (otherSlotsHaveFish) {
            val nonFish = pool.filter { !SemainierConfig.isFishRecipe(it) }
            if (nonFish.isNotEmpty()) pool = nonFish
        }

        val candidates = pool.filter { it.id != targetSlot.recipeId }
        val chosenPool = candidates.ifEmpty { pool }
        val chosen = chosenPool.random()

        dao.upsertMealSlot(targetSlot.copy(recipeId = chosen.id, disabled = false).toEntity(targetIndex))
        return chosen.name
    }

    suspend fun assignRecipeToSlot(slotKey: String, recipeId: String) {
        val slots = dao.getMealSlotsOnce().map { it.toDomain() }
        val idx = slots.indexOfFirst { it.slotKey == slotKey }
        if (idx == -1) return
        val updated = slots[idx].copy(recipeId = recipeId, disabled = false)
        dao.upsertMealSlot(updated.toEntity(idx))
    }

    suspend fun toggleSlotDisabled(slotKey: String): Boolean? {
        val slots = dao.getMealSlotsOnce().map { it.toDomain() }
        val idx = slots.indexOfFirst { it.slotKey == slotKey }
        if (idx == -1) return null
        val current = slots[idx]
        val newDisabled = !current.disabled
        val updated = if (newDisabled) {
            current.copy(disabled = true, recipeId = null)
        } else {
            current.copy(disabled = false)
        }
        dao.upsertMealSlot(updated.toEntity(idx))
        return newDisabled
    }

    suspend fun updateSlotServings(slotKey: String, delta: Int) {
        val slots = dao.getMealSlotsOnce().map { it.toDomain() }
        val idx = slots.indexOfFirst { it.slotKey == slotKey }
        if (idx == -1) return
        val current = slots[idx]
        val newServings = (current.servings + delta).coerceIn(1, 12)
        dao.upsertMealSlot(current.copy(servings = newServings).toEntity(idx))
    }

    suspend fun removeSlotRecipe(slotKey: String) {
        val slots = dao.getMealSlotsOnce().map { it.toDomain() }
        val idx = slots.indexOfFirst { it.slotKey == slotKey }
        if (idx == -1) return
        dao.upsertMealSlot(slots[idx].copy(recipeId = null).toEntity(idx))
    }

    suspend fun swapOrMoveSlots(sourceKey: String, targetKey: String): String? {
        if (sourceKey == targetKey) return null
        val slots = dao.getMealSlotsOnce().map { it.toDomain() }
        val srcIdx = slots.indexOfFirst { it.slotKey == sourceKey }
        val tgtIdx = slots.indexOfFirst { it.slotKey == targetKey }
        if (srcIdx == -1 || tgtIdx == -1) return null

        val src = slots[srcIdx]
        val tgt = slots[tgtIdx]
        if (src.recipeId == null && tgt.recipeId == null) return null

        val (newSrc, newTgt, message) = when {
            src.recipeId != null && tgt.recipeId != null -> Triple(
                src.copy(recipeId = tgt.recipeId, disabled = false),
                tgt.copy(recipeId = src.recipeId, disabled = false),
                "Repas échangés !"
            )
            src.recipeId != null && tgt.recipeId == null -> Triple(
                src.copy(recipeId = null),
                tgt.copy(recipeId = src.recipeId, disabled = false),
                "Repas déplacé !"
            )
            else -> Triple(
                src.copy(recipeId = tgt.recipeId, disabled = false),
                tgt.copy(recipeId = null),
                "Repas déplacé !"
            )
        }

        dao.upsertMealSlots(listOf(newSrc.toEntity(srcIdx), newTgt.toEntity(tgtIdx)))
        return message
    }

    suspend fun resetDefaultWeek() {
        val emptyPlan = DefaultCatalog.buildDefaultEmptyPlan()
        dao.upsertMealSlots(emptyPlan.mapIndexed { idx, slot -> slot.toEntity(idx) })
    }

    suspend fun toggleGroceryCheck(itemKey: String, currentlyChecked: Boolean) {
        if (currentlyChecked) {
            dao.deleteGroceryCheck(itemKey)
        } else {
            dao.upsertGroceryCheck(GroceryCheckEntity(itemKey, true))
        }
    }

    suspend fun uncheckAllGroceries() {
        dao.clearAllGroceryChecks()
    }

    suspend fun addManualGroceryItem(name: String, qty: String, aisle: String) {
        val item = ManualGroceryItem(
            id = System.currentTimeMillis().toString(),
            name = name.trim(),
            qty = qty.trim(),
            aisle = aisle
        )
        dao.upsertManualGrocery(item.toEntity())
    }

    suspend fun removeManualGroceryItem(manualId: String) {
        dao.deleteManualGrocery(manualId)
        dao.deleteGroceryCheck("manual_$manualId")
    }

    suspend fun exportTransferCode(): String {
        val recipes = dao.getRecipesOnce().map { it.toDomain() }
        val slots = dao.getMealSlotsOnce().map { it.toDomain() }
        val checks = dao.getGroceryChecksOnce().filter { it.isChecked }.map { it.itemKey }.toSet()
        val manual = dao.getManualGroceriesOnce().map { it.toDomain() }
        return TransferPayloadCodec.encodeMenuPayload(recipes, slots, checks, manual)
    }

    suspend fun importTransferCode(rawInput: String): Boolean {
        val decoded = TransferPayloadCodec.decodeMenuPayload(rawInput) ?: return false

        val existingEntities = dao.getRecipesOnce()
        val existingIds = existingEntities.map { it.id }.toMutableSet()
        val minOrderInitial = existingEntities.minOfOrNull { it.sortOrder } ?: 0
        var nextCustomOrder = minOrderInitial - 1

        val mergedRecipes = existingEntities.toMutableList()
        decoded.customRecipes.forEach { cr ->
            if (!existingIds.contains(cr.id)) {
                mergedRecipes.add(0, cr.toEntity(sortOrder = nextCustomOrder--))
                existingIds.add(cr.id)
            }
        }

        val finalRecipes = if (decoded.favoriteIds != null) {
            mergedRecipes.map { entity ->
                entity.copy(isFavorite = decoded.favoriteIds.contains(entity.id))
            }
        } else {
            mergedRecipes
        }

        val slotEntities = decoded.slots.mapIndexed { idx, slot -> slot.toEntity(idx) }
        val checkEntities = (decoded.checkedKeys ?: emptySet()).map { GroceryCheckEntity(it, true) }
        val manualEntities = (decoded.manualItems ?: emptyList()).mapIndexed { idx, item ->
            item.toEntity(createdAt = System.currentTimeMillis() + idx)
        }

        dao.applyImportedState(
            recipes = finalRecipes,
            slots = slotEntities,
            checks = checkEntities,
            manualGroceries = manualEntities
        )
        return true
    }

    private fun getFilteredPool(
        recipes: List<Recipe>,
        styleFilter: String,
        applianceFilter: String,
        favoritesOnly: Boolean,
        mealKey: String?
    ): List<Recipe> {
        return recipes.filter { r ->
            if (mealKey != null && !SemainierConfig.isRecipeSuitableForMeal(r, mealKey)) return@filter false
            if (favoritesOnly && !r.isFavorite) return@filter false
            val matchApp = applianceFilter == "all" || r.appliance == applianceFilter
            if (!matchApp) return@filter false
            when (styleFilter) {
                "all" -> true
                "fast" -> r.time <= 15
                else -> r.category == styleFilter
            }
        }
    }

    companion object {
        fun buildAggregatedGroceries(
            recipes: List<Recipe>,
            slots: List<MealSlot>,
            checkedKeys: Set<String>,
            manualItems: List<ManualGroceryItem>
        ): List<AggregatedGroceryItem> {
            val recipeMap = recipes.associateBy { it.id }
            val aggregated = linkedMapOf<String, AggregatedGroceryItem>()

            slots.forEach { slot ->
                if (slot.disabled || slot.recipeId == null) return@forEach
                val recipe = recipeMap[slot.recipeId] ?: return@forEach
                val servings = slot.servings
                recipe.ingredients.forEach { ing ->
                    val aisle = ing.aisle.ifBlank { "Épicerie" }
                    val normKey = "$aisle|${ing.name.lowercase(Locale.FRENCH).trim()}|${ing.unit.lowercase(Locale.FRENCH).trim()}"
                    val existing = aggregated[normKey]
                    if (existing != null) {
                        aggregated[normKey] = existing.copy(qty = existing.qty + ing.qty * servings)
                    } else {
                        aggregated[normKey] = AggregatedGroceryItem(
                            key = normKey,
                            name = ing.name,
                            qty = ing.qty * servings,
                            unit = ing.unit,
                            aisle = aisle,
                            isManual = false,
                            isChecked = checkedKeys.contains(normKey)
                        )
                    }
                }
            }

            manualItems.forEach { item ->
                val key = "manual_${item.id}"
                aggregated[key] = AggregatedGroceryItem(
                    key = key,
                    manualId = item.id,
                    name = item.name,
                    qtyText = item.qty,
                    aisle = item.aisle.ifBlank { "Épicerie" },
                    isManual = true,
                    isChecked = checkedKeys.contains(key)
                )
            }

            val frCollator = Collator.getInstance(Locale.FRENCH).apply {
                strength = Collator.PRIMARY
            }
            return aggregated.values.sortedWith { a, b -> frCollator.compare(a.name, b.name) }
        }
    }
}
