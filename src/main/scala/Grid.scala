class Grid(private val gridCells: Array[Array[GridCell]], private val regions: Array[GridRegion]):
  private def getOptionCellAtPos(pos: (Int, Int)): Option[GridCell] = gridCells.lift(pos._1).flatMap( _.lift(pos._2) )

  def getOptionRegionContaining(pos: (Int, Int)): Option[GridRegion] = this.getOptionCellAtPos(pos).map(_.getRegion)
  
  def getGridCells: Array[Array[GridCell]] = this.gridCells
  
  def getRegions: Array[GridRegion] = this.regions

  override def equals(obj: Any): Boolean =
    obj match
      case grid: Grid =>
        (grid.regions sameElements this.regions) &&
          (grid.gridCells.flatten sameElements this.gridCells.flatten)
      case _          => false
end Grid

