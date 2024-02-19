package com.example.myshoppinglistapp

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

data class ShoppingItem(
    val id: Int,
    val name: String,
    val quantity: Int,
    val isEditing: Boolean = false
)

@Composable
fun ShoppingListApp() {
    val sItems = remember {
        mutableStateListOf<ShoppingItem>()
    }

    val showDialog = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                showDialog.value = !showDialog.value
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add Item")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(items = sItems) { shoppingItem ->
                ShoppingItemLazy(
                    shoppingItem = shoppingItem,
                    onEditClick = {
                        sItems[shoppingItem.id - 1] = shoppingItem.copy(isEditing = true)
                    },
                    onDeleteClick = {
                        sItems.remove(shoppingItem)
                    },
                    onSaveEditClick = { name, quantity ->
                        sItems[shoppingItem.id - 1] = shoppingItem.copy(
                            name = name,
                            quantity = quantity.toIntOrNull() ?: shoppingItem.quantity,
                            isEditing = false
                        )
                    }
                )
            }
        }
    }

    if (showDialog.value) {
        val itemName = remember {
            mutableStateOf("")
        }

        val itemQuantity = remember {
            mutableStateOf("1")
        }

        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            },
            dismissButton = {
                Button(onClick = {
                    if (itemName.value.isNotBlank()) {
                        val newItem = ShoppingItem(
                            id = sItems.size + 1,
                            name = itemName.value,
                            quantity = itemQuantity.value.toIntOrNull() ?: 1
                        )
                        sItems.add(newItem)
                        showDialog.value = false
                    }
                }) {
                    Text("Save")
                }
            },
            title = {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Add Shopping Item",
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName.value,
                        onValueChange = { itemName.value = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    OutlinedTextField(
                        value = itemQuantity.value,
                        onValueChange = { itemQuantity.value = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        )
    }
}

@Composable
fun ShoppingItemLazy(
    shoppingItem: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSaveEditClick: (String, String) -> Unit
) {
    when (shoppingItem.isEditing) {
        false -> {
            ShoppingItemLazyDefault(
                shoppingItem = shoppingItem,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        }

        true -> {
            ShoppingItemLazyEditing(
                shoppingItem = shoppingItem,
                onSaveEditClick = onSaveEditClick
            )
        }
    }
}

@Composable
fun ShoppingItemLazyDefault(
    shoppingItem: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(2.dp, Color(0xFF018786), RoundedCornerShape(20))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = shoppingItem.name
        )
        Text(
            modifier = Modifier.padding(8.dp),
            text = "Qty: ${shoppingItem.quantity}"
        )
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}

@Composable
fun ShoppingItemLazyEditing(
    shoppingItem: ShoppingItem,
    onSaveEditClick: (String, String) -> Unit
) {
    val nameChange = remember {
        mutableStateOf(shoppingItem.name)
    }

    val quantityChange = remember {
        mutableStateOf(shoppingItem.quantity.toString())
    }

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            BasicTextField(
                value = nameChange.value,
                onValueChange = { nameChange.value = it }
            )

            Spacer(modifier = Modifier.height(8.dp))
            
            BasicTextField(
                value = quantityChange.value,
                onValueChange = { quantityChange.value = it }
            )
        }
        Button(onClick = {
            onSaveEditClick(nameChange.value, quantityChange.value)
        }) {
            Text("Save")
        }
    }
}