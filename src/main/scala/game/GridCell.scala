package game

class GridCell(private var region: GridRegion, private var value: Int = 0):
  require(value >= 0 && value <= 9)

  /** Returns the current value of the cell.
   * (0 = no value)*/
  def getValue: Int = this.value

  /** Sets the value for the cell. */
  def setValue(newValue: Int): Unit =
    require(newValue >= 0 && newValue <= 9)
    this.value = newValue

  /** Returns the GridRegion object this cell belongs to. */
  def getRegion: GridRegion = this.region
  
  /** A human-readable represantion of the GridCell object. */
  override def toString: String =
    "A" + (if value != 0 then " GridCell with value " + this.value else "n empty Gridcell")

  /** A more useful comparison of GridCell(s). */
  override def equals(obj: Any): Boolean =
    obj match
      case cell: GridCell => this.getValue == cell.getValue
      case _              => false
end GridCell

