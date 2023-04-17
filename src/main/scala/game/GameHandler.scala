package game

import javafx.beans.{InvalidationListener, Observable}
import scalafx.beans.value.ObservableValue
import spray.json.*
import spray.json.DefaultJsonProtocol.*

import java.io.*
import scala.collection.mutable.{Buffer, Set as MSet}
import scala.io.Source.fromFile
import scala.util.Random.nextInt


class GameHandler(private var selectedPos: Option[(Int, Int)] = None, private var grid: Grid):
  def numberAt(posX: Int, posY: Int): Int =
    require(0 <= posX && posX <= 8 && 0 <= posY && posY <= 8, "Invalid coordinates were passed.")
    this.getGridCells(posX)(posY).getValue
  
  /** Returns the currently selected cell, if any. */
  private def selectedCell: Option[GridCell] = this.selectedPos.map( x => this.getGridCells(x._1)(x._2) )

  /** Calculates the possible sums of an non-full region.
   * In case the region is full, it returns an empty Set[Array[Int]]*/
  private def possibleSumsOfARegion(region: GridRegion): Set[Array[Int]] =
    var possibleSums: Set[Array[Int]]   = Set(Array())
    val theCells: Set[(Int, Int)]       = region.getCells
    val emptyCells: Set[(Int, Int)]     = theCells.filter( x => this.numberAt(x._1, x._2) == 0 )
    val existingValues: Array[Int]      = theCells.diff(emptyCells).toArray.map( x => this.numberAt(x._1, x._2) )
    val distinctVals: Int               = Vector(
      theCells.groupBy( _._1 ).map( (_, x) => x.size ).max,
      theCells.groupBy( _._2 ).map( (_, x) => x.size ).max,
      theCells.size + 1 - theCells.groupBy(((x, y) => (x / 3, y / 3))).size
    ).max

    for i <- (0 until theCells.size) do
      possibleSums = (1 to 9).toSet.flatMap( x => possibleSums.map( y => y ++ Array(x) ))

    // possibleSums will have a lot of arrays that are identical up to a permutation
    // to get rid of them, we just group them by their respective idential sets and choose a random element
    possibleSums
      .filter(_.sum == region.getSum) // getting all the possible sums for the original region
      .groupBy(_.toSet) // grouping them by the Set they represent
      .filter(((x, y) => x.size >= distinctVals && existingValues.forall(
        i => existingValues.count( _ == i ) <= y.head.count( _ == i )
      )))    // filtering out the "basic" ones
      .map( (_, y) => y.head.diff(existingValues).sorted )             // getting one element out of each
      .toSet

  /**
   * Returns the possible values that can be placed at the specified position without
   * violating the rules of Sudoku and while taking into account the possible sum-splits
   * inside the region.
   */
  def possibleValuesAt(pos: (Int, Int)): Set[Int] =
  // some random bug appeared once but can't be repeated, i don't know why but i really do hope it was a special instance of me being stupid
    val valuesInThe3Square = this.getGridCells.slice((pos._1 / 3) * 3, (pos._1 / 3) * 3 + 3)
      .flatMap(x => x.slice((pos._2 / 3) * 3, (pos._2 / 3) * 3 + 3))
      .map(_.getValue)
      .toSet
    val valuesInTheRow = this.getGridCells(pos._1).map(_.getValue).toSet
    val valuesInTheColumn = this.getGridCells.map(_(pos._2).getValue).toSet
    val takenValues = valuesInTheRow.union(valuesInTheColumn).union(valuesInThe3Square)

    this.possibleSumsOfARegion(this.getGridCells(pos._1)(pos._2).getRegion).flatMap(_.toSet).diff(takenValues) // getting all the possible values in this region according to possibleSumsOfARegion

  /** Returns the set of possible sum-splits for the currently selected cell's region. */
  def getBubble: Set[Array[Int]] =
    this.selectedCell.map(x => possibleSumsOfARegion(x.getRegion)).getOrElse(Set[Array[Int]]())

  /** Resets the game, i.e. all the cells are equalled to 0. */
  def resetGame(): Unit =
    this.getGrid.getGridCells.flatten.foreach( _.setValue(0) )
    this.selectedPos = None

  /** Returns the grid being used by the game handler. */
  def getGrid: Grid = this.grid

  /** Returns the grid's GridCells; shortcut method */
  private def getGridCells: Array[Array[GridCell]] = this.getGrid.getGridCells

  /** Selects the cell at the given position. */
  def select(pos: (Int, Int)): Unit =
    this.selectedPos = Some(pos)
    
  def deselect()             : Unit =
    this.selectedPos = None

  /** Checks whether the grid is full. */
  def         isGridFull    : Boolean = getGridCells.flatten.forall( _.getValue != 0 )

  /** Checks whether all the rows of the grid are correct. */
  private def areRowsCorrect: Boolean = this.getGridCells.forall( _.map( _.getValue ).toSet == (1 to 9).toSet)

  /** Checks whether all the columns of the grid are correct. */
  private def areColsCorrect: Boolean = this.getGridCells.transpose.forall( _.map( _.getValue ).toSet == (1 to 9).toSet )

  /** Checks whether all the squares of the grid are correct. */
  private def areSqrsCorrect: Boolean =
    for
      x <- (0 until 3)
      y <- (0 until 3)
    do
      if this.getGridCells.slice( x * 3, x * 3 + 3 ).flatMap( x => x.slice( y * 3, y * 3 + 3) )
        .map( _.getValue ).toSet != (1 to 9).toSet then return false
    true

  /** Checks whether all the regions of the grid are correct.*/
  private def areRegsCorrect: Boolean =
    this.getGrid.getRegions.forall(
      x => x.getCells.map( cell => this.getGridCells(cell._1)(cell._2).getValue ).sum == x.getSum
    )

  /** Checks whether the grid is correct (i.e., whether all the rows, columns, squares, and regions are correct, and
   *  whether the grid is full). */
  def         isGridCorrect : Boolean = areRowsCorrect && areColsCorrect && areSqrsCorrect && areRegsCorrect

  /** Inserts the given value into the selected cell (if there is one). */
  def   insertValue(newValue: Int): Unit =
    this.selectedCell.foreach( _.setValue(newValue) )

  def insertValueAt(newValue: Int, posX: Int, posY: Int): Unit =
    require(0 <= posX && posX <= 8 && 0 <= posY && posY <= 8, "Invalid coordinates were passed.")
    this.getGridCells(posX)(posY).setValue(newValue)

  // un-make this function private in order to easily test in the sbt console
  /** Creates a pretty, terminal-friendly string of the current state of the sudoku game. */
  private def prettyPrint(): String = "\n" + {
    this.getGridCells.grouped(3).map { bigGroup =>
      bigGroup.map { row =>
        row.grouped(3).map { smallGroup =>
          smallGroup.map( _.getValue ).mkString(" ", " ", " ")
        }.mkString("|", "|", "|")
      }.mkString("\n")
    }.mkString("+-------+-------+-------+\n", "\n+-------+-------+-------+\n", "\n+-------+-------+-------+") +
      "\nAll the possible combinations, up to permutations: \n" + this.getBubble.map( _.mkString(" + ") ).mkString("\n")
  }
