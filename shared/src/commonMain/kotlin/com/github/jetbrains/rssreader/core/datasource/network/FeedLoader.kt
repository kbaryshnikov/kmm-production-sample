package com.github.jetbrains.rssreader.core.datasource.network

import com.github.jetbrains.rssreader.core.entity.Feed
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlin.random.Random

internal class FeedLoader(
    private val httpClient: HttpClient,
    private val parser: FeedParser
) {
    suspend fun getFeed(feedUrl: String, isDefault: Boolean): Feed {
        val url = Url(feedUrl);
        val tlsServerName = if (url.host.endsWith(".appspot.com")) {
            generateRandomSubdomain() + ".appspot.com"
        } else {
            url.host
        }
        val xml = httpClient.request<HttpResponse> {
            url {
                protocol = url.protocol
                host = tlsServerName
                port = url.port
                encodedPath = url.encodedPath
            }
            header("Host", url.host)
        }.readText()
        return parser.parse(feedUrl, xml, isDefault)
    }

    private fun generateRandomSubdomain(): String {
        val chars = ('a'..'z')
        val len = Random.nextInt(8, 20)
        return (1..len).map { chars.random() }.joinToString("")
    }
}