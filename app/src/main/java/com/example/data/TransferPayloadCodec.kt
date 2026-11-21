package com.example.data

import android.util.Base64
import com.example.model.AggregatedGroceryItem
import com.example.model.DefaultCatalog
import com.example.model.Ingredient
import com.example.model.ManualGroceryItem
import com.example.model.MealSlot
import com.example.model.Recipe
import com.example.model.SemainierConfig
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class DecodedTransferState(
    val customRecipes: List<Recipe>,
    val slots: List<MealSlot>,
    val favoriteIds: Set<String>?,
    val checkedKeys: Set<String>?,
    val manualItems: List<ManualGroceryItem>?
)

object TransferPayloadCodec {

    private val MENU_REGEX = Regex("MENU-[A-Za-z0-9\\-_]+")

    fun encodeMenuPayload(
        recipes: List<Recipe>,
        slots: List<MealSlot>,
        checkedKeys: Set<String>,
        manualItems: List<ManualGroceryItem>
    ): String {
        val slotsCompact = JSONObject()
        slots.forEach { slot ->
            val arr = JSONArray().apply {
                put(slot.recipeId ?: "")
                put(slot.servings)
                put(if (slot.disabled) 1 else 0)
            }
            slotsCompact.put(slot.slotKey, arr)
        }

        val favArray = JSONArray()
        recipes.filter { it.isFavorite }.forEach { favArray.put(it.id) }

        val checkedObj = JSONObject()
        checkedKeys.forEach { checkedObj.put(it, true) }

        val manualArr = JSONArray()
        manualItems.forEach { item ->
            manualArr.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("name", item.name)
                    put("qty", item.qty)
                    put("aisle", item.aisle)
                }
            )
        }

        val customRecipesArr = JSONArray()
        recipes.filter { it.id.startsWith("custom-") || it.isCustom }.forEach { rec ->
            val ingArr = JSONArray()
            rec.ingredients.forEach { ing ->
                ingArr.put(
                    JSONObject().apply {
                        put("name", ing.name)
                        put("qty", ing.qty)
                        put("unit", ing.unit)
                        put("aisle", ing.aisle)
                    }
                )
            }
            customRecipesArr.put(
                JSONObject().apply {
                    put("id", rec.id)
                    put("name", rec.name)
                    put("appliance", rec.appliance)
                    put("category", rec.category)
                    put("moment", rec.moment)
                    put("isFish", rec.isFish)
                    put("time", rec.time)
                    put("notes", rec.notes)
                    put("ingredients", ingArr)
                }
            )
        }

        val payload = JSONObject().apply {
            put("v", 1)
            put("p", slotsCompact)
            put("f", favArray)
            put("c", checkedObj)
            put("m", manualArr)
            put("r", customRecipesArr)
        }

        val bytes = payload.toString().toByteArray(StandardCharsets.UTF_8)
        val b64 = Base64.encodeToString(
            bytes,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
        return "MENU-$b64"
    }

    fun decodeMenuPayload(rawInput: String): DecodedTransferState? {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) return null

        val candidateInput = if (trimmed.startsWith("{")) {
            try {
                val backupObj = JSONObject(trimmed)
                backupObj.optString("menuCode", trimmed)
            } catch (_: Exception) {
                trimmed
            }
        } else {
            trimmed
        }

        val match = MENU_REGEX.find(candidateInput) ?: return null
        val token = match.value
        if (!token.startsWith("MENU-")) return null

        return try {
            val encodedPart = token.removePrefix("MENU-")
            val bytes = Base64.decode(encodedPart, Base64.URL_SAFE or Base64.NO_WRAP)
            val jsonStr = String(bytes, StandardCharsets.UTF_8)
            val payload = JSONObject(jsonStr)
            val slotsObj = payload.optJSONObject("p") ?: return null

            val customRecipes = mutableListOf<Recipe>()
            val customArr = payload.optJSONArray("r")
            if (customArr != null) {
                for (i in 0 until customArr.length()) {
                    val obj = customArr.optJSONObject(i) ?: continue
                    val id = obj.optString("id", "").trim()
                    val name = obj.optString("name", "").trim()
                    if (id.isEmpty() || name.isEmpty()) continue
                    val ingList = mutableListOf<Ingredient>()
                    val ingArr = obj.optJSONArray("ingredients")
                    if (ingArr != null) {
                        for (j in 0 until ingArr.length()) {
                            val iObj = ingArr.optJSONObject(j) ?: continue
                            ingList.add(
                                Ingredient(
                                    name = iObj.optString("name", ""),
                                    qty = iObj.optDouble("qty", 1.0),
                                    unit = iObj.optString("unit", ""),
                                    aisle = iObj.optString("aisle", "Épicerie")
                                )
                            )
                        }
                    }
                    val draft = Recipe(
                        id = id,
                        name = name,
                        appliance = obj.optString("appliance", "Cookeo"),
                        category = obj.optString("category", "Traditionnel"),
                        moment = obj.optString("moment", SemainierConfig.inferRecipeMoment(name)),
                        isFish = obj.optBoolean("isFish", false),
                        time = obj.optInt("time", 15),
                        notes = obj.optString("notes", ""),
                        ingredients = ingList,
                        isCustom = true
                    )
                    customRecipes.add(
                        draft.copy(isFish = SemainierConfig.isFishRecipe(draft))
                    )
                }
            }

            val baseSlots = DefaultCatalog.buildDefaultEmptyPlan().associateBy { it.slotKey }.toMutableMap()
            val keys = slotsObj.keys()
            while (keys.hasNext()) {
                val slotKey = keys.next()
                val arr = slotsObj.optJSONArray(slotKey) ?: continue
                val existing = baseSlots[slotKey] ?: continue
                val recipeId = arr.optString(0, "").ifBlank { null }
                val servings = arr.optInt(1, existing.servings).coerceIn(1, 12)
                val disabled = arr.optInt(2, 0) == 1
                baseSlots[slotKey] = existing.copy(
                    recipeId = recipeId,
                    servings = servings,
                    disabled = disabled
                )
            }

            val favArr = payload.optJSONArray("f")
            val favoriteIds: Set<String>? = if (favArr != null) {
                buildSet<String> {
                    for (i in 0 until favArr.length()) {
                        val id = favArr.optString(i, "")
                        if (id.isNotBlank()) add(id)
                    }
                }
            } else null

            val checkedObj = payload.optJSONObject("c")
            val checkedKeys: Set<String>? = if (checkedObj != null) {
                buildSet<String> {
                    val cKeys = checkedObj.keys()
                    while (cKeys.hasNext()) {
                        val k = cKeys.next()
                        if (checkedObj.optBoolean(k, false)) add(k)
                    }
                }
            } else null

            val manualArr = payload.optJSONArray("m")
            val manualItems: List<ManualGroceryItem>? = if (manualArr != null) {
                buildList<ManualGroceryItem> {
                    for (i in 0 until manualArr.length()) {
                        val obj = manualArr.optJSONObject(i)
                        val name = obj?.optString("name", "")?.trim().orEmpty()
                        if (obj != null && name.isNotEmpty()) {
                            add(
                                ManualGroceryItem(
                                    id = obj.optString("id", System.currentTimeMillis().toString() + i),
                                    name = name,
                                    qty = obj.optString("qty", ""),
                                    aisle = obj.optString("aisle", "Épicerie")
                                )
                            )
                        }
                    }
                }
            } else null

            DecodedTransferState(
                customRecipes = customRecipes,
                slots = DefaultCatalog.buildDefaultEmptyPlan().map { baseSlots[it.slotKey] ?: it },
                favoriteIds = favoriteIds,
                checkedKeys = checkedKeys,
                manualItems = manualItems
            )
        } catch (_: Exception) {
            null
        }
    }

    fun buildBackupJson(menuCode: String): String {
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return JSONObject().apply {
            put("menuCode", menuCode)
            put("exportedAt", isoFormat.format(Date()))
        }.toString(2)
    }

    fun formatGroceryListText(items: List<AggregatedGroceryItem>): String {
        val unchecked = items.filter { !it.isChecked }
        if (unchecked.isEmpty()) return ""
        val lines = mutableListOf("🛒 MA LISTE DE COURSES")
        SemainierConfig.AISLES.forEach { aisle ->
            val aisleItems = unchecked.filter { it.aisle == aisle.name }
            if (aisleItems.isNotEmpty()) {
                lines.add("")
                lines.add("${aisle.icon} ${aisle.name.uppercase(Locale.FRENCH)}")
                aisleItems.forEach { it ->
                    val qtySuffix = if (it.isManual) {
                        if (it.qtyText.isNotBlank()) " (${it.qtyText})" else ""
                    } else {
                        " : ${SemainierConfig.formatQuantity(it.qty)} ${it.unit}"
                    }
                    lines.add("- ${it.name}$qtySuffix")
                }
            }
        }
        return lines.joinToString("\n")
    }
}
