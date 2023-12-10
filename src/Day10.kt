// https://adventofcode.com/2023/day/10
fun main() {
    val input = readInput("Day10-input")
    val part1StepsToFarthestPosition = Day10Part1.calculateStepsToFarthestPosition(input)
    println("Part 1 - Steps to Farthest Position in Maze: $part1StepsToFarthestPosition")
}

private object Day10Part1 {

    // Assumes that there's only one loop in the input.
    fun calculateStepsToFarthestPosition(input: List<String>): Int {
        val maze = MazeParser().parse(input)
        val stepGrid = MazeTraverser(maze).calculateStepLoopGrid(maze)
        return (farthestStep(stepGrid) / 2)
    }

    private fun farthestStep(stepGrid: Array<IntArray>): Int {
        var farthest = -1
        stepGrid.forEach { row ->
            row.forEach { step ->
                if (step > farthest) {
                    farthest = step
                }
            }
        }
        // Add 1 to include the step back to into the initial place.
        return farthest + 1
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

class MazeTraverser(
        private val maze: Maze,
) {

    fun calculateStepLoopGrid(maze: Maze): Array<IntArray> {
        val stepGrid = Array(size = maze.rows) {
            IntArray(size = maze.columns).apply {
                fill(UntraversedValue)
            }
        }
        val (startRow, startCol) = maze.startIndex
        stepGrid[startRow][startCol] = 0

        var currentPosition = maze.startIndex
        var currentStep = 0
        var direction = findMovableDirection(
                index = maze.startIndex,
                results = stepGrid,
        )
        while (direction != null) {
            val (fromRow, fromtCol) = currentPosition
            val destRow = fromRow + direction.relativeRow
            val destCol = fromtCol + direction.relativeCol
            currentStep++
            stepGrid[destRow][destCol] = currentStep

            currentPosition = destRow to destCol
            direction = findMovableDirection(
                    index = currentPosition,
                    results = stepGrid,
            )
        }
        return stepGrid
    }

    private fun findMovableDirection(
            index: Pair<Int, Int>,
            results: Array<IntArray>,
    ): Maze.Direction? {
        val (startRow, startCol) = index
        for (direction in Maze.Direction.entries) {
            val isWithinBounds = isWithinBounds(
                    from = index,
                    maze = maze,
                    direction = direction,
            )
            // Short-circuit if we're going to try and go outside the bounds of the Maze.
            if (!isWithinBounds) {
                continue
            }

            val destRow = startRow + direction.relativeRow
            val destCol = startCol + direction.relativeCol
            val currentDestVal = results[destRow][destCol]
            // Short-circuit if we're trying to traverse a pipe that's already been traversed.
            if (currentDestVal != UntraversedValue) {
                continue
            }

            val canMove = canEnter(
                    from = index,
                    maze = maze,
                    direction = direction,
            )
            if (canMove) {
                return direction
            }
        }
        return null
    }

    private fun isWithinBounds(
            from: Pair<Int, Int>,
            maze: Maze,
            direction: Maze.Direction,
    ): Boolean {
        val (fromRow, fromCol) = from
        val destRow = fromRow + direction.relativeRow
        val destCol = fromCol + direction.relativeCol
        val outOfRowBounds = destRow < 0 || destRow >= maze.rows
        val outOfColBounds = destCol < 0 || destCol >= maze.columns
        return (!outOfRowBounds && !outOfColBounds)
    }

    private fun canEnter(
            from: Pair<Int, Int>,
            maze: Maze,
            direction: Maze.Direction,
    ): Boolean {
        val (fromRow, fromCol) = from
        val fromPipe = maze[fromRow][fromCol]
        val destRow = fromRow + direction.relativeRow
        val destCol = fromCol + direction.relativeCol

        val destPipe = maze[destRow][destCol]
        return fromPipe.canMoveTo(direction, destPipe)
    }

    companion object {
        const val UntraversedValue = -1
    }
}

data class Maze(
        val startIndex: Pair<Int, Int>,
        private val pipeGrid: List<List<Pipe>>,
) : List<List<Maze.Pipe>> by pipeGrid {

    val rows
        get() = this.size
    val columns
        get() = this[0].size

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

        fun canMoveInDirection(direction: Direction) = when (direction) {
            Direction.North -> canMoveNorth
            Direction.South -> canMoveSouth
            Direction.West -> canMoveWest
            Direction.East -> canMoveEast
        }

        private val canMoveNorth: Boolean
            get() = when (this) {
                Vertical,
                Start,
                NorthToWest90DegreeBend,
                NorthToEast90DegreeBend,
                -> true

                SouthToWest90DegreeBend,
                SouthToEast90DegreeBend,
                Horizontal,
                Ground -> false
            }

        private val canMoveSouth: Boolean
            get() = when (this) {
                Vertical,
                Start,
                SouthToWest90DegreeBend,
                SouthToEast90DegreeBend,
                -> true

                NorthToWest90DegreeBend,
                NorthToEast90DegreeBend,
                Horizontal,
                Ground -> false
            }

        private val canMoveWest: Boolean
            get() = when (this) {
                Horizontal,
                Start,
                SouthToWest90DegreeBend,
                NorthToWest90DegreeBend,
                -> true

                SouthToEast90DegreeBend,
                NorthToEast90DegreeBend,
                Vertical,
                Ground -> false
            }

        private val canMoveEast: Boolean
            get() = when (this) {
                Horizontal,
                Start,
                SouthToEast90DegreeBend,
                NorthToEast90DegreeBend,
                -> true

                SouthToWest90DegreeBend,
                NorthToWest90DegreeBend,
                Vertical,
                Ground -> false
            }

        fun canMoveTo(
                direction: Direction,
                destPipe: Pipe,
        ): Boolean =
                (canMoveInDirection(direction) && destPipe.canMoveInDirection(direction.opposite))

        companion object {
            private val CharToPipeMap: Map<Char, Pipe> =
                    Pipe.entries.associateBy { it.charRep }

            fun fromChar(char: Char) = CharToPipeMap[char] ?: error("$char not a valid pipe")
        }
    }

    enum class Direction(
            val relativeRow: Int,
            val relativeCol: Int,
    ) {
        North(-1, 0),
        East(0, +1),
        South(+1, 0),
        West(0, -1);

        val opposite: Direction
            get() = when (this) {
                North -> South
                South -> North
                West -> East
                East -> West
            }
    }
}