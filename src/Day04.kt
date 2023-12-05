import StringParsing.findNumbers
import StringParsing.parseNumber
import kotlin.math.pow

// https://adventofcode.com/2023/day/4
fun main() {
    val input = readInput("Day04-input")
    val part1ScratchcardPoints = Day04Part1.calculateScratchPoints(input)
    println("Part 1 - Scratchcard points: $part1ScratchcardPoints")

    val part2NumberOfScratchcards = Day04Part2.calculateNumScratchCards(input)
    println("Part 2 - Number of Scratchcards: $part2NumberOfScratchcards")
}

private object Day04Part1 {

    fun calculateScratchPoints(input: List<String>): Int {
        val scratchcards = ScratchcardParser()
            .parse(input)
        return scratchcards.sumOf(Scratchcard::calculatePoints)
    }
}

private object Day04Part2 {

    fun calculateNumScratchCards(input: List<String>): Int {
        val scratchcards = ScratchcardParser()
            .parse(input)
        return ScratchcardCollector(scratchcards)
            .calculateTotalNumberOfScratchcards()
    }

    class ScratchcardCollector(
        private val scratchcards: List<Scratchcard>,
    ) {
        private val idToScratchcardMap = scratchcards.associateBy { it.id }

        fun calculateTotalNumberOfScratchcards(): Int {
            val wonCopyIds = mutableListOf<Int>()
            scratchcards.forEach { scratchcard ->
                wonCopyIds.addAll(collectWonCopies(scratchcard))
            }
            return scratchcards.size + wonCopyIds.size
        }

        private fun collectWonCopies(scratchcard: Scratchcard): List<Int> {
            val wonCopyIds = mutableListOf<Int>()
            val numWinners = scratchcard.numWinners()
            if (numWinners > 0) {
                for (id in (scratchcard.id + 1)..(scratchcard.id + numWinners)) {
                    wonCopyIds.add(id)
                    idToScratchcardMap[id]?.let { copiedScratchcard ->
                        wonCopyIds.addAll(collectWonCopies(copiedScratchcard))
                    }
                }
            }
            return wonCopyIds
        }
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
        val numWinners = numWinners()
        return if (numWinners > 0) {
            2.0.pow((numWinners - 1).toDouble()).toInt()
        } else 0
    }

    fun numWinners(): Int {
        var numWinners = 0
        scratchedNumbers.forEach { scratched ->
            if (winningNumbers.contains(scratched)) {
                numWinners++
            }
        }
        return numWinners
    }
}