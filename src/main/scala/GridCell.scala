class GridCell(private var region: GridRegion, private var value: Int = 0):
  require(value >= 0 && value <= 9)

  def getValue: Int = this.value

  def setValue(newValue: Int): Unit =
    require(newValue >= 1 && newValue <= 9)
    this.value = newValue

  def deleteValue(): Unit =
    this.value = 0
    
  def isNonEmpty: Boolean = this.value != 0

  def getRegion: GridRegion = this.region
  
  def setRegion(newRegion: GridRegion) =
    this.region = newRegion

  override def toString: String =
    "A" + (if value != 0 then " GridCell with value " + this.value else "n empty Gridcell")
end GridCell

