package service

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import org.stash.project.db.DatabaseFactory

class ArticleService(
    private val databaseFactory: DatabaseFactory,
) {
    suspend fun addArticle(url: String): String {
        // Fetch the html content from the URL and parse it if needed
        val doc = Ksoup.parseGetRequest(url)
        val title = doc.title()
        val content = doc.body() // Extract text content from the body

        databaseFactory.addArticle(url, title, content.html())
        return title
    }

    suspend fun getAllArticles(): List<String> {
        return databaseFactory.getAllArticles()
    }

    suspend fun getContentByTitle(title: String): String {
        return databaseFactory.getContentByTitle(title)
    }
}
