package game

class Grid(private val gridCells: Array[Array[GridCell]], private val regionColoring: Map[(Int, Int), String]):
  private def getOptionCellAtPos(pos: (Int, Int)): Option[GridCell] = gridCells.lift(pos._1).flatMap( _.lift(pos._2) )

  def getOptionRegionContaining(pos: (Int, Int)): Option[GridRegion] = this.getOptionCellAtPos(pos).map(_.getRegion)
  
  def getGridCells: Array[Array[GridCell]]   = this.gridCells
  
  def getRegionsMap: Map[(Int, Int), String] = this.regionColoring
  
  def getRegions   : Set[GridRegion]         = this.getGridCells.flatten.map( _.getRegion ).toSet
  
  override def equals(obj: Any): Boolean =
    obj match
      case grid: Grid =>
        grid.regionColoring == this.regionColoring &&
          (grid.gridCells.flatten sameElements this.gridCells.flatten)
      case _          => false
end Grid

