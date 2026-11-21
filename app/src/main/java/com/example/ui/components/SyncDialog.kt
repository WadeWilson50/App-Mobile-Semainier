package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.Cream50
import com.example.ui.theme.Cream200
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Sage50
import com.example.ui.theme.Sage200
import com.example.ui.theme.Sage600
import com.example.ui.theme.Sage700
import com.example.ui.theme.Stone500
import com.example.ui.theme.Stone600
import com.example.ui.theme.Stone700
import com.example.ui.theme.Stone800
import com.example.ui.theme.Stone900
import com.example.ui.theme.Terracotta500

@Composable
fun SyncDialog(
    transferCode: String,
    onShareCode: (String) -> Unit,
    onCopyCode: (String) -> Unit,
    onImportCode: (String) -> Unit,
    onExportJsonBackup: () -> Unit,
    onImportJsonBackup: () -> Unit,
    onDismiss: () -> Unit
) {
    var pasteInput by remember { mutableStateOf("") }

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
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .widthIn(max = 540.dp)
                .fillMaxWidth()
                .heightIn(max = 720.dp)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("📱💻", fontSize = 20.sp)
                        Column {
                            Text(
                                text = "Chromebook ↔ Téléphone",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Stone900
                            )
                            Text(
                                text = "Compatible avec la version web locale",
                                style = MaterialTheme.typography.bodySmall,
                                color = Stone500
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_sync_dialog")
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
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Step 1: Send
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Sage50,
                        border = BorderStroke(1.dp, Sage200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "1. ENVOYER CE MENU",
                                    color = Sage700,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = PureWhite,
                                    border = BorderStroke(1.dp, Sage200)
                                ) {
                                    Text(
                                        text = "Mode local",
                                        color = Sage700,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onShareCode(transferCode) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Sage600,
                                        contentColor = PureWhite
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("sync_share_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Share,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text("Partager", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }

                                OutlinedButton(
                                    onClick = { onCopyCode(transferCode) },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Sage200),
                                    colors = ButtonDefaults.outlinedButtonColors(containerColor = PureWhite),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("sync_copy_code_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ContentCopy,
                                        contentDescription = null,
                                        tint = Stone800,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "Copier le code",
                                        color = Stone800,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Text(
                                text = "Copiez ou partagez ce code MENU-... vers votre Chromebook (WhatsApp, Mail, Keep) ou collez ci-dessous un code reçu depuis le Chromebook.",
                                color = Sage700,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }

                    // Step 2: Import
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "2. CHARGER UN MENU REÇU (CODE OU LIEN)",
                            color = Stone700,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = pasteInput,
                            onValueChange = { pasteInput = it },
                            minLines = 3,
                            maxLines = 5,
                            placeholder = {
                                Text(
                                    text = "Collez ici le code MENU-... ou le lien reçu depuis votre Chromebook...",
                                    fontSize = 12.sp
                                )
                            },
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Cream50,
                                unfocusedContainerColor = Cream50,
                                focusedBorderColor = Terracotta500,
                                unfocusedBorderColor = Cream200
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sync_paste_code_input")
                        )

                        Button(
                            onClick = { onImportCode(pasteInput) },
                            enabled = pasteInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Terracotta500,
                                contentColor = PureWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sync_apply_code_button")
                        ) {
                            Text("Appliquer ce menu ici", fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Cream200))

                    // Backup (.json)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onExportJsonBackup,
                            modifier = Modifier.testTag("sync_export_json_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = null,
                                tint = Stone600,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Sauvegarde (.json)",
                                color = Stone600,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        TextButton(
                            onClick = onImportJsonBackup,
                            modifier = Modifier.testTag("sync_import_json_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.UploadFile,
                                contentDescription = null,
                                tint = Stone600,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Importer (.json)",
                                color = Stone600,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
