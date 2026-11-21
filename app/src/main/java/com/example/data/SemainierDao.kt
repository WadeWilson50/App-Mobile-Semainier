package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SemainierDao {
    // Recipes
    @Query("SELECT * FROM recipes ORDER BY sortOrder ASC")
    fun observeRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes ORDER BY sortOrder ASC")
    suspend fun getRecipesOnce(): List<RecipeEntity>

    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun getRecipeCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecipes(recipes: List<RecipeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecipe(recipe: RecipeEntity)

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateRecipeFavorite(id: String, isFavorite: Boolean)

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteRecipeById(id: String)

    @Query("SELECT MIN(sortOrder) FROM recipes")
    suspend fun getMinRecipeSortOrder(): Int?

    // Meal Slots
    @Query("SELECT * FROM meal_slots ORDER BY sortOrder ASC")
    fun observeMealSlots(): Flow<List<MealSlotEntity>>

    @Query("SELECT * FROM meal_slots ORDER BY sortOrder ASC")
    suspend fun getMealSlotsOnce(): List<MealSlotEntity>

    @Query("SELECT COUNT(*) FROM meal_slots")
    suspend fun getMealSlotCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMealSlots(slots: List<MealSlotEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMealSlot(slot: MealSlotEntity)

    @Query("UPDATE meal_slots SET recipeId = NULL WHERE recipeId = :recipeId")
    suspend fun clearRecipeFromSlots(recipeId: String)

    // Grocery Checks
    @Query("SELECT * FROM grocery_checks")
    fun observeGroceryChecks(): Flow<List<GroceryCheckEntity>>

    @Query("SELECT * FROM grocery_checks")
    suspend fun getGroceryChecksOnce(): List<GroceryCheckEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGroceryCheck(check: GroceryCheckEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGroceryChecks(checks: List<GroceryCheckEntity>)

    @Query("DELETE FROM grocery_checks WHERE itemKey = :itemKey")
    suspend fun deleteGroceryCheck(itemKey: String)

    @Query("DELETE FROM grocery_checks")
    suspend fun clearAllGroceryChecks()

    // Manual Groceries
    @Query("SELECT * FROM manual_groceries ORDER BY createdAt ASC")
    fun observeManualGroceries(): Flow<List<ManualGroceryEntity>>

    @Query("SELECT * FROM manual_groceries ORDER BY createdAt ASC")
    suspend fun getManualGroceriesOnce(): List<ManualGroceryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertManualGrocery(item: ManualGroceryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertManualGroceries(items: List<ManualGroceryEntity>)

    @Query("DELETE FROM manual_groceries WHERE id = :id")
    suspend fun deleteManualGrocery(id: String)

    @Query("DELETE FROM manual_groceries")
    suspend fun clearManualGroceries()

    @Transaction
    suspend fun applyImportedState(
        recipes: List<RecipeEntity>,
        slots: List<MealSlotEntity>,
        checks: List<GroceryCheckEntity>,
        manualGroceries: List<ManualGroceryEntity>
    ) {
        upsertRecipes(recipes)
        upsertMealSlots(slots)
        clearAllGroceryChecks()
        if (checks.isNotEmpty()) upsertGroceryChecks(checks)
        clearManualGroceries()
        if (manualGroceries.isNotEmpty()) upsertManualGroceries(manualGroceries)
    }
}
