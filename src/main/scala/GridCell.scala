class GridCell(private var region: GridRegion, private var value: Option[Int] = None):
  require(value.getOrElse(1) >= 1 && value.getOrElse(1) <= 9)
  
  def getValue = this.value

  def setValue(newValue: Int) =
    require(newValue >= 1 && newValue <= 9)
    this.value = Some(newValue)

  def deleteValue() =
    this.value = None

  def getRegion: GridRegion = this.region
  def setRegion(newRegion: GridRegion) =
    this.region = newRegion

  override def toString: String =
    "A" + (if value.isDefined then " GridCell with value  " + this.value.getOrElse(0) else "n empty Gridcell") + ", belonging to " + this.region
end GridCell

