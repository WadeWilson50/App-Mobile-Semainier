package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.Amber50
import com.example.ui.theme.Amber200
import com.example.ui.theme.Amber800
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald200
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Orange50
import com.example.ui.theme.Orange200
import com.example.ui.theme.Orange800
import com.example.ui.theme.Rose50
import com.example.ui.theme.Rose200
import com.example.ui.theme.Rose800
import com.example.ui.theme.Sky50
import com.example.ui.theme.Sky200
import com.example.ui.theme.Sky800
import com.example.ui.theme.Stone100
import com.example.ui.theme.Stone200
import com.example.ui.theme.Stone800
import kotlin.math.abs
import kotlin.math.roundToInt

data class Ingredient(
    val name: String,
    val qty: Double,
    val unit: String,
    val aisle: String = "Épicerie"
)

data class Recipe(
    val id: String,
    val name: String,
    val appliance: String,
    val category: String,
    val moment: String = "any",
    val isFish: Boolean = false,
    val time: Int,
    val notes: String,
    val ingredients: List<Ingredient>,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false
)

data class DayConfig(
    val key: String,
    val label: String,
    val isWeekend: Boolean,
    val defaultServings: Int
)

data class MealTypeConfig(
    val key: String,
    val label: String,
    val icon: String
)

data class MealSlot(
    val slotKey: String,
    val dayKey: String,
    val mealKey: String,
    val recipeId: String?,
    val servings: Int,
    val disabled: Boolean
)

data class ManualGroceryItem(
    val id: String,
    val name: String,
    val qty: String,
    val aisle: String
)

data class AggregatedGroceryItem(
    val key: String,
    val manualId: String? = null,
    val name: String,
    val qty: Double = 0.0,
    val qtyText: String = "",
    val unit: String = "",
    val aisle: String,
    val isManual: Boolean,
    val isChecked: Boolean
)

data class BadgeStyleMeta(
    val name: String,
    val icon: String,
    val bgColor: Color,
    val textColor: Color,
    val borderColor: Color
)

object SemainierConfig {
    val DAYS = listOf(
        DayConfig("lundi", "Lundi", isWeekend = false, defaultServings = 3),
        DayConfig("mardi", "Mardi", isWeekend = false, defaultServings = 3),
        DayConfig("mercredi", "Mercredi", isWeekend = false, defaultServings = 3),
        DayConfig("jeudi", "Jeudi", isWeekend = false, defaultServings = 3),
        DayConfig("vendredi", "Vendredi", isWeekend = false, defaultServings = 3),
        DayConfig("samedi", "Samedi", isWeekend = true, defaultServings = 4),
        DayConfig("dimanche", "Dimanche", isWeekend = true, defaultServings = 4)
    )

    val MEAL_TYPES = listOf(
        MealTypeConfig("midi", "Midi", "☀️"),
        MealTypeConfig("soir", "Soir", "🌙")
    )

    val DEFAULT_DISABLED_SLOTS = setOf("lundi_midi", "mardi_midi", "jeudi_midi")

    val DEFAULT_FAVORITE_IDS = setOf("af-1", "ck-1", "tm-1", "pc-2")

    val APPLIANCES = listOf(
        "Cookeo",
        "Thermomix TM6",
        "Airfryer",
        "Poêle & Casserole"
    )

    val CATEGORIES = listOf("Traditionnel", "Convivial")

    val APPLIANCES_META = mapOf(
        "Cookeo" to BadgeStyleMeta("Cookeo", "🥘", Sky50, Sky800, Sky200),
        "Thermomix TM6" to BadgeStyleMeta("Thermomix TM6", "🌪️", Emerald50, Emerald800, Emerald200),
        "Airfryer" to BadgeStyleMeta("Airfryer", "🍟", Orange50, Orange800, Orange200),
        "Poêle & Casserole" to BadgeStyleMeta("Poêle & Casserole", "🍳", Stone100, Stone800, Stone200)
    )

    val AISLES = listOf(
        BadgeStyleMeta("Fruits & Légumes", "🥕", Emerald50, Emerald800, Emerald200),
        BadgeStyleMeta("Boucherie & Poissonnerie", "🥩", Rose50, Rose800, Rose200),
        BadgeStyleMeta("Crèmerie & Œufs", "🧀", Amber50, Amber800, Amber200),
        BadgeStyleMeta("Boulangerie", "🥖", Orange50, Orange800, Orange200),
        BadgeStyleMeta("Épicerie", "🍝", Stone100, Stone800, Stone200)
    )

    fun applianceMeta(appliance: String): BadgeStyleMeta =
        APPLIANCES_META[appliance] ?: APPLIANCES_META["Poêle & Casserole"]!!

    fun formatQuantity(value: Double): String {
        val roundedInt = value.roundToInt()
        if (abs(value - roundedInt) < 0.05) {
            return roundedInt.toString()
        }
        val roundedTenth = (value * 10.0).roundToInt() / 10.0
        return if (roundedTenth % 1.0 == 0.0) {
            roundedTenth.toInt().toString()
        } else {
            roundedTenth.toString().replace('.', ',')
        }
    }

    fun inferRecipeMoment(name: String): String {
        val lower = name.lowercase()
        if (listOf("velouté", "soupe", "croque", "tartine", "omelette", "galette").any { lower.contains(it) }) {
            return "soir"
        }
        if (listOf("hachis", "chili", "carbonara", "spaghetti").any { lower.contains(it) }) {
            return "any"
        }
        if (listOf("porc", "bœuf", "boeuf", "poulet", "dinde", "blanquette", "steak").any { lower.contains(it) }) {
            return "midi"
        }
        return "any"
    }

    fun isFishRecipe(recipe: Recipe?): Boolean {
        if (recipe == null) return false
        if (recipe.isFish) return true
        val fishKeywords = listOf(
            "saumon", "cabillaud", "poisson", "colin", "merlu", "thon",
            "truite", "lieu", "sardine", "maquereau", "dorade", "bar", "crevette", "gambas"
        )
        val text = "${recipe.name} ${recipe.ingredients.joinToString(" ") { it.name }}".lowercase()
        return fishKeywords.any { text.contains(it) }
    }

    fun isRecipeSuitableForMeal(recipe: Recipe, mealKey: String?): Boolean {
        val moment = recipe.moment.ifBlank { inferRecipeMoment(recipe.name) }
        if (mealKey.isNullOrBlank() || moment == "any") return true
        return moment == mealKey
    }
}