end GameHandler

object GameHandler:
  private val NUMBER_OF_CELLS = "numberOfCells"
  private val REGION_SUM      = "regionSum"
  private val X               = "x"
  private val Y               = "y"
  private val VALUE           = "value"
  private var currentAddress: String = _
  
  /** Returns the current (i.e., the last-opened) address. */
  def getAddress: String = this.currentAddress
  
  /** Load the game from a file. */
  def loadGame(address: String): GameHandler =
    currentAddress = address
    val gameGrid = (0 to 8).toArray.map( x => Array.ofDim[GridCell](9) )
    val setOfGridRegions = MSet[GridRegion]()
    var returnGrid: Grid = null

    def colorGraph(graph: Map[GridRegion, Set[GridRegion]]): Map[(Int, Int), String] =
      val colors: Buffer[String] = Buffer[String]("#03465a", "#eaa406", "#ba4c24", "#e49979", "#a2a3c1", "#6d8a88")
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
        (cellPositions zip cellValues).foreach((
          (x, y) =>
            if gameGrid(x._1)(x._2) == null then
              gameGrid(x._1)(x._2) = GridCell(newGridRegion, y)
            else
              throw CorruptedFileException("There are some overlapping regions. ")
          )
        )

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
  
  /** Saves the game to a file; the location is pre-defined as the last-opened one. */
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
