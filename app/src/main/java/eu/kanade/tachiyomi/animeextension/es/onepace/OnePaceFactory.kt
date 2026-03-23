package eu.kanade.tachiyomi.animeextension.es.onepace

import eu.kanade.tachiyomi.animesource.AnimeSource
import eu.kanade.tachiyomi.animesource.AnimeSourceFactory

class OnePaceFactory : AnimeSourceFactory {
    override fun createSources(): List<AnimeSource> = listOf(
        OnePace()
    )
}
