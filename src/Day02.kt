// https://adventofcode.com/2023/day/2
fun main() {
    val input = readInput("Day02-input")
    val part1NumPossibleGamesIdSum = Day02Part1.calculatePossibleGamesIdSum(input)
    println("Part 1 - Possible Games ID Sum: $part1NumPossibleGamesIdSum")

    val part2MinimumBagCubePowerSum = Day02Part2.calculateMinimumBagCubePower(input)
    println("Part 2 - Minimum Bag Power Sum: $part2MinimumBagCubePowerSum")
}

private object Day02Part1 {

    private val Part1Bag = hashMapOf(
            Cube.Color.Red to 12,
            Cube.Color.Green to 13,
            Cube.Color.Blue to 14,
    )

    fun calculatePossibleGamesIdSum(input: List<String>): Int {
        val parser = GameParser()
        var runningIdSum = 0
        input.forEach { line ->
            val game = parser.parseGameFromLine(line)
            if (game.isPossible(Part1Bag)) {
                runningIdSum += game.id
            }
        }
        return runningIdSum
    }
}

private object Day02Part2 {

    fun calculateMinimumBagCubePower(input: List<String>): Int {
        val parser = GameParser()
        var runningPowerSum = 0
        input.forEach { line ->
            val game = parser.parseGameFromLine(line)
            runningPowerSum += game.calculateMinimumRequiredBagPower()
        }
        return runningPowerSum
    }
}

class GameParser {

    fun parseGameFromLine(line: String): Game {
        val idToSetSplit = line.split(":")
        val gameId = idToSetSplit[0]
                .substringAfter("Game ")
                .toInt()

        val setsSplit = idToSetSplit[1].split(';')
        val bagPulls = parseGameBagPulls(setsSplit)
        return Game(
                id = gameId,
                bagPulls = bagPulls,
        )
    }

    // Expected format for split e.g. [ 3 blue, 4 red,  1 red, 2 green, 6 blue,  2 green]
    private fun parseGameBagPulls(setsSplit: List<String>): List<Game.BagPull> =
            setsSplit.map { set ->
                val cubesSplit = set.trim()
                        .split(',')
                parseBagPull(cubesSplit)
            }

    // Expected format for split e.g. [3 blue,  4 red]
    private fun parseBagPull(cubesSplit: List<String>): Game.BagPull {
        val map = cubesSplit.associate { cube ->
            val numToColorSplit = cube.trim()
                    .split(' ')
            val numCubes = numToColorSplit[0].toInt()
            val colorString = numToColorSplit[1].trim()
            val color = Cube.Color.fromString(colorString)
            color to numCubes
        }
        return Game.BagPull(map)
    }

}

data class Game(
        val id: Int,
        val bagPulls: List<BagPull>,
) {

    fun isPossible(bag: Map<Cube.Color, Int>): Boolean {
        bagPulls.forEach { pull ->
            pull.forEach { (color, numCubes) ->
                val numCubesInBag = bag[color] ?: 0
                if (numCubesInBag < numCubes) {
                    return false
                }
            }
        }
        return true
    }

    fun calculateMinimumRequiredBagPower(): Int {
        val bag = findMinimumRequiredBag()
        var power = 1
        bag.forEach { (_, minNumCubes) ->
            power *= minNumCubes
        }
        return power
    }

    private fun findMinimumRequiredBag(): Map<Cube.Color, Int> {
        val bag = hashMapOf<Cube.Color, Int>().apply {
            Cube.Color.values().forEach { color ->
                put(color, 0)
            }
        }

        bagPulls.forEach { pull ->
            pull.forEach { (color, numCubes) ->
                val minimumCubesInBag = bag[color] ?: 0
                if (numCubes > minimumCubesInBag) {
                    bag[color] = numCubes
                }
            }
        }
        return bag
    }

    data class BagPull(
            val cubeMap: Map<Cube.Color, Int>,
    ) : Map<Cube.Color, Int> by cubeMap
}

object Cube {

    enum class Color(val stringRep: String) {
        Red("red"),
        Blue("blue"),
        Green("green");

        companion object {
            fun fromString(stringRep: String) =
                    values().firstOrNull { it.stringRep == stringRep }
                            ?: error("$stringRep not a valid cube color")
        }
    }
}
