import Day01.incrementsCount

object Day01 {
  fun readInputsInts(name: String) =
    readInput(name).map(String::toInt)

  fun List<List<Int>>.incrementsCount(): Int =
    this.count { list -> list.last() > list.first() }
}

@Suppress("MagicNumber")
fun main() {
  println(
    "Part 1: " +
      Day01.readInputsInts("Day01_p1").windowed(2).incrementsCount()
  ) // 1233

  println(
    "Part 2: " +
      // A + B + C <=> B + C + D -> A B C D -> D > A
      Day01.readInputsInts("Day01_p2").windowed(4).incrementsCount()
  ) // 1275
}