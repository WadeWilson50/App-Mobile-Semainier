package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.Ingredient
import com.example.model.SemainierConfig
import com.example.ui.theme.Cream50
import com.example.ui.theme.Cream100
import com.example.ui.theme.Cream200
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Red600
import com.example.ui.theme.Stone400
import com.example.ui.theme.Stone500
import com.example.ui.theme.Stone600
import com.example.ui.theme.Stone700
import com.example.ui.theme.Stone900
import com.example.ui.theme.Terracotta500
import com.example.ui.theme.Terracotta600

private data class IngredientDraft(
    val id: Int,
    var name: String = "",
    var qty: String = "",
    var unit: String = "",
    var aisle: String = "Épicerie"
)

@Composable
fun AddRecipeDialog(
    onSave: (
        name: String,
        appliance: String,
        category: String,
        time: Int,
        moment: String,
        notes: String,
        ingredients: List<Ingredient>
    ) -> Unit,
    onDismiss: () -> Unit,
    onValidationMessage: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var appliance by remember { mutableStateOf("Cookeo") }
    var category by remember { mutableStateOf("Traditionnel") }
    var timeText by remember { mutableStateOf("15") }
    var moment by remember { mutableStateOf("any") }
    var notes by remember { mutableStateOf("") }

    var nextId by remember { mutableStateOf(4) }
    val ingredientDrafts = remember {
        mutableStateListOf(
            IngredientDraft(1),
            IngredientDraft(2),
            IngredientDraft(3)
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
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .widthIn(max = 580.dp)
                .fillMaxWidth()
                .heightIn(max = 760.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Cream50)
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nouvelle Recette Rapide",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Stone900
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_add_recipe_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.cd_close),
                            tint = Stone500
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Cream200))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    FieldLabel("NOM DU PLAT *")
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        placeholder = { Text("Ex: Poulet curry au Cookeo", fontSize = 13.sp) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Cream50,
                            unfocusedContainerColor = Cream50,
                            focusedBorderColor = Terracotta500,
                            unfocusedBorderColor = Cream200
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_recipe_name_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1.3f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            FieldLabel("APPAREIL *")
                            CompactDropdown(
                                selected = appliance,
                                options = listOf(
                                    "Cookeo" to "🥘 Cookeo",
                                    "Thermomix TM6" to "🌪️ TM6",
                                    "Airfryer" to "🍟 Airfryer",
                                    "Poêle & Casserole" to "🍳 Poêle"
                                ),
                                onSelect = { appliance = it }
                            )
                        }
                        Column(modifier = Modifier.weight(1.1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            FieldLabel("STYLE *")
                            CompactDropdown(
                                selected = category,
                                options = listOf(
                                    "Traditionnel" to "🇫🇷 Trad.",
                                    "Convivial" to "🍕 Convivial"
                                ),
                                onSelect = { category = it }
                            )
                        }
                        Column(modifier = Modifier.weight(0.8f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            FieldLabel("MIN *")
                            OutlinedTextField(
                                value = timeText,
                                onValueChange = { timeText = it.filter { c -> c.isDigit() } },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Cream50,
                                    unfocusedContainerColor = Cream50,
                                    focusedBorderColor = Terracotta500,
                                    unfocusedBorderColor = Cream200
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("add_recipe_time_input")
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        FieldLabel("MOMENT CONSEILLÉ")
                        CompactDropdown(
                            selected = moment,
                            options = listOf(
                                "any" to "☀️🌙 Midi & Soir (polyvalent)",
                                "midi" to "☀️ Midi uniquement (plat complet)",
                                "soir" to "🌙 Soir uniquement (léger / velouté)"
                            ),
                            onSelect = { moment = it }
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        FieldLabel("MODE DE CUISSON & RÉGLAGES")
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            minLines = 2,
                            maxLines = 4,
                            placeholder = {
                                Text(
                                    "Ex: Mode sous pression 8 min au Cookeo, ou 180°C 12 min à l'Airfryer...",
                                    fontSize = 13.sp
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
                                .fillMaxWidth()
                                .testTag("add_recipe_notes_input")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FieldLabel("INGRÉDIENTS (QTÉ / 1 PERS.) *")
                        TextButton(
                            onClick = {
                                ingredientDrafts.add(IngredientDraft(nextId++))
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("add_ingredient_row_button")
                        ) {
                            Text(
                                text = "+ Ajouter une ligne",
                                color = Terracotta600,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ingredientDrafts.forEachIndexed { index, draft ->
                            IngredientDraftRow(
                                index = index,
                                draft = draft,
                                onUpdate = { updated -> ingredientDrafts[index] = updated },
                                onRemove = {
                                    if (ingredientDrafts.size > 1) {
                                        ingredientDrafts.removeAt(index)
                                    }
                                }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Cream200))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("cancel_add_recipe_button")
                    ) {
                        Text("Annuler", color = Stone600)
                    }
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                onValidationMessage("Veuillez saisir un nom de plat.")
                                return@Button
                            }
                            val parsedIngredients = ingredientDrafts.mapNotNull { row ->
                                val iName = row.name.trim()
                                val iQty = row.qty.replace(',', '.').toDoubleOrNull()
                                val iUnit = row.unit.trim()
                                if (iName.isNotEmpty() && iQty != null && iQty > 0.0 && iUnit.isNotEmpty()) {
                                    Ingredient(iName, iQty, iUnit, row.aisle)
                                } else null
                            }
                            if (parsedIngredients.isEmpty()) {
                                onValidationMessage("Ajoutez au moins un ingrédient valide.")
                                return@Button
                            }
                            onSave(
                                name,
                                appliance,
                                category,
                                timeText.toIntOrNull() ?: 15,
                                moment,
                                notes,
                                parsedIngredients
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Terracotta500,
                            contentColor = PureWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_recipe_button")
                    ) {
                        Text("Enregistrer la recette", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = Stone700,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun CompactDropdown(
    selected: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val label = options.find { it.first == selected }?.second ?: options.first().second

    Box {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Cream50,
            border = BorderStroke(1.dp, Cream200),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = Stone700,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Stone500,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = PureWhite
        ) {
            options.forEach { (value, optLabel) ->
                DropdownMenuItem(
                    text = { Text(optLabel, fontSize = 13.sp) },
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
private fun IngredientDraftRow(
    index: Int,
    draft: IngredientDraft,
    onUpdate: (IngredientDraft) -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Cream100.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, Cream200),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = draft.name,
                    onValueChange = { onUpdate(draft.copy(name = it)) },
                    singleLine = true,
                    placeholder = { Text("Ingrédient (Pâtes)", fontSize = 12.sp) },
                    shape = RoundedCornerShape(9.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite,
                        focusedBorderColor = Terracotta500,
                        unfocusedBorderColor = Cream200
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ing_name_$index")
                )
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Supprimer ligne",
                        tint = Stone400.copy(alpha = 0.9f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = draft.qty,
                    onValueChange = { onUpdate(draft.copy(qty = it)) },
                    singleLine = true,
                    placeholder = { Text("Qté/pers", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(9.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite,
                        focusedBorderColor = Terracotta500,
                        unfocusedBorderColor = Cream200
                    ),
                    modifier = Modifier
                        .weight(0.8f)
                        .testTag("ing_qty_$index")
                )
                OutlinedTextField(
                    value = draft.unit,
                    onValueChange = { onUpdate(draft.copy(unit = it)) },
                    singleLine = true,
                    placeholder = { Text("Unité (g, ml)", fontSize = 12.sp) },
                    shape = RoundedCornerShape(9.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite,
                        focusedBorderColor = Terracotta500,
                        unfocusedBorderColor = Cream200
                    ),
                    modifier = Modifier
                        .weight(0.9f)
                        .testTag("ing_unit_$index")
                )
                Box(modifier = Modifier.weight(1.1f)) {
                    CompactDropdown(
                        selected = draft.aisle,
                        options = SemainierConfig.AISLES.map { it.name to "${it.icon} ${it.name}" },
                        onSelect = { onUpdate(draft.copy(aisle = it)) }
                    )
                }
            }
        }
    }
}
