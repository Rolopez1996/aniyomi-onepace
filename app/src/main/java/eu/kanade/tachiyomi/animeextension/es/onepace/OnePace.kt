package eu.kanade.tachiyomi.animeextension.es.onepace

import eu.kanade.tachiyomi.animesource.model.*
import eu.kanade.tachiyomi.animesource.online.ParsedAnimeHttpSource
import okhttp3.Response

class OnePace : ParsedAnimeHttpSource() {

    override val name = "One Pace"
    override val baseUrl = "https://onepace.net"
    override val lang = "es"
    override val supportsLatest = false

    override fun popularAnimeRequest(page: Int) =
        GET("$baseUrl/es/watch")

    override fun popularAnimeParse(response: Response): AnimesPage {
        val document = response.asJsoup()

        val animeList = document.select("a[href*='/es/watch/']").map {
            SAnime.create().apply {
                title = it.text().trim()
                url = it.attr("href")
            }
        }

        return AnimesPage(animeList.distinctBy { it.url }, false)
    }

    override fun animeDetailsParse(response: Response): SAnime {
        val document = response.asJsoup()

        return SAnime.create().apply {
            title = document.select("h1").text()
            description = document.select("p").firstOrNull()?.text() ?: ""
            thumbnail_url = document.select("img").firstOrNull()?.absUrl("src")
        }
    }

    override fun episodeListParse(response: Response): List<SEpisode> {
        val document = response.asJsoup()
        val episodes = mutableListOf<SEpisode>()

        var mode = "SUB"

        document.select("h2, h3, a[href*='pixeldrain']").forEach { element ->

            val text = element.text().lowercase()

            if (text.contains("sub")) mode = "SUB"
            if (text.contains("doblaje")) mode = "LAT"

            if (element.tagName() == "a" && element.attr("href").contains("pixeldrain")) {

                val quality = when {
                    text.contains("1080") -> "1080p"
                    text.contains("720") -> "720p"
                    text.contains("480") -> "480p"
                    else -> "HD"
                }

                episodes.add(SEpisode.create().apply {
                    name = "${mode} - ${element.text()} [$quality]"
                    url = element.attr("href")
                })
            }
        }

        return episodes
    }

    override fun videoListParse(response: Response): List<Video> {
        val url = response.request.url.toString()

        val videoUrl = url
            .replace("/u/", "/api/file/")
            .replace("/l/", "/api/file/") + "?download"

        return listOf(
            Video(videoUrl, "Pixeldrain", videoUrl)
        )
    }

    override fun latestUpdatesRequest(page: Int) = throw UnsupportedOperationException()
    override fun latestUpdatesParse(response: Response) = throw UnsupportedOperationException()
    override fun searchAnimeRequest(page: Int, query: String, filters: AnimeFilterList) =
        throw UnsupportedOperationException()
    override fun searchAnimeParse(response: Response) = throw UnsupportedOperationException()
}
