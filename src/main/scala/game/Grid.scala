package game

class Grid(private val gridCells: Array[Array[GridCell]], private val regionColoring: Map[(Int, Int), String]):
  /** Returns the board's GridCells. */
  def getGridCells: Array[Array[GridCell]]   = this.gridCells

  /** Returns a map which assigns each cell position a color */
  def getRegionsMap: Map[(Int, Int), String] = this.regionColoring

  /** Returns the Set of the Grid's GridRegions. */
  def getRegions   : Set[GridRegion]         = this.getGridCells.flatten.map( _.getRegion ).toSet

  /** A more useful comparison of Grid(s). */
  override def equals(obj: Any): Boolean =
    obj match
      case grid: Grid =>
        grid.regionColoring == this.regionColoring &&
          (grid.gridCells.flatten sameElements this.gridCells.flatten)
      case _          => false
end Grid

