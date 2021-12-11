fun main() {
    fun computeOctopi(input: List<String>): List<Octopus> {
        val octopi = input.map { it.toList().mapNotNull(Char::digitToIntOrNull).map(::Octopus) }

        for (y in octopi.indices) {
            for (x in octopi[y].indices) {
                val octopus = octopi[y][x]

                for (neighborY in y - 1..y + 1) {
                    if (0 <= neighborY && neighborY < octopi.size) {
                        for (neighborX in x - 1..x + 1) {
                            if (0 <= neighborX && neighborX < octopi[y].size) {
                                val neighborOctopus = octopi[neighborY][neighborX]
                                if (neighborOctopus != octopus) {
                                    octopus.neighbors.add(neighborOctopus)
                                }
                            }
                        }
                    }
                }
            }
        }

        return octopi.flatten()
    }

    fun part1(input: List<String>): Int {
        val octopi = computeOctopi(input)
        var flashes = 0
        repeat(100) {
            octopi.forEach(Octopus::step)
            flashes += octopi.count(Octopus::hasFlashedThisStep)
            octopi.forEach(Octopus::endStep)
        }

        return flashes
    }

    fun part2(input: List<String>): Int {
        val octopi = computeOctopi(input)
        var allFlashedSimultaneously = false
        var stepNumber = 0
        while (!allFlashedSimultaneously) {
            octopi.forEach(Octopus::step)
            allFlashedSimultaneously = octopi.all(Octopus::hasFlashedThisStep)
            octopi.forEach(Octopus::endStep)
            stepNumber++
        }

        return stepNumber
    }

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}

class Octopus(var energy: Int, val neighbors: MutableSet<Octopus> = mutableSetOf()) {
    private var hasFlashedThisStep = false

    fun hasFlashedThisStep() = hasFlashedThisStep

    fun step() {
        increaseEnergy()
    }

    fun endStep() {
        if (hasFlashedThisStep()) {
            hasFlashedThisStep = false
            energy = 0
        }
    }

    private fun flash() {
        if (!hasFlashedThisStep()) {
            hasFlashedThisStep = true
            neighbors.forEach { it.increaseEnergy() }
        }
    }

    private fun increaseEnergy() {
        energy++
        if (energy > 9) {
            flash()
        }
    }

    override fun toString(): String {
        return energy.toString()
    }
}