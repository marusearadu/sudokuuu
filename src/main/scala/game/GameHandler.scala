package game

import spray.json.*
import spray.json.DefaultJsonProtocol.*
import sun.jvm.hotspot.debugger.Address

import java.io.*
import scala.collection.mutable.{Buffer, Set as MSet}
import scala.io.Source.fromFile
import scala.util.Random.nextInt

// Some private variables used to read & write from file.

class GameHandler(private var selectedPos: Option[(Int, Int)] = None, private var grid: Grid):
  private def selectedCell: Option[GridCell] = this.selectedPos.map( x => this.getGridCells(x._1)(x._2) )

  private def possibleSumsOfARegion(region: GridRegion): Set[Array[Int]] =
    var possibleSums: Set[Array[Int]] = Set(Array())
    val theCellValues = region.getCells.toArray.map( x => this.getGridCells(x._1)(x._2).getValue )
    val emptyCells = theCellValues.filter( _ == 0 )
    val emptyCellsSum = region.getSum - theCellValues.filter( _ != 0 ).sum

    for i <- emptyCells.indices do
      possibleSums = (1 to 9).toSet.flatMap( x => possibleSums.map( y => y ++ Array(x) ))

    // possibleSums will have a lot of arrays that are identical up to a permutation
    // to get rid of them, we just group them by their respective idential sets and choose a random element
    possibleSums.filter( _.sum == emptyCellsSum ).groupBy( _.toSet ).map( (x, y) => y.head.sorted ).toSet

  // some random bug appeared once but can't be repeated, i don't know why but i really do hope it was a special instance of me being stupid
  def possibleValuesAt(pos: (Int, Int)) =
    val valuesInThe3Square = this.getGridCells.slice( (pos._1 / 3) * 3, (pos._1 / 3) * 3 + 3 )
      .flatMap( x => x.slice( (pos._2 / 3) * 3, (pos._2 / 3) * 3 + 3) )
      .map( _.getValue )
      .toSet
    val valuesInTheRow = this.getGridCells(pos._1).map( _.getValue).toSet
    val valuesInTheColumn = this.getGridCells.map( _(pos._2).getValue ).toSet
    val takenValues = valuesInTheRow.union(valuesInTheColumn).union(valuesInThe3Square)

    this.selectedCell
      .map( x => this.possibleSumsOfARegion(x.getRegion).flatMap( _.toSet) ).getOrElse( Set[Int]() ) // getting all the possible values in this region according to possibleSumsOfARegion
      .diff(takenValues)

  def getBubble: Set[Array[Int]] =
    this.selectedCell.map(x => possibleSumsOfARegion(x.getRegion)).getOrElse(Set[Array[Int]]())

  def resetGame(): Unit =
    this.getGrid.getGridCells.flatten.foreach( _.deleteValue())

  def getGrid: Grid = this.grid

  private def getGridCells = this.getGrid.getGridCells

  def select(pos: (Int, Int)): Unit =
    this.selectedPos = Some(pos)

  def         isGridFull    : Boolean = getGridCells.flatten.forall( _.isNonEmpty )

  private def areRowsCorrect: Boolean = this.getGridCells.forall( _.toSet == (1 to 9).toSet )

  private def areColsCorrect: Boolean = this.getGridCells.transpose.forall( _.toSet == (1 to 9).toSet )

  private def areSqrsCorrect: Boolean =
    for
      x <- (0 until 3)
      y <- (0 until 3)
    do
      if this.getGridCells.slice( x * 3, x * 3 + 3 ).flatMap( x => x.slice( y * 3, y * 3 + 3) )
        .map( _.getValue ).toSet != (1 to 9).toSet then return false
    true

  private def areRegsCorrect: Boolean =
    this.getGrid.getRegions.forall(
      x => x.getCells.map( cell => this.getGridCells(cell._1)(cell._2).getValue ).sum == x.getSum
    )

  def         isGridCorrect : Boolean = isGridFull && areRowsCorrect && areColsCorrect && areSqrsCorrect && areRegsCorrect

  def   insertValue(newValue: Int): Unit =
    this.selectedCell.foreach( _.setValue(newValue) )

  def deleteValue(): Unit =
    this.selectedCell.foreach( _.deleteValue() )

  def prettyPrint(): String = {
    this.getGridCells.grouped(3).map { bigGroup =>
      bigGroup.map { row =>
        row.grouped(3).map { smallGroup =>
          smallGroup.map( _.getValue ).mkString(" ", " ", " ")
        }.mkString("|", "|", "|")
      }.mkString("\n")
    }.mkString("+-------+-------+-------+\n", "\n+-------+-------+-------+\n", "\n+-------+-------+-------+") +
      "\nAll the possible combinations, up to permutations: \n" + this.getBubble.map( _.mkString(" + ") ).mkString("\n")
  }

  def getValue: Any =
    this.selectedCell.map(_.getValue).getOrElse("Please select a cell first.")
