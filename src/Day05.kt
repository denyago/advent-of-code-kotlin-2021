import Day05.asVentLines
import kotlin.math.abs

fun main() {
  val ventLines1 = readInput("Day05_test")
    .asVentLines().filter { it.isVertical() || it.isHorizontal() }
  println("Parsed Lines: ${ventLines1.count()}")
  val diagram1 = Diagram(ventLines1)
  println("Built Diagram.")
  println(
    "Part 1: " +
      diagram1.dangerRarting()
  ) // 5608 (test = 5)

  val ventLines2 = readInput("Day05_p2")
    .asVentLines()
  println("Parsed Lines: ${ventLines2.count()}")
  val diagram2 = Diagram(ventLines2)
  println("Built Diagram.\n${diagram2.inspect()}")
  println(
    "Part 2: " +
      diagram2.dangerRarting()
  ) // -> 20299 (test = 12)
}

object Day05 {
  fun List<String>.asVentLines() =
    this.map(VentLine::fromString)
}

data class VentLine(val start: Point, val end: Point) {
  fun asPoints() =
    asXs().zip(asYs())
      .fold(emptyList<Point>()) { list, p ->
        list + listOf(Point(p.first, p.second))
      }

  fun isHorizontal() =
    start.x != end.x && start.y == end.y

  fun isVertical() =
    start.y != end.y && start.x == end.x

  @Suppress("unused")
  fun inspect() =
    "Line ${start.x},${start.y} -> ${end.x},${end.y} " +
      "(${asPoints().joinToString(";", transform = Point::inspect)})"

  private fun asXs() =
    if (xLength == 0) {
      generateSequence { start.x }.take(yLength + 1).toList()
    } else {
      listOf(start.x, end.x).let { (s, e) ->
        if (s < e) (s until e + 1) else (s downTo e)
      }.toList()
    }

  private fun asYs() =
    if (yLength == 0) {
      generateSequence { start.y }.take(xLength + 1).toList()
    } else {
      listOf(start.y, end.y).let { (s, e) ->
        if (s < e) (s until e + 1) else (s downTo e)
      }.toList()
    }

  private val yLength = abs(end.y - start.y)
  private val xLength = abs(end.x - start.x)

  companion object {
    fun fromString(string: String) =
      string.split("""\s+->\s+""".toRegex())
        .map(Point::fromString).chunked(2)
        .first().let { (start, end) -> VentLine(start, end) }
  }
}

data class Point(val x: Int, val y: Int) {
  fun inspect() = "($x,$y)"

  companion object {
    fun fromString(string: String) =
      string.split(",")
        .map(String::toInt).let { (x, y) -> Point(x, y) }
  }
}

@JvmInline
value class VentIntencity(private val i: Int = 0) {
  operator fun plus(that: VentIntencity) =
    VentIntencity(this.i + that.i)

  override fun toString() =
    if (i == 0) "." else i.toString()

  operator fun compareTo(other: VentIntencity) = i - other.i
}

class Diagram(ventLines: List<VentLine>) {
  private val ventsMap = mapFromLines(ventLines)

  fun dangerRarting() =
    ventsMap.values.count { it > VentIntencity(1) }

  private fun mapFromLines(ventLines: List<VentLine>) =
    ventLines
      .also { println("Mapping ${it.count()} Vent Lines...") }
      .flatMap(VentLine::asPoints)
      .also { println("Reducing points ${it.count()} into a Map") }
      .fold(emptyMap<Point, VentIntencity>().toMutableMap()) { ventsMap, point ->
        ventsMap[point] = ventsMap.getOrDefault(point, VentIntencity(0)) + VentIntencity(1)
        ventsMap
      }

  @Suppress("unused")
  fun inspect() =
    ventsMap.keys
      .let { keys ->
        val maxX = keys.map(Point::x).maxOrNull() ?: 0
        val maxY = keys.map(Point::y).maxOrNull() ?: 0
        (0..maxY).joinToString("\n") { y ->
          (0..maxX).joinToString("") { x ->
            ventsMap.getOrDefault(Point(x, y), VentIntencity()).toString()
          }
        }
      }
}