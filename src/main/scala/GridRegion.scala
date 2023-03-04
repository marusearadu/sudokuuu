class GridRegion(private val sum: Int, cellNumber: Int, private val cells: Set[(Int, Int)], private val color: String):
  require(this.areTheCellsOK
      && this.isTheColorOk
      && this.isContiguous
      && this.isTheSumOk
  )

  def getCells: Set[(Int, Int)] = this.cells

  def getColor: String = this.color

  def getSum: Int = this.sum

  private def isTheColorOk: Boolean =
    this.color.startsWith("#") && this.color.length == 7 &&
      this.color.tail.map( _.asDigit ).forall( x => x >= 0 && x <= 9)

  private def areTheCellsOK: Boolean =
    cellNumber <= 4 && cellNumber >= 2 && cellNumber == this.cells.size
      && (cells.foldLeft(Set[Int]()){case (acc, (x, y)) => acc + x + y}).forall( x => x >= 0 && x < 9 )

  private def isTheSumOk: Boolean = (1 until cellNumber + 1).sum <= sum
    && sum <= (9 until 9 - cellNumber by -1).sum

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
    Set((pos._1 - 1, pos._2), (pos._1 + 1, pos._2), (pos._1, pos._2 - 1), (pos._1, pos._2 + 1)).intersect(cells)
end GridRegion
