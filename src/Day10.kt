// https://adventofcode.com/2023/day/6
fun main() {
    val input = readInput("Day10-test")
    val part1StepsToFarthestPosition = Day10Part1.calculateStepsToFarthestPosition(input)
    println("Part 1 - Steps to Farthest Position in Maze: $part1StepsToFarthestPosition")
}

private object Day10Part1 {

    fun calculateStepsToFarthestPosition(input: List<String>): Int {
        val maze = MazeParser().parse(input)
        // TODO
        return 0
    }
}

class MazeParser {

    fun parse(input: List<String>): Maze {
        val grid = ArrayList<List<Maze.Pipe>>(input.size)
        var startIndex: Pair<Int, Int>? = null

        input.forEachIndexed { i, line ->
            val row = ArrayList<Maze.Pipe>(line.length)
            line.forEachIndexed { j, char ->
                val pipe = Maze.Pipe.fromChar(char)
                row.add(pipe)
                if (pipe == Maze.Pipe.Start) {
                    if (startIndex != null) {
                        error("Multiple starting positions in Maze, found second at ($i,$j)")
                    }
                    startIndex = i to j
                }
            }
            grid.add(row)
        }
        return Maze(
            startIndex = startIndex ?: error("No starting point found in input"),
            pipeGrid = grid,
        )
    }

}

data class Maze(
    private val startIndex: Pair<Int, Int>,
    private val pipeGrid: List<List<Pipe>>,
) : List<List<Maze.Pipe>> by pipeGrid {

    fun printMaze() {
        pipeGrid.forEach { row ->
            val rowString = row.joinToString(
                separator = "",
                transform = { it.charRep.toString() }
            )
            println(rowString)
        }
    }

    enum class Pipe(val charRep: Char) {
        Vertical('|'),
        Horizontal('-'),
        NorthToEast90DegreeBend('L'),
        NorthToWest90DegreeBend('J'),
        SouthToWest90DegreeBend('7'),
        SouthToEast90DegreeBend('F'),
        Ground('.'),
        Start('S');

        companion object {
            private val CharToPipeMap: Map<Char, Pipe> =
                Pipe.values()
                    .associateBy { it.charRep }

            fun fromChar(char: Char) = CharToPipeMap[char] ?: error("$char not a valid pipe")
        }
    }
}