// https://adventofcode.com/2023/day/2
fun main() {
    val input = readInput("Day02-input")
    val part1NumPossibleGamesIdSum = Day02Part1.calculatePossibleGamesIdSum(input)
    println("Part 1 - Possible Games ID Sum: $part1NumPossibleGamesIdSum")
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

    fun isPossible(bag: HashMap<Cube.Color, Int>): Boolean {
        bagPulls.forEach { map ->
            map.forEach { (color, numCubes) ->
                val numCubesInBag = bag[color] ?: 0
                if (numCubesInBag < numCubes) {
                    return false
                }
            }
        }
        return true
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
