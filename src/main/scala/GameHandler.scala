import scala.io.Source.fromFile
import java.io._
import spray.json.*
import DefaultJsonProtocol.*

import scala.util.Random.nextInt
import scala.collection.mutable.{Set => MSet, Buffer}

class GameHandler(private var gamePath: String):
  // Some private variables used to read & write from file.
  private val NUMBER_OF_CELLS = "numberOfCells"
  private val REGION_SUM      = "regionSum"
  private val X               = "x"
  private val Y               = "y"

  var selectedCell: Option[GridCell] = None
  var accountingBubble: Set[Set[Int]] = Set()
  private var grid: Option[Grid] = loadGame()

  def selectedRegion: Option[GridRegion] = this.selectedCell.map( _.getRegion )

  def setPath(newPath: String): Unit =
    this.gamePath = newPath
    loadGame()

  def getPath = this.gamePath

  private def loadGame(): Option[Grid] =
    val gameGrid = (0 to 8).toArray.map( x => Array.ofDim[GridCell](9) )
    val setOfGridRegions = MSet[GridRegion]()
    var returnGrid: Option[Grid] = None

    def gridRegionsNeighbouringACell(x: Int, y: Int): Set[GridRegion] =
      var neighbours = MSet[Option[GridRegion]]()
      for
        (xCord, yCord) <- Vector( (x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1) )
      do
        neighbours += gameGrid.lift(xCord).flatMap( _.lift(yCord) ).map( _.getRegion )

      neighbours.flatten.toSet

    try
      val buff = fromFile(gamePath)
      val jsonObject = buff.mkString.parseJson.asJsObject.getFields("regions").head.convertTo[Vector[Map[String, Int]]]
      buff.close()

      for
        obj <- jsonObject
      do
        val nrOfCells     = obj(NUMBER_OF_CELLS)
        val cellPositions = (0 until nrOfCells).map( x => (obj(X + x), obj(Y + x)) ).toSet
        val cellValues    = (0 until nrOfCells).map( x => obj.get("value" + x) )
        val regionSum     = obj(REGION_SUM)
        val newGridRegion = GridRegion(regionSum, cellPositions)

        setOfGridRegions += newGridRegion
        // update the accounting bubble?
        (cellPositions zip cellValues).foreach(((x, y) => gameGrid(x._1)(x._2) = GridCell(newGridRegion, y)))

      returnGrid =
          Some(
            Grid(gameGrid,
            colorGraph(setOfGridRegions.map( region => (region, region.getCells.flatMap(cell => gridRegionsNeighbouringACell(cell._1, cell._2)).diff(Set(region)) ) ).toMap))
          )

    catch
      // implement my own errors + throw them
      case e: FileNotFoundException => println("The specified file path is incorrect. \n\n" + e.printStackTrace())
      case e: NullPointerException  => println("Some of cells don't belong to any region, file is corrupted. \n\n" + e.printStackTrace())
      case e: IOException           => println("An IOException occured, maybe try again? \n\n"  + e.printStackTrace())
      case e: Exception             => println("Unknown exception occured: \n\n" + e.printStackTrace())

    returnGrid

  def saveGame(): Boolean =
    ???

  def resetGame(): Unit =
    ???

  //TODO: updateGame(button: Button) =

  def isGridFull: Boolean =
    ???

  def isGridCorrect: Boolean =
    ???

  def isRegionCorrect: Boolean =
    ???

  def getGrid: Option[Grid] = this.grid
  //TODO: def updateSelection

  def insertValue(newValue: Int): Unit =
    this.selectedCell.foreach( _.setValue(newValue) )
    ???

  def deleteValue(): Unit =
    this.selectedCell.foreach( _.deleteValue() )
    ???

  def updateBubble(): Unit =
    ???

  private def colorGraph(graph: Map[GridRegion, Set[GridRegion]]): Array[GridRegion] =
    val colors: Buffer[String] = Buffer[String]("#" + (0 until 6).map( x => nextInt(15).toHexString ).mkString)
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
        
    gridRegionList

end GameHandler
