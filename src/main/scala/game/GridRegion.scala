package game

class GridRegion(private val sum: Int, private val cells: Set[(Int, Int)], private var color: String = "#FFFFFF"):
  require(this.areTheCellsOK, 
    "There are either too many cells, too little cells, or the input value of one of them isn't in the interval [1, 9].")
  require(this.isTheColorOk,  "The color doesn't follow the standard format.")
  require(this.isContiguous,  "The region is not contiguous - there are some 'breaks' between the region's cells.")
  require(this.isTheSumOk,    "The value of the sum is unattainable using " + this.getNumberOfCells + " cells. The troubling cells " +
    "are: " + this.getCells + " , and their sum is : " + this.getSum)
  /** Returns the board position of the cells it contains. */
  def getCells: Set[(Int, Int)] = this.cells

  /** Returns the region's current color. */
  def getColor: String = this.color

  /** Sets the region's color. */
  def setColor(newColor: String) =
    require(isTheColorOk(newColor), "Incorrect color representation; please use the hexadecimal standard. ")
    this.color = newColor

  /** Returns the (required) sum of the region's cell values. */
  def getSum: Int = this.sum

  /** Returns the number of cells that the region contains. */
  def getNumberOfCells: Int = this.cells.size

  /** Private method checking whether a given color is ok. */
  private def isTheColorOk(checked: String) : Boolean =
    checked.startsWith("#") && checked.length == 7 &&
      checked.tail.map( _.asDigit ).forall( x => x >= 0 && x <= 15)

  /** Private method checking whether the color assigned to the region is ok. */
  private def isTheColorOk: Boolean = isTheColorOk(this.color)

  /** Private method checking whether the number of cells is between 2 and 4, 
   * and that the value of each cell is between 0 and 9. */
  private def areTheCellsOK: Boolean =
    cells.size <= 4 && cells.size >= 2
      && (cells.foldLeft(Set[Int]()){case (acc, (x, y)) => acc + x + y}).forall( x => x >= 0 && x < 9 )

  /** Private method checking whether the given required sum is attainable,
   * knowing the number of cells the region contains. */
  private def isTheSumOk: Boolean = 
    (this.getNumberOfCells * 1.5).toInt <= sum && sum <= (10 * this.getNumberOfCells) - (this.getNumberOfCells * 1.5).toInt

  /** Private method checking whether the given cell positions form a contiguous region. */
  private def isContiguous: Boolean =
    def getNeighboursInTheSet(pos: (Int, Int)) =
      // the function uses the fact that we already know that our grid
      // has a rectangular shape and that neighbours share an edge
      Set((pos._1 - 1, pos._2), (pos._1 + 1, pos._2), (pos._1, pos._2 - 1), (pos._1, pos._2 + 1)).intersect(cells)
    // Assumption that the set has at least 1 element
    var passedThrough = Set(cells.head)
    var neighbours = getNeighboursInTheSet(passedThrough.head)
    if neighbours == cells then return true

    while neighbours != passedThrough && neighbours.nonEmpty do
      val currentVertex = neighbours.diff(passedThrough).head
      passedThrough += currentVertex
      neighbours = neighbours.union(getNeighboursInTheSet(currentVertex))
      if neighbours == cells then return true

    neighbours == cells

  /** A human-readable String representation of the GridRegion. */
  override def toString: String =
    "" + this.getNumberOfCells + " cells: {" + this.getCells.mkString("; ") + "} with sum " + this.getSum + " and color " + this.getColor
  /** A more accurate comparison for GridRegion(s). */
  override def equals(obj: Any): Boolean =
    obj match
      case region: GridRegion =>
        (region.getSum == this.getSum)   &&
          (region.getCells == this.getCells)
      case _                  => false
end GridRegion
