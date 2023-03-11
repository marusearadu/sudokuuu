class GridRegion(private val sum: Int, private val cells: Set[(Int, Int)], private val color: String = "#FFFFFF"):
  require(this.areTheCellsOK, "Are the cells OK")
  require(this.isTheColorOk, "is the color OK")
  require(this.isContiguous, "is the region contiguous")
  require(this.isTheSumOk, "is the sum ok")

  def getCells: Set[(Int, Int)] = this.cells

  def getColor: String = this.color

  def getSum: Int = this.sum

  def getNumberOfCells: Int = this.cells.size

  private def isTheColorOk: Boolean =
    this.color.startsWith("#") && this.color.length == 7 &&
      this.color.tail.map( _.asDigit ).forall( x => x >= 0 && x <= 15)

  private def areTheCellsOK: Boolean =
    cells.size <= 4 && cells.size >= 2
      && (cells.foldLeft(Set[Int]()){case (acc, (x, y)) => acc + x + y}).forall( x => x >= 0 && x < 9 )

  private def isTheSumOk: Boolean = (1 to cells.size).sum <= sum
    && sum <= (9 until 9 - cells.size by -1).sum

  private def isContiguous: Boolean =
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

  private def getNeighboursInTheSet(pos: (Int, Int)) =
  // the function uses the fact that we already know that our grid
  // has a rectangular shape and that neighbours share an edge
    Set((pos._1 - 1, pos._2), (pos._1 + 1, pos._2), (pos._1, pos._2 - 1), (pos._1, pos._2 + 1))
      .intersect(cells)

  override def toString: String =
    "A gridRegion object containing " + this.getNumberOfCells + " cells: " + this.getCells.mkString("; ") + ". The sum is " + this.getSum + "."
end GridRegion
