class GridRegion(val sum: Int, val cellNumber: Int, val cells: Set[(Int, Int)], val color: Color):
  require(2 <= cellNumber
      && cellNumber <= 4
      && cells.size == cellNumber
      && (cells.foldLeft(Set[Int]()){case (acc, (x, y)) => acc + x + y}).forall( x => x >= 0 && x < 9 )
      && this.isContiguous
      && this.doesTheSumMakeSense
  )

  def getCells: Set[(Int, Int)] = this.cells

  def getColor: Color = this.color

  private def doesTheSumMakeSense: Boolean = (1 until cellNumber + 1).sum <= sum && sum >= (9 until 9 - cellNumber by -1).sum

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
