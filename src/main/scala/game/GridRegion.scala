package game

class GridRegion(private val sum: Int, private val cells: Set[(Int, Int)], private var color: String = "#FFFFFF"):
  require(this.areTheCellsOK, 
    "There are either too many cells, too little cells, or the input value of one of them isn't in the interval [1, 9].")
  require(this.isTheColorOk,  "The color doesn't follow the standard format.")
  require(this.isContiguous,  "The region is not contiguous - there are some 'breaks' between the region's cells.")
  require(this.isTheSumOk,    "The value of the sum is unattainable using " + this.getNumberOfCells + " cells. The troubling cells " +
    "are: " + this.getCells + " , and their sum is : " + this.getSum)

  def getCells: Set[(Int, Int)] = this.cells

  def getColor: String = this.color

  def setColor(newColor: String) =
    if isTheColorOk(newColor) then this.color = newColor

  def getSum: Int = this.sum

  def getNumberOfCells: Int = this.cells.size

  private def isTheColorOk(checked: String) : Boolean =
    checked.startsWith("#") && checked.length == 7 &&
      checked.tail.map( _.asDigit ).forall( x => x >= 0 && x <= 15)
    
  private def isTheColorOk: Boolean = isTheColorOk(this.color)

  private def areTheCellsOK: Boolean =
    cells.size <= 4 && cells.size >= 2
      && (cells.foldLeft(Set[Int]()){case (acc, (x, y)) => acc + x + y}).forall( x => x >= 0 && x < 9 )

  private def isTheSumOk: Boolean = 
    (this.getNumberOfCells * 1.5).toInt <= sum && sum <= (10 * this.getNumberOfCells) - (this.getNumberOfCells * 1.5).toInt

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

  override def toString: String =
    "" + this.getNumberOfCells + " cells: {" + this.getCells.mkString("; ") + "} with sum " + this.getSum + " and color " + this.getColor

  override def equals(obj: Any): Boolean =
    obj match
      case region: GridRegion =>
        (region.getSum == this.getSum)   &&
          (region.getCells == this.getCells)
      case _                  => false
end GridRegion
