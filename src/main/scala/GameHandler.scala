import scala.io.Source.fromFile
import java.io._
import spray.json.*
import DefaultJsonProtocol.*
import scala.collection.mutable.{Set => MSet}

class GameHandler(private val gamePath: String):
  private val NUMBER_OF_CELLS = "numberOfCells"
  private val REGION_SUM      = "regionSum"
  private val X               = "x"
  private val Y               = "y"

  var selectedCell: Option[GridCell] = None
  var accountingBubble: Set[Set[Int]] = Set()
  private val grid: Grid = loadGame()

  def selectedRegion: Option[GridRegion] = this.selectedCell.map( _.getRegion )

  def loadGame(): Grid =
    val gameGrid = (0 to 8).toArray.map( x => Array.ofDim[GridCell](9) )
    var neighbourhoodMap: Map[GridRegion, Set[GridRegion]] = Map[GridRegion, Set[GridRegion]]()
    val setOfGridRegions = MSet[GridRegion]()

    try
      val buff = fromFile(gamePath)
      val jsonObject = buff.mkString.parseJson.asJsObject.getFields("region").head.convertTo[Vector[Map[String, Int]]]
      buff.close()

      for
        obj <- jsonObject
      do
        val nrOfCells     = obj("numberOfCells")
        val cellPositions = (0 until nrOfCells).map( x => (obj("x" + x), obj("y" + x)) ).toSet
        val cellValues    = (0 until nrOfCells).map( x => obj.get("value" + x) )
        val regionSum     = obj("regionSum")
        val newGridRegion = GridRegion(regionSum, cellPositions)

        setOfGridRegions += newGridRegion
        (cellPositions zip cellValues).foreach(((x, y) => gameGrid(x._1)(x._2) = GridCell(newGridRegion, y)))

      neighbourhoodMap = setOfGridRegions.map( region => (region, region.getCells.flatMap(cell => gridRegionsNeighbouringACell(cell._1, cell._2))) ).toMap

    catch
      // implement my own errors + throw them
      case e: FileNotFoundException => println("The specified file path is incorrect. \n\n" + e.printStackTrace())
      case e: NullPointerException  => println("Some of cells don't belong to any region, file is corrupted. \n\n" + e.printStackTrace())
      case e: IOException           => println("An IOException occured, maybe try again? \n\n" + e.printStackTrace())
      case e: Exception             => println("Unknown exception occured: \n\n" + e.printStackTrace())

    Grid(gameGrid, neighbourhoodMap)

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

  def getGrid = this.grid

  //TODO: def updateSelection

  def insertValue(newValue: Int): Unit =
    this.selectedCell.foreach( _.setValue(newValue) )
    ???

  def deleteValue(): Unit =
    this.selectedCell.foreach( _.deleteValue() )
    ???

  def updateBubble(): Unit =
    ???

  private def gridRegionsNeighbouringACell(x: Int, y: Int): Set[GridRegion] =
    Set((x - 1, y - 1), (x + 1, y - 1), (x - 1, y + 1), (x + 1, y + 1)).flatMap(this.grid.getOptionRegionContaining(_))
end GameHandler