end GameHandler

object GameHandler:
  private val NUMBER_OF_CELLS = "numberOfCells"
  private val REGION_SUM      = "regionSum"
  private val X               = "x"
  private val Y               = "y"
  private val VALUE           = "value"
  private var currentAddress: String = _
  
  def getAddress: String = this.currentAddress

  def loadGame(address: String): GameHandler =
    currentAddress = address
    val gameGrid = (0 to 8).toArray.map( x => Array.ofDim[GridCell](9) )
    val setOfGridRegions = MSet[GridRegion]()
    var returnGrid: Grid = null

    def colorGraph(graph: Map[GridRegion, Set[GridRegion]]): Map[(Int, Int), String] =
      val colors: Buffer[String] = Buffer[String]("#e5de00", "#e3242b", "#1338be", "#a45ee5")
      val gridRegionList = graph.keys.toArray
      gridRegionList.head.setColor(colors.head)

      for
        gridRegion <- gridRegionList.tail
      do
        val colorsUsedByNow = graph(gridRegion).map( x => x.getColor ).filterNot( _ == "#FFFFFF" ).toBuffer
        val freeColors = colors.diff(colorsUsedByNow)
        if freeColors.isEmpty then
          val newColor = "#" + (0 until 6).map( x => nextInt(15).toHexString ).mkString
          colors += newColor
          gridRegion.setColor(newColor)
        else
          gridRegion.setColor(colors.diff(colorsUsedByNow).head)

      gridRegionList.map(region => region.getCells -> region.getColor)
        .map( ((x, y) => x.map(each => each -> y)) ).toSet.flatten.toMap

    def gridRegionsNeighbouringACell(x: Int, y: Int): Set[GridRegion] =
      var neighbours = MSet[Option[GridRegion]]()
      for
        (xCord, yCord) <- Vector( (x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1) )
      do
        neighbours += gameGrid.lift(xCord).flatMap( _.lift(yCord) ).map( _.getRegion )

      neighbours.flatten.toSet

    try
      val buff = fromFile(currentAddress)
      val jsonObject = buff.mkString.parseJson.convertTo[Vector[Map[String, Int]]]
      buff.close()

      for
        obj <- jsonObject
      do
        val nrOfCells     = obj(NUMBER_OF_CELLS)
        val cellPositions = (0 until nrOfCells).map( x => (obj(X + x), obj(Y + x)) ).toSet
        val cellValues    = (0 until nrOfCells).map( x => obj(VALUE + x) )
        val regionSum     = obj(REGION_SUM)
        val newGridRegion = GridRegion(regionSum, cellPositions)

        setOfGridRegions += newGridRegion
        (cellPositions zip cellValues).foreach(((x, y) => gameGrid(x._1)(x._2) = GridCell(newGridRegion, y)))

      returnGrid = Grid(
        gameGrid, 
        colorGraph(setOfGridRegions.map( region => (region, region.getCells.flatMap(cell => gridRegionsNeighbouringACell(cell._1, cell._2)).diff(Set(region)) ) ).toMap)
      )

    catch
      case e: FileNotFoundException  => throw new BadFilePathException
      case e: NoSuchElementException => throw new CorruptedFileException("Error parsing the JSON: some of the required elements weren't found.")
      case e: NullPointerException   => throw new CorruptedFileException("File should include all include information about all the cells, " +
        "including their regions (and possibly their cell value).")
      case e: IOException            => throw new UnknownException("Unknown IOException occured: \n" + e)
      case e: Exception              => throw new UnknownException("Unknown exception occured: \n" + e)
    if returnGrid == null then throw new CorruptedFileException("Corrupt JSON file. \n") else GameHandler(grid = returnGrid)

  def saveGame(game: GameHandler, address: String = this.currentAddress): Unit =
    def makeMap(x: Int, y: Int, z: Int): Map[String, Int] = Map(X + z -> x, Y + z -> y, VALUE + z -> game.getGrid.getGridCells(x)(y).getValue)

    try
      val vectorOfRegions: Array[Map[String, Int]] =
        game.getGrid.getRegions.map(
          region =>
            region.getCells.zipWithIndex.map(((pos, z) => makeMap(pos._1, pos._2, z))).reduce((x, y) => x ++ y) ++
              Map(REGION_SUM -> region.getSum, NUMBER_OF_CELLS -> region.getNumberOfCells)
        ).toArray
      val jsonPrint = vectorOfRegions.toJson.prettyPrint
      val file = new File(address)
      val bufferedWriter = new BufferedWriter(new FileWriter(file))
      bufferedWriter.write(jsonPrint)
      bufferedWriter.close()
    catch
      case e: IOException => throw new UnknownException("An IOException was encountered, aborting saving. Please try again. \n")
      case e: Exception   => throw new UnknownException("Unknown exception occured: \n" + e)
end GameHandler

