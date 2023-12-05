import StringParsing.findNumbers
import kotlin.math.max
import kotlin.math.min

// https://adventofcode.com/2023/day/3
fun main() {
    val input = readInput("Day03-input")

    val part1PartNumberSum = Day03Part1.calculateEngineSchematicSum(input)
    println("Part 1 - Valid Engine Part Number Sum: $part1PartNumberSum")
}

private object Day03Part1 {

    fun calculateEngineSchematicSum(input: List<String>): Int {
        var runningPartNumberSum = 0
        input.forEachIndexed { index, line ->
            val numbers = line.findNumbers()
            var numberStartCheckIndex = 0
            numbers.forEach { numberString ->
                val numberIndex =
                    input[index].indexOf(numberString, startIndex = numberStartCheckIndex)
                numberStartCheckIndex = numberIndex + numberString.length
                val isValid = isValidPartNumber(
                    numberString = numberString,
                    numberStartIndex = numberIndex,
                    lineIndex = index,
                    input = input,
                )
                if (isValid) {
                    runningPartNumberSum += numberString.toInt()
                }
            }
        }

        return runningPartNumberSum
    }

    private fun isValidPartNumber(
        numberString: String,
        numberStartIndex: Int,
        lineIndex: Int,
        input: List<String>,
    ): Boolean {
        val startIndex = input[lineIndex].indexOf(numberString, startIndex = numberStartIndex)
        val endIndex = startIndex + numberString.length

        val aboveLine = if (lineIndex > 0) {
            input[lineIndex - 1]
        } else null
        val belowLine = if (lineIndex < input.size - 1) {
            input[lineIndex + 1]
        } else null

        return containsAdjacentSymbol(
            lineToCheck = input[lineIndex].trim(),
            startIndex = startIndex,
            endIndex = endIndex,
            aboveLine = aboveLine?.trim(),
            belowLine = belowLine?.trim(),
        )
    }

    private fun containsAdjacentSymbol(
        lineToCheck: String,
        startIndex: Int,
        endIndex: Int,
        aboveLine: String?,
        belowLine: String?
    ): Boolean {
        val startCheckIndex = max((startIndex - 1), 0)
        val endCheckIndex = min(endIndex, lineToCheck.length - 1)
        // Check to the left and right of the number in the same line.
        if (lineToCheck[startCheckIndex].isSymbol() || lineToCheck[endCheckIndex].isSymbol()) {
            return true
        }

        // Check lines above and below the number.
        for (i in startCheckIndex..endCheckIndex) {
            if (aboveLine?.get(i)?.isSymbol() == true) {
                return true
            }
            if (belowLine?.get(i)?.isSymbol() == true) {
                return true
            }
        }
        return false
    }

    private fun Char.isSymbol(): Boolean =
        this != '.' && !isDigit()
}