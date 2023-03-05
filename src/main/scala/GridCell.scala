class GridCell(private var region: Option[GridRegion] = None, private var value: Option[Int] = None):
  require(value.getOrElse(1) >= 1 && value.getOrElse(1) <= 9)
  
  def getValue = this.value

  def setValue(newValue: Int) =
    require(newValue >= 1 && newValue <= 9)
    this.value = Some(newValue)

  def deleteValue() =
    this.value = None

  def getRegion = this.region
  def setRegion(newRegion: GridRegion) = this.region = Some(newRegion)
end GridCell

