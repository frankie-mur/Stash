package org.stash.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.TextNode

@Composable
fun CustomHtmlRenderer(
    element: Element,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        RenderElement(element)
    }
}

@Composable
private fun RenderElement(element: Element) {
    when (element.tagName().lowercase()) {
        "p" -> {
            RenderParagraph(element)
        }
        "h1" -> {
            RenderHeading(element, MaterialTheme.typography.h3)
        }
        "h2" -> {
            RenderHeading(element, MaterialTheme.typography.h4)
        }
        "h3" -> {
            RenderHeading(element, MaterialTheme.typography.h5)
        }
        "h4" -> {
            RenderHeading(element, MaterialTheme.typography.h6)
        }
        "h5" -> {
            RenderHeading(element, MaterialTheme.typography.subtitle1)
        }
        "h6" -> {
            RenderHeading(element, MaterialTheme.typography.subtitle2)
        }
        "div" -> {
            RenderDiv(element)
        }
        "span" -> {
            RenderSpan(element)
        }
        "ul" -> {
            RenderUnorderedList(element)
        }
        "ol" -> {
            RenderOrderedList(element)
        }
        "li" -> {
            RenderListItem(element)
        }
        "a" -> {
            RenderLink(element)
        }
        "img" -> {
            RenderImage(element)
        }
        "br" -> {
            Spacer(modifier = Modifier.height(8.dp))
        }
        "hr" -> {
            Divider(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
            )
        }
        "blockquote" -> {
            RenderBlockquote(element)
        }
        "pre" -> {
            RenderPreformatted(element)
        }
        "code" -> {
            RenderCode(element)
        }
        "table" -> {
            RenderTable(element)
        }
        else -> {
            // Handle other elements by rendering their children
            element.children().forEach { child ->
                RenderElement(child)
            }
            // If no children, render as text
            if (element.children().isEmpty() && element.hasText()) {
                Text(
                    text = element.text(),
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun RenderParagraph(element: Element) {
    val annotatedString = buildStyledText(element)
    SelectionContainer {
        Text(
            text = annotatedString,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 8.dp),
        )
    }
}

@Composable
private fun RenderHeading(
    element: Element,
    textStyle: TextStyle,
) {
    val annotatedString = buildStyledText(element)
    SelectionContainer {
        Text(
            text = annotatedString,
            style = textStyle.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

@Composable
private fun RenderDiv(element: Element) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        element.children().forEach { child ->
            RenderElement(child)
        }
    }
}

@Composable
private fun RenderSpan(element: Element) {
    val annotatedString = buildStyledText(element)
    Text(text = annotatedString)
}

@Composable
private fun RenderUnorderedList(element: Element) {
    Column(modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)) {
        element.select("li").forEach { listItem ->
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                Text(
                    text = "• ",
                    modifier = Modifier.padding(end = 4.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    RenderElement(listItem)
                }
            }
        }
    }
}

@Composable
private fun RenderOrderedList(element: Element) {
    Column(modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)) {
        element.select("li").forEachIndexed { index, listItem ->
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                Text(
                    text = "${index + 1}. ",
                    modifier = Modifier.padding(end = 4.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    RenderElement(listItem)
                }
            }
        }
    }
}

@Composable
private fun RenderListItem(element: Element) {
    val annotatedString = buildStyledText(element)
    Text(
        text = annotatedString,
        modifier = Modifier.padding(bottom = 4.dp),
    )
}

@Composable
private fun RenderLink(element: Element) {
    val href = element.attr("href")
    val text = element.text()

    ClickableText(
        text =
            AnnotatedString(
                text = text,
                spanStyle =
                    SpanStyle(
                        color = MaterialTheme.colors.primary,
                        textDecoration = TextDecoration.Underline,
                    ),
            ),
        onClick = {
            // Handle link click - you can implement URL opening logic here
            println("Link clicked: $href")
        },
        modifier = Modifier.padding(vertical = 2.dp),
    )
}

@Composable
private fun RenderImage(element: Element) {
    val src = element.attr("src")
    val alt = element.attr("alt")

    // Placeholder for image - you'll need to implement actual image loading
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(vertical = 8.dp),
        backgroundColor = Color.LightGray,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "Image: $alt")
            Text(text = "Source: $src", style = MaterialTheme.typography.caption)
        }
    }
}

@Composable
private fun RenderBlockquote(element: Element) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 2.dp,
    ) {
        Row {
            Divider(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .width(4.dp),
                color = MaterialTheme.colors.primary,
            )
            Column(modifier = Modifier.padding(16.dp)) {
                element.children().forEach { child ->
                    RenderElement(child)
                }
            }
        }
    }
}

@Composable
private fun RenderPreformatted(element: Element) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        backgroundColor = Color.Black.copy(alpha = 0.05f),
    ) {
        SelectionContainer {
            Text(
                text = element.text(),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.body2,
            )
        }
    }
}

@Composable
private fun RenderCode(element: Element) {
    Text(
        text = element.text(),
        fontFamily = FontFamily.Monospace,
        style =
            MaterialTheme.typography.body2.copy(
                background = Color.Black.copy(alpha = 0.1f),
            ),
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
    )
}

@Composable
private fun RenderTable(element: Element) {
    // Basic table implementation - you might want to enhance this
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        elevation = 1.dp,
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            element.select("tr").forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    row.select("td, th").forEach { cell ->
                        Text(
                            text = cell.text(),
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(8.dp),
                            fontWeight = if (cell.tagName() == "th") FontWeight.Bold else FontWeight.Normal,
                        )
                    }
                }
                if (row != element.select("tr").last()) {
                    Divider()
                }
            }
        }
    }
}

private fun buildStyledText(element: Element): AnnotatedString {
    return buildAnnotatedString {
        processElement(element, this)
    }
}

private fun processElement(
    element: Element,
    builder: AnnotatedString.Builder,
) {
    element.childNodes().forEach { node ->
        when (node) {
            is TextNode -> {
                builder.append(node.text())
            }
            is Element -> {
                when (node.tagName().lowercase()) {
                    "b", "strong" -> {
                        builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            processElement(node, builder)
                        }
                    }
                    "i", "em" -> {
                        builder.withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            processElement(node, builder)
                        }
                    }
                    "u" -> {
                        builder.withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                            processElement(node, builder)
                        }
                    }
                    "s", "strike", "del" -> {
                        builder.withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                            processElement(node, builder)
                        }
                    }
                    "sup" -> {
                        builder.withStyle(SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 12.sp)) {
                            processElement(node, builder)
                        }
                    }
                    "sub" -> {
                        builder.withStyle(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 12.sp)) {
                            processElement(node, builder)
                        }
                    }
                    "code" -> {
                        builder.withStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = Color.Black.copy(alpha = 0.1f))) {
                            processElement(node, builder)
                        }
                    }
                    "br" -> {
                        builder.append("\n")
                    }
                    else -> {
                        processElement(node, builder)
                    }
                }
            }
        }
    }
}
