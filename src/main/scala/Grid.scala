class Grid(private val gridCells: Array[Array[GridCell]], private val regions: Array[GridRegion]):
  private def getOptionCellAtPos(pos: (Int, Int)): Option[GridCell] = gridCells.lift(pos._1).flatMap( _.lift(pos._2) )

  def getOptionRegionContaining(pos: (Int, Int)): Option[GridRegion] = this.getOptionCellAtPos(pos).map(_.getRegion)
  
  def getGridCells: Array[Array[GridCell]] = this.gridCells
  
  def getRegions: Array[GridRegion] = this.regions

  def prettyString(): String = {
    gridCells.grouped(3).map { bigGroup =>
      bigGroup.map { row =>
        row.grouped(3).map { smallGroup =>
          smallGroup.map( _.getValue ).mkString(" ", " ", " ")
        }.mkString("|", "|", "|")
      }.mkString("\n")
    }.mkString("+-------+-------+-------+\n", "\n+-------+-------+-------+\n", "\n+-------+-------+-------+")
  }

  def possibleValuesAt(pos: (Int, Int)) =
    val valuesInThe3Square = gridCells.slice( (pos._1 / 3) * 3, (pos._1 / 3) * 3 + 3 )
      .flatMap( x => x.slice( (pos._2 / 3) * 3, (pos._2 / 3) * 3 + 3) )
      .map( _.getValue )
      .toSet
    val valuesInTheRow = gridCells(pos._1).map( _.getValue).toSet
    val valuesInTheColumn = gridCells.map( _(pos._2).getValue ).toSet
    val takenValues = valuesInTheRow.union(valuesInTheColumn).union(valuesInThe3Square)

    (1 to 9).toSet.diff(takenValues)
end Grid

