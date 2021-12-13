fun main() {
    fun foldY(
        dots: List<Pair<Int, Int>>,
        foldValue: Int
    ) = dots.filter { it.second != foldValue }
        .map {
            if (it.second < foldValue) it else Pair(
                it.first,
                it.second - ((it.second - foldValue) * 2)
            )
        }.distinct()

    fun foldX(
        dots: List<Pair<Int, Int>>,
        foldValue: Int
    ) = dots.filter { it.first != foldValue }
        .map {
            if (it.first < foldValue) it else Pair(
                it.first - ((it.first - foldValue) * 2),
                it.second
            )
        }.distinct()

    fun dotsFrom(input: List<String>) = input.filter { it.contains(",") }
        .map { it.split(",") }
        .map { Pair(it[0].toInt(), it[1].toInt()) }

    fun foldsFrom(input: List<String>) = input.filter { it.startsWith("fold along ") }
        .map { it.substring("fold along ".length).split("=") }
        .map { Pair(it[0], it[1].toInt()) }

    fun fold(
        fold: Pair<String, Int>,
        dots: List<Pair<Int, Int>>
    ): List<Pair<Int, Int>> {
        var dots1 = dots
        val foldValue = fold.second
        dots1 = if (fold.first == "y") {
            foldY(dots1, foldValue)
        } else {
            foldX(dots1, foldValue)
        }
        return dots1
    }

    /**
     * How many dots after one fold
     */
    fun part1(input: List<String>): Int {
        return fold(foldsFrom(input).first(), dotsFrom(input)).size
    }

    fun part2(input: List<String>): String {
        var dots = dotsFrom(input)

        for (fold in foldsFrom(input)) {
            dots = fold(fold, dots)
        }

        val xMax = dots.maxOf { it.first }
        val yMax = dots.maxOf { it.second }

        var line = ""
        for (y in 0..yMax) {
            for (x in 0..xMax) {
                line += if (dots.contains(Pair(x, y))) "#" else "."
            }
            line += "\n"
        }

        return line
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 17)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
