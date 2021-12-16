fun main() {

    fun asBinaryText(input: List<String>) = input.first()
        .map { it.digitToInt(16) }
        .joinToString("") { it.toString(2).padStart(4, '0') }


    fun part1(input: List<String>): Int {
        return readPacket(asBinaryText(input).iterator())
            .flatten()
            .sumOf { it.version }
    }

    fun part2(input: List<String>): Long {
        return readPacket(asBinaryText(input).iterator())
            .evaluate()
    }

    check(asBinaryText(readInput("Day16_test1")) == "110100101111111000101000")
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 16)
    check(part2(readInput("Day16_test2")) == 1L)

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))

}

const val LITERAL_TYPE_ID = 4

abstract class Packet(val version: Int, val typeId: Int) {
    fun flatten(): List<Packet> = listOf(this) + subPackets().map { it.flatten() }.flatten()

    abstract fun subPackets(): List<Packet>

    abstract fun evaluate(): Long

}

class LiteralPacket(version: Int, private val value: Long) : Packet(version, LITERAL_TYPE_ID) {
    override fun subPackets(): List<Packet> {
        return listOf()
    }

    override fun evaluate(): Long {
        return value
    }

    override fun toString(): String {
        return value.toString()
    }
}

class OperatorPacket(version: Int, typeId: Int, val subPackets: List<Packet>) : Packet(version, typeId) {
    override fun subPackets(): List<Packet> {
        return subPackets
    }

    override fun evaluate(): Long {
        return when (typeId) {
            0 -> subPackets.sumOf { it.evaluate() }
            1 -> subPackets.map { it.evaluate() }.reduce { acc, l -> acc * l }
            2 -> subPackets.minOf { it.evaluate() }
            3 -> subPackets.maxOf { it.evaluate() }
            5 -> if (subPackets[0].evaluate() > subPackets[1].evaluate()) 1 else 0
            6 -> if (subPackets[0].evaluate() < subPackets[1].evaluate()) 1 else 0
            7 -> if (subPackets[0].evaluate() == subPackets[1].evaluate()) 1 else 0
            else -> throw RuntimeException()
        }
    }

    override fun toString(): String {
        return when (typeId) {
            0 -> "(${subPackets.joinToString(" + ")})"
            1 -> "(${subPackets.joinToString(" * ")})"
            2 -> "min(${subPackets.joinToString(", ")})"
            3 -> "max(${subPackets.joinToString(", ")})"
            5 -> "(${subPackets.joinToString(" > ")})"
            6 -> "(${subPackets.joinToString(" < ")})"
            7 -> "(${subPackets.joinToString(" == ")})"
            else -> throw RuntimeException()
        }
    }
}

data class PacketHeader(val version: Int, val typeId: Int)

fun readLiteralPacket(version: Int, iter: CharIterator): LiteralPacket {
    val groups = mutableListOf<String>()
    do {
        val group = next(iter, 5)
        groups.add(group.drop(1))
    } while (group.first() == '1')

    val literalValue = groups.joinToString("").toLong(2)
    return LiteralPacket(version, literalValue)
}

fun readOperatorPacket(version: Int, typeId: Int, iter: CharIterator): OperatorPacket {
    val subPackets = mutableListOf<Packet>()
    if (iter.next() == '0') {
        // the next 15 bits are a number that represents the total length in bits of the sub-packets
        val subPacketsLengthInBits = next(iter, 15).toInt(2)
        val subPacketsIter = next(iter, subPacketsLengthInBits).iterator()
        while (subPacketsIter.hasNext()) {
            subPackets.add(readPacket(subPacketsIter))
        }
    } else {
        // the next 11 bits are a number that represents the number of sub-packets
        val numberOfSubPackets = next(iter, 11).toInt(2)
        repeat(numberOfSubPackets) {
            subPackets.add(readPacket(iter))
        }
    }

    return OperatorPacket(version, typeId, subPackets)
}

fun readPacket(iter: CharIterator): Packet {
    val (version, typeId) = readHeader(next(iter, 6).iterator())
    return if (typeId == LITERAL_TYPE_ID) readLiteralPacket(version, iter) else readOperatorPacket(
        version,
        typeId,
        iter
    )
}

fun readHeader(iter: CharIterator): PacketHeader {
    return PacketHeader(
        next(iter, 3).toInt(2),
        next(iter, 3).toInt(2)
    )
}

fun next(iterator: CharIterator, n: Int): String {
    val chars = mutableListOf<Char>()
    for (i in 1..n) {
        chars.add(iterator.next())
    }

    return chars.joinToString("")
}

