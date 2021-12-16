fun main() {

    class Path(val coordinates: Pair<Int, Int>, val totalCost: Int = 0)

    fun findPathCosts(input: List<String>): Collection<Path> {
        val goal = Pair(input.size - 1, input.last().length - 1)
        val cheapestPaths = mutableMapOf<Pair<Int, Int>, Int>()
        var paths: Collection<Path> = listOf(Path(Pair(0, 0), 0))
        while (paths.any { it.coordinates != goal }) {
            paths = paths.flatMap { path ->
                if (path.coordinates == goal) listOf(path) else
                    listOf(
                        Pair(path.coordinates.first, path.coordinates.second + 1),
                        Pair(path.coordinates.first + 1, path.coordinates.second),
                        Pair(path.coordinates.first, path.coordinates.second - 1),
                        Pair(path.coordinates.first - 1, path.coordinates.second)
                    )
                        .filter { it.first <= goal.first && it.second <= goal.second && it.first >= 0 && it.second >= 0 }
                        .map {
                            Path(
                                it,
                                path.totalCost + input[it.first][it.second].digitToInt()
                            )
                        }
                        .filter { !cheapestPaths.containsKey(it.coordinates) || it.totalCost < cheapestPaths[it.coordinates]!! }
            }

            cheapestPaths.putAll(paths.groupBy { it.coordinates }
                .mapValues { (_, value) -> value.minOf { it.totalCost } })
            paths = paths.groupingBy { it.coordinates }
                .reduce { key, acc, e -> if (acc.totalCost > e.totalCost) e else acc }.values
        }

        return paths
    }

    fun part1(input: List<String>): Int {
        return findPathCosts(input).minOf { it.totalCost }
    }

    fun increaseRisk(risk: Char): Char {
        return (if (risk.digitToInt() < 9) risk.digitToInt() + 1 else 1).digitToChar()
    }

    fun computeRiskToTheRight(initialLine: String): String {
        var tile = initialLine.toList()
        val line = tile.toMutableList()

        for (i in 1..4) {
            tile = tile.map { increaseRisk(it) }
            line.addAll(tile)
        }

        return line.joinToString("")
    }

    fun computeRiskDown(input: List<String>): List<String> {
        var tile = input
        val tallCave = tile.toMutableList()

        for (i in 1..4) {
            tile = tile.map { it.map { char -> increaseRisk(char) }.joinToString("") }
            tallCave.addAll(tile)
        }

        return tallCave
    }

    fun part2(input: List<String>): Int {
        val bigCave = computeRiskDown(input)
            .map { computeRiskToTheRight(it) }

        return findPathCosts(bigCave).minOf { it.totalCost }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 40)

    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}
