fun main() {
    val trebuchetCalibration = Day01Part1.calculateTrebuchetCalibration()
    println("Part 1 - Trebuchet Calculation: $trebuchetCalibration")
}

private object Day01Part1 {
    fun calculateTrebuchetCalibration(): Int {
        val input = readInput("Day01-input")
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