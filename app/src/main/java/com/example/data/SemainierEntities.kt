package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Ingredient
import com.example.model.ManualGroceryItem
import com.example.model.MealSlot
import com.example.model.Recipe
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val appliance: String,
    val category: String,
    val moment: String,
    val isFish: Boolean,
    val time: Int,
    val notes: String,
    val ingredientsJson: String,
    val isFavorite: Boolean,
    val isCustom: Boolean,
    val sortOrder: Int
)

@Entity(tableName = "meal_slots")
data class MealSlotEntity(
    @PrimaryKey val slotKey: String,
    val dayKey: String,
    val mealKey: String,
    val recipeId: String?,
    val servings: Int,
    val disabled: Boolean,
    val sortOrder: Int
)

@Entity(tableName = "grocery_checks")
data class GroceryCheckEntity(
    @PrimaryKey val itemKey: String,
    val isChecked: Boolean
)

@Entity(tableName = "manual_groceries")
data class ManualGroceryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val qty: String,
    val aisle: String,
    val createdAt: Long
)

object EntityMappers {
    fun ingredientsToJson(ingredients: List<Ingredient>): String {
        val arr = JSONArray()
        ingredients.forEach { ing ->
            arr.put(
                JSONObject().apply {
                    put("name", ing.name)
                    put("qty", ing.qty)
                    put("unit", ing.unit)
                    put("aisle", ing.aisle)
                }
            )
        }
        return arr.toString()
    }

    fun jsonToIngredients(json: String): List<Ingredient> {
        return try {
            val arr = JSONArray(json)
            List(arr.length()) { index ->
                val obj = arr.getJSONObject(index)
                Ingredient(
                    name = obj.optString("name", ""),
                    qty = obj.optDouble("qty", 1.0),
                    unit = obj.optString("unit", ""),
                    aisle = obj.optString("aisle", "Épicerie")
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun RecipeEntity.toDomain(): Recipe = Recipe(
        id = id,
        name = name,
        appliance = appliance,
        category = category,
        moment = moment,
        isFish = isFish,
        time = time,
        notes = notes,
        ingredients = jsonToIngredients(ingredientsJson),
        isFavorite = isFavorite,
        isCustom = isCustom
    )

    fun Recipe.toEntity(sortOrder: Int): RecipeEntity = RecipeEntity(
        id = id,
        name = name,
        appliance = appliance,
        category = category,
        moment = moment,
        isFish = isFish,
        time = time,
        notes = notes,
        ingredientsJson = ingredientsToJson(ingredients),
        isFavorite = isFavorite,
        isCustom = isCustom,
        sortOrder = sortOrder
    )

    fun MealSlotEntity.toDomain(): MealSlot = MealSlot(
        slotKey = slotKey,
        dayKey = dayKey,
        mealKey = mealKey,
        recipeId = recipeId,
        servings = servings,
        disabled = disabled
    )

    fun MealSlot.toEntity(sortOrder: Int): MealSlotEntity = MealSlotEntity(
        slotKey = slotKey,
        dayKey = dayKey,
        mealKey = mealKey,
        recipeId = recipeId,
        servings = servings,
        disabled = disabled,
        sortOrder = sortOrder
    )

    fun ManualGroceryEntity.toDomain(): ManualGroceryItem = ManualGroceryItem(
        id = id,
        name = name,
        qty = qty,
        aisle = aisle
    )

    fun ManualGroceryItem.toEntity(createdAt: Long = System.currentTimeMillis()): ManualGroceryEntity =
        ManualGroceryEntity(
            id = id,
            name = name,
            qty = qty,
            aisle = aisle,
            createdAt = createdAt
        )
}
