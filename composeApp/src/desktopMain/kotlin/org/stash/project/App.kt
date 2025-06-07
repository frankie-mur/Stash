package org.stash.project

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.stash.project.db.DatabaseFactory

@Composable
@Preview
fun App() {
    var newUrl by remember { mutableStateOf("") }
    val articles = remember { mutableStateListOf<String>() }
    var errorMessage by remember { mutableStateOf("") }
    var selectedArticleTitle by remember { mutableStateOf<String?>(null) }
    var selectedContent by remember { mutableStateOf<String>("") }
    val coroutineScope = rememberCoroutineScope()
    val articleService = remember { service.ArticleService(DatabaseFactory) }

    LaunchedEffect(Unit) {
        DatabaseFactory.init()
        articles.addAll(DatabaseFactory.getAllArticles())
    }

    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("📥 Stash", style = MaterialTheme.typography.displayMedium)
            Spacer(Modifier.height(16.dp))

            if (selectedArticleTitle == null) {
                // List page
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = newUrl,
                        onValueChange = {
                            newUrl = it
                            errorMessage = ""
                        },
                        label = { Text("Enter URL") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                    )
                    Button(
                        onClick = {
                            if (newUrl.isNotBlank()) {
                                coroutineScope.launch {
                                    val updatedUrl =
                                        if (!newUrl.startsWith("http://") && !newUrl.startsWith("https://")) {
                                            "https://$newUrl"
                                        } else {
                                            newUrl
                                        }
                                    val title = articleService.addArticle(url = updatedUrl)
                                    articles.add(title)
                                    newUrl = ""
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(56.dp),
                    ) {
                        Text("Add")
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                Spacer(Modifier.height(16.dp))
                Text("Your Stash:", style = MaterialTheme.typography.bodySmall)
                val scrollState = rememberLazyListState()
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    items(articles) { item ->
                        Card(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        coroutineScope.launch {
                                            selectedContent = articleService.getContentByTitle(item)
                                            selectedArticleTitle = item
                                        }
                                    },
                            elevation = 4.dp,
                        ) {
                            Text(
                                item,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            } else {
                // Detail page
                val bodyElement = com.fleeksoft.ksoup.Ksoup.parseBodyFragment(selectedContent).body()
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(
                        onClick = { selectedArticleTitle = null },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.CenterVertically),
                    ) {
                        Text("← Back")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        selectedArticleTitle ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(bodyElement.children().toList()) { element ->
                        CustomHtmlRenderer(element)
                    }
                }
            }
        }
    }
}
