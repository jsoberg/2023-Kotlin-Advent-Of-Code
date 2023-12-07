import StringParsing.findNumbers

// https://adventofcode.com/2023/day/6
fun main() {
    val input = readInput("Day06-input")
    val part1WaysToBeatRecord = Day06Part1.calculateNumWaysToBeatRecord(input)
    println("Part 1 - Number of Ways to Beat Record: $part1WaysToBeatRecord")

    val part2WaysToBeatRecord = Day06Part2.calculateNumWaysToBeatRecord(input)
    println("Part 2 - Number of Ways to Beat Combined Record: $part2WaysToBeatRecord")
}

private object Day06Part1 {

    fun calculateNumWaysToBeatRecord(input: List<String>): Long {
        val records = RaceParser().parsePart1(input)
        var numberOfWaysToBeatResult = 1L
        records.forEach { record ->
            val numWaysToBeat = record.calculateNumberOfWaysToBeat()
            if (numWaysToBeat > 0) {
                numberOfWaysToBeatResult *= numWaysToBeat
            }
        }
        return numberOfWaysToBeatResult
    }
}

private object Day06Part2 {

    fun calculateNumWaysToBeatRecord(input: List<String>): Long {
        val record = RaceParser().parsePart2(input)
        return record.calculateNumberOfWaysToBeat()
    }
}

class RaceParser {

    fun parsePart1(input: List<String>): List<RaceRecord> {
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
            .map { it.toLong() }

    fun parsePart2(input: List<String>): RaceRecord {
        val raceTimes = input.first { it.startsWith("Time") }
            .parseNumbers()
            .joinToString(separator = "")
            .toLong()
        val records = input.first { it.startsWith("Distance") }
            .parseNumbers()
            .joinToString(separator = "")
            .toLong()
        return RaceRecord(
            raceTimeMs = raceTimes,
            raceRecordDistanceMm = records,
        )
    }
}

data class RaceRecord(
    val raceTimeMs: Long,
    val raceRecordDistanceMm: Long,
) {
    fun calculateNumberOfWaysToBeat(): Long {
        var numWaysToBeat = 0L
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