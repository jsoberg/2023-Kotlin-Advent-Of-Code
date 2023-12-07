import StringParsing.findNumbers

// https://adventofcode.com/2023/day/6
fun main() {
    val input = readInput("Day06-input")
    val part1WaysToBeatRecord = Day06Part1.calculateNumWaysToBeatRecord(input)
    println("Part 1 - Number of Ways to Beat Record: $part1WaysToBeatRecord")
}

private object Day06Part1 {

    fun calculateNumWaysToBeatRecord(input: List<String>): Int {
        val records = RaceParser().parse(input)
        var numberOfWaysToBeatResult = 1
        records.forEach { record ->
            val numWaysToBeat = record.calculateNumberOfWaysToBeat()
            if (numWaysToBeat > 0) {
                numberOfWaysToBeatResult *= numWaysToBeat
            }
        }
        return numberOfWaysToBeatResult
    }
}

class RaceParser {

    fun parse(input: List<String>): List<RaceRecord> {
        val raceTimes = input.first { it.startsWith("Time") }
            .parseNumbers()
        val records = input.first { it.startsWith("Distance") }
            .parseNumbers()
        return raceTimes.mapIndexed { index, raceTime ->
            RaceRecord(
                raceTimeMs = raceTime,
                raceRecordDistanceMm = records[index],
            )
        }
    }

    private fun String.parseNumbers() =
        split(':')[1]
            .findNumbers()
            .map { it.toInt() }
}

data class RaceRecord(
    val raceTimeMs: Int,
    val raceRecordDistanceMm: Int,
) {
    fun calculateNumberOfWaysToBeat(): Int {
        var numWaysToBeat = 0
        for (startTime in 1 until raceTimeMs) {
            val timeLeftToRunRace = raceTimeMs - startTime
            val distanceFromStartTime = timeLeftToRunRace * startTime
            if (distanceFromStartTime > raceRecordDistanceMm) {
                numWaysToBeat++
            }
        }
        return numWaysToBeat
    }
}