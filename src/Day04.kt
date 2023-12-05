import StringParsing.findNumbers
import StringParsing.parseNumber
import kotlin.math.pow

// https://adventofcode.com/2023/day/4
fun main() {
    val input = readInput("Day04-input")
    val part1ScratchcardPoints = Day04Part1.calculateScratchPoints(input)
    println("Part 1 - Scratchcard points: $part1ScratchcardPoints")
}

private object Day04Part1 {

    fun calculateScratchPoints(input: List<String>): Int {
        val scratchcards = ScratchcardParser()
            .parse(input)
        return scratchcards.sumOf(Scratchcard::calculatePoints)
    }
}

class ScratchcardParser {

    fun parse(input: List<String>): List<Scratchcard> =
        input.map(::parseLine)

    private fun parseLine(line: String): Scratchcard {
        val cardIdToNumbersSplit = line.split(':')
        val cardId = cardIdToNumbersSplit[0].parseNumber()

        val numbersSplit = cardIdToNumbersSplit[1].split('|')
        val winningNumbers = numbersSplit[0].mapToIntHashSet()
        val scratchedNumbers = numbersSplit[1].mapToIntHashSet()
        return Scratchcard(
            id = cardId,
            winningNumbers = winningNumbers,
            scratchedNumbers = scratchedNumbers,
        )
    }

    private fun String.mapToIntHashSet() =
        findNumbers()
            .map { it.toInt() }
            .toHashSet()

}

data class Scratchcard(
    val id: Int,
    val winningNumbers: HashSet<Int>,
    val scratchedNumbers: HashSet<Int>,
) {
    fun calculatePoints(): Int {
        var numWinners = 0
        scratchedNumbers.forEach { scratched ->
            if (winningNumbers.contains(scratched)) {
                numWinners++
            }
        }
        return if (numWinners > 0) {
            2.0.pow((numWinners - 1).toDouble()).toInt()
        } else 0
    }
}