class GridCell(val region: GridRegion, var value: Option[Int] = None):
  require(value.getOrElse(1) >= 1 && value.getOrElse(1) <= 9)
  
  def getValue = this.value

  def setValue(newValue: Int) =
    require(newValue >= 1 && newValue <= 9)
    this.value = Some(newValue)

  def deleteValue() =
    this.value = None

  def getRegion = this.region
end GridCell

