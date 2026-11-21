package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AggregatedGroceryItem
import com.example.model.BadgeStyleMeta
import com.example.model.SemainierConfig
import com.example.ui.SemainierUiState
import com.example.ui.theme.Cream50
import com.example.ui.theme.Cream100
import com.example.ui.theme.Cream200
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Sage600
import com.example.ui.theme.Stone300
import com.example.ui.theme.Stone400
import com.example.ui.theme.Stone500
import com.example.ui.theme.Stone600
import com.example.ui.theme.Stone700
import com.example.ui.theme.Stone800
import com.example.ui.theme.Stone900
import com.example.ui.theme.Terracotta50
import com.example.ui.theme.Terracotta500
import com.example.ui.theme.Terracotta700

@Composable
fun GroceriesScreen(
    state: SemainierUiState,
    onToggleCheck: (String, Boolean) -> Unit,
    onUncheckAll: () -> Unit,
    onCopyList: () -> Unit,
    onShareList: () -> Unit,
    onAddManualItem: (String, String, String) -> Unit,
    onRemoveManualItem: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val aisleGroups = remember(state.groceries) {
        SemainierConfig.AISLES.mapNotNull { aisle ->
            val items = state.groceries.filter { it.aisle == aisle.name }
            if (items.isEmpty()) null else aisle to items
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
                GroceriesHeaderCard(
                    onUncheckAll = onUncheckAll,
                    onCopyList = onCopyList,
                    onShareList = onShareList,
                    onAddManualItem = onAddManualItem
                )
            }

            if (aisleGroups.isEmpty()) {
                item(span = { GridItemSpan(columns) }) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = PureWhite,
                        border = BorderStroke(1.dp, Cream200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.groceries_empty_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = Stone700,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = stringResource(R.string.groceries_empty_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = Stone500,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(
                    items = aisleGroups,
                    key = { it.first.name }
                ) { (aisleMeta, items) ->
                    AisleCard(
                        aisleMeta = aisleMeta,
                        items = items,
                        onToggleCheck = onToggleCheck,
                        onRemoveManualItem = onRemoveManualItem
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GroceriesHeaderCard(
    onUncheckAll: () -> Unit,
    onCopyList: () -> Unit,
    onShareList: () -> Unit,
    onAddManualItem: (String, String, String) -> Unit
) {
    var manualName by remember { mutableStateOf("") }
    var manualQty by remember { mutableStateOf("") }
    var manualAisle by remember { mutableStateOf("Épicerie") }
    var aisleDropdownExpanded by remember { mutableStateOf(false) }

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
                    text = stringResource(R.string.groceries_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Stone900
                )
                Text(
                    text = stringResource(R.string.groceries_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Stone500
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onUncheckAll,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Cream100,
                        contentColor = Stone700
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 9.dp),
                    modifier = Modifier.testTag("uncheck_all_groceries_button")
                ) {
                    Text(
                        text = stringResource(R.string.groceries_uncheck_all),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = onCopyList,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Sage600,
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 9.dp),
                    modifier = Modifier.testTag("copy_groceries_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.groceries_copy),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = onShareList,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Cream200),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 9.dp),
                    modifier = Modifier.testTag("share_groceries_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = null,
                        tint = Stone700,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.groceries_share),
                        color = Stone700,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Cream100)
            )

            // Manual grocery addition form
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = manualName,
                    onValueChange = { manualName = it },
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Ajouter un article libre (Yaourts, Café…)",
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
                        .testTag("manual_grocery_name_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = manualQty,
                        onValueChange = { manualQty = it },
                        singleLine = true,
                        placeholder = { Text("Qté (1 pack)", fontSize = 12.sp) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Cream50,
                            unfocusedContainerColor = Cream50,
                            focusedBorderColor = Terracotta500,
                            unfocusedBorderColor = Cream200
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("manual_grocery_qty_input")
                    )

                    Box {
                        val selectedMeta = SemainierConfig.AISLES.find { it.name == manualAisle }
                            ?: SemainierConfig.AISLES.last()
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Cream50,
                            border = BorderStroke(1.dp, Cream200),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { aisleDropdownExpanded = true }
                                .testTag("manual_grocery_aisle_dropdown")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${selectedMeta.icon} ${selectedMeta.name}",
                                    color = Stone700,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
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
                            expanded = aisleDropdownExpanded,
                            onDismissRequest = { aisleDropdownExpanded = false },
                            containerColor = PureWhite
                        ) {
                            SemainierConfig.AISLES.forEach { aisle ->
                                DropdownMenuItem(
                                    text = { Text("${aisle.icon} ${aisle.name}") },
                                    onClick = {
                                        manualAisle = aisle.name
                                        aisleDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (manualName.isNotBlank()) {
                                onAddManualItem(manualName, manualQty, manualAisle)
                                manualName = ""
                                manualQty = ""
                            }
                        },
                        enabled = manualName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Stone800,
                            contentColor = PureWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                        modifier = Modifier.testTag("add_manual_grocery_button")
                    ) {
                        Text("+ Ajouter", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AisleCard(
    aisleMeta: BadgeStyleMeta,
    items: List<AggregatedGroceryItem>,
    onToggleCheck: (String, Boolean) -> Unit,
    onRemoveManualItem: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, Cream200),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(aisleMeta.bgColor)
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${aisleMeta.icon} ${aisleMeta.name}",
                    color = aisleMeta.textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(PureWhite.copy(alpha = 0.85f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = items.size.toString(),
                        color = aisleMeta.textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(aisleMeta.borderColor)
            )

            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onToggleCheck(item.key, item.isChecked) }
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                            .testTag("grocery_item_${index}_${item.aisle}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = item.isChecked,
                            onCheckedChange = { onToggleCheck(item.key, item.isChecked) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Sage600,
                                checkmarkColor = PureWhite,
                                uncheckedColor = Stone300
                            ),
                            modifier = Modifier.size(22.dp)
                        )

                        Text(
                            text = item.name,
                            color = if (item.isChecked) Stone400 else Stone800,
                            fontSize = 14.sp,
                            fontWeight = if (item.isChecked) FontWeight.Normal else FontWeight.Medium,
                            textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                            modifier = Modifier.weight(1f)
                        )

                        if (item.isManual) {
                            if (item.qtyText.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Cream100)
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = item.qtyText,
                                        color = Stone600,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            if (item.manualId != null) {
                                IconButton(
                                    onClick = { onRemoveManualItem(item.manualId) },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .testTag("remove_manual_grocery_${item.manualId}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = stringResource(R.string.cd_delete_item),
                                        tint = Stone400,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Terracotta50)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${SemainierConfig.formatQuantity(item.qty)} ${item.unit}",
                                    color = Terracotta700,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (index < items.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Cream100)
                        )
                    }
                }
            }
        }
    }
}
