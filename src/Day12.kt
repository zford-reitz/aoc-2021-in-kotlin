fun main() {

    fun part1(input: List<String>): Int {
        val edges = input.map { it.split("-") }
        val nodes = edges.flatten().toSet()

        val caveMap = nodes.filter { it != "end" }
            .groupBy(keySelector = { node -> node }, valueTransform = { node ->
                edges.filter { it.contains(node) }
                    .flatten()
                    .filter { it != node && it != "start" }
            }).mapValues { it.value.flatten() }

        fun progress(path: List<String>): List<List<String>> {
            val nextNodes = caveMap[path.last()] ?: return listOf(path)

            return nextNodes.filter { it.lowercase() != it || !path.contains(it) }.map { path.plus(it) }
        }

        var paths = caveMap["start"]!!.map { listOf(it) }
        while (paths.any { it.last() != "end" }) {
            paths = paths.flatMap { progress(it) }
        }

        return paths.size
    }

    fun part2(input: List<String>): Int {
        val edges = input.map { it.split("-") }
        val nodes = edges.flatten().toSet()

        val caveMap = nodes.filter { it != "end" }
            .groupBy(keySelector = { node -> node }, valueTransform = { node ->
                edges.filter { it.contains(node) }
                    .flatten()
                    .filter { it != node && it != "start" }
            }).mapValues { it.value.flatten() }

        fun isLowerCase(s: String): Boolean {
            return s.lowercase() == s
        }

        fun hasNoDuplicateSmallCaves(path: List<String>) =
            path.filter { isLowerCase(it) }.groupingBy { it }.eachCount()
                .all { it.value < 2 }

        fun progress(path: List<String>): List<List<String>> {
            val nextNodes = caveMap[path.last()] ?: return listOf(path)

            return nextNodes.filter {
                hasNoDuplicateSmallCaves(path) || !(isLowerCase(it) && path.contains(it))
            }.map { path.plus(it) }
        }

        var paths = caveMap["start"]!!.map { listOf(it) }
        while (paths.any { it.last() != "end" }) {
            paths = paths.flatMap { progress(it) }
        }

        return paths.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 10)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

