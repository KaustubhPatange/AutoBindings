package com.kpstv.sample_ktx

import java.text.MessageFormat
import kotlin.random.Random

object Utils {

    private val tags = listOf(
        "background", "wallpaper", "halloween", "nature", "food", "christmas", "business", "autumn", "office", "money", "fall", "computer", "love"
    )

    private fun random(start: Int, end: Int): Int {
        return start + Random.nextInt(end - start + 1)
    }

    fun createRandomImageUrl(): String {
        val landscape = Random.nextBoolean()
        val endpoint = Random.nextBoolean()

        val width = random(300, 400)
        val height = random(200, 300)

        return MessageFormat.format(
            if (endpoint)
                "https://lorempixel.com/{0}/{1}/"
            else
                "https://picsum.photos/{0}/{1}/",
            if (landscape) width else height, if (landscape) height else width
        )
    }

    fun createRandomTag(): List<String> {
        val first = Random.nextInt(tags.size - 1)
        val second = Random.nextInt(tags.size - 1)
        return tags.subList(minOf(first, second), maxOf(first, second))
    }
}