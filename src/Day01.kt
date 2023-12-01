fun main() {
    val input = readInput("Day01-input")

    val part1Calibration = Day01Part1.calculateTrebuchetCalibration(input)
    println("Part 1 - Trebuchet Calculation: $part1Calibration")

    val part2Calibration = Day01Part2.calculateTrebuchetCalibration(input)
    println("Part 2 - Trebuchet Calculation with English Values: $part2Calibration")
}

private object Day01Part1 {
    fun calculateTrebuchetCalibration(input: List<String>): Int {
        return input.mapNotNull { garbledValue ->
            if (garbledValue.isNotEmpty() && garbledValue != "\n") {
                parseGarbledValue(garbledValue)
            } else {
                null
            }
        }.sum()
    }

    fun parseGarbledValue(garbledValue: String): Int {
        val parsedIntString = garbledValue.replace(Regexes.NonDigit, "")
        return when (parsedIntString.length) {
            // If there's only one digit, duplicate it (since first and last are equal).
            1 -> "$parsedIntString$parsedIntString".toInt()

            else -> {
                "${parsedIntString.first()}${parsedIntString.last()}".toInt()
            }
        }
    }
}

private object Day01Part2 {

    private val DigitEnglishStringToIntValueMap =
            mapOf(
                    "one" to 1,
                    "two" to 2,
                    "three" to 3,
                    "four" to 4,
                    "five" to 5,
                    "six" to 6,
                    "seven" to 7,
                    "eight" to 8,
                    "nine" to 9,
            )

    fun calculateTrebuchetCalibration(input: List<String>): Int {
        var runningSum = 0
        for (line in input) {
            runningSum += calculateLineCalibration(line)
        }
        return runningSum
    }

    private fun calculateLineCalibration(line: String): Int {
        val occurrences = mutableListOf<NumericalOccurrence>()
        for (entry in DigitEnglishStringToIntValueMap) {
            // Try parsing the english numerical form.
            occurrences.addAll(
                    parseStringValueOccurrences(
                            originalString = line,
                            searchForString = entry.key,
                            matchingNumericalValue = entry.value,
                    )
            )

            // Then, try parsing the digit itself.
            occurrences.addAll(
                    parseStringValueOccurrences(
                            originalString = line,
                            searchForString = entry.value.toString(),
                            matchingNumericalValue = entry.value,
                    )
            )
        }
        
        return when {
            occurrences.isEmpty() -> 0
            // Edge case where first and last digit are the same.
            occurrences.size == 1 -> {
                val digit = occurrences.first().numericalValue
                "$digit$digit".toInt()
            }

            else -> {
                occurrences.sortBy { it.indexInString }
                val first = occurrences.first().numericalValue
                val last = occurrences.last().numericalValue
                "$first$last".toInt()
            }
        }
    }

    private fun parseStringValueOccurrences(
            originalString: String,
            searchForString: String,
            matchingNumericalValue: Int,
    ): List<NumericalOccurrence> {
        val occurrences = mutableListOf<NumericalOccurrence>()

        var startIndex = 0
        while (startIndex < originalString.length - 1) {
            when (val index = originalString.indexOf(searchForString, startIndex)) {
                -1 -> return occurrences
                else -> {
                    occurrences.add(
                            NumericalOccurrence(
                                    indexInString = index,
                                    numericalValue = matchingNumericalValue,
                            )
                    )
                    startIndex = index + searchForString.length
                }
            }
        }

        return occurrences
    }

    private data class NumericalOccurrence(
            val indexInString: Int,
            val numericalValue: Int,
    )
}