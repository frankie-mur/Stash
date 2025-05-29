package org.stash.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.stash.project.db.DatabaseFactory

@Composable
@Preview
fun App() {
    var newUrl by remember { mutableStateOf("") }
    var articles = remember { mutableStateListOf<String>() }
    val coroutineScope = rememberCoroutineScope()

    //Launches in background
    LaunchedEffect(Unit) {
        DatabaseFactory.init()
        articles.addAll(DatabaseFactory.getAllArticles())
    }

    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("📥 Stash", style = MaterialTheme.typography.displayMedium)
            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = newUrl,
                    onValueChange = { newUrl = it },
                    label = { Text("Enter URL") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
                Button(
                    onClick = {
                        if (newUrl.isNotBlank()) {
                            // Launch in coroutine to not block main thread
                            coroutineScope.launch {
                                DatabaseFactory.addArticle(newUrl, "", "")
                                articles = DatabaseFactory.getAllArticles().toMutableStateList()
                                newUrl = ""
                            }
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Add")
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Your Stash:", style = MaterialTheme.typography.bodySmall)
            val scrollState = rememberLazyListState()
            LazyColumn(
                state = scrollState,
                modifier = Modifier.fillMaxHeight()
            ){
                items(articles) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = 4.dp
                    ) {
                        Text(
                            item,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }

    }