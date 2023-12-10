import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("input/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
        .toString(16)
        .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

object Regexes {
    val NonDigit = "[^0-9]".toRegex()
    val Number = "[0-9]+".toRegex()
}

object StringParsing {
    fun String.findNumbers(): List<String> =
            Regexes.Number
                    .findAll(this)
                    .map(MatchResult::value)
                    .toList()

    fun String.parseNumber(): Int =
            replace(Regexes.NonDigit, "")
                    .toInt()
}

object Matrices {
    fun Array<IntArray>.printMatrix() {
        forEach { row ->
            row.joinToString(",").println()
        }
    }
}
