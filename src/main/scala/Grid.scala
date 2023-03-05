class Grid(val gridCells: Array[Array[GridCell]], val graph: Map[GridRegion,Set[GridRegion]]):
  def getCellAtPos(pos: (Int, Int)): GridCell = gridCells(pos._1)(pos._2)

  def getRegionContaining(pos: (Int, Int)) = this.getCellAtPos(pos).getRegion

  def possibleValuesAt(pos: (Int, Int)) =
    val valuesInThe3Square = gridCells.slice( (pos._1 / 3) * 3, (pos._1 / 3) * 3 + 3 )
      .flatMap( x => x.slice( (pos._2 / 3) * 3, (pos._2 / 3) * 3 + 3) )
      .map( _.getValue.getOrElse(0) )
      .toSet
    val valuesInTheRow = gridCells(pos._1).map( _.getValue.getOrElse(0)).toSet
    val valuesInTheColumn = gridCells.map( _(pos._2).getValue.getOrElse(0) ).toSet
    val takenValues = valuesInTheRow.union(valuesInTheColumn).union(valuesInThe3Square)

    (1 to 9).toSet.diff(takenValues)
end Grid

