package org.nmccarra1.url.shortener.utils

/**
 * functions which generate and validate IDs base64
 */
object IdManager {
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf('.', '-')
    private const val idLength = 6

    fun generate(): String =
        (1..idLength)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")

    fun isValidId(id: String) : Boolean {
        return when {
            id.length != 6 -> false
            id.toList().filter { charPool.contains(it) }.size != 6 -> false
            else -> true
        }
    }
}