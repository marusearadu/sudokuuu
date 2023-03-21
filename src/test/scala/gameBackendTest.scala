import java.io.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.should

class gameBackendTest extends AnyFlatSpec with Matchers:
  val testData: Seq[(String, Array[GridRegion])] = Seq(
      ("src\\test\\scala\\testFiles\\example.json", Array(
          GridRegion(14, Set((3,1), (3,2))),
          GridRegion(4, Set((0,6), (1,6))),
          GridRegion(13, Set((4,1), (4,2), (5,1))),
          GridRegion(20, Set((4,3), (5,3), (6,3))),
          GridRegion(25, Set((1,0), (1,1), (2,0), (2,1))),
          GridRegion(16, Set((7,2), (8,2))),
          GridRegion(17, Set((8,7), (8,8))),
          GridRegion(15, Set((0,2), (0,3), (0,4))),
          GridRegion(6, Set((5,2), (6,1), (6,2))),
          GridRegion(20, Set((5,5), (6,5), (6,6))),
          GridRegion(17, Set((1,2), (1,3))),
          GridRegion(13, Set((8,4), (8,5), (8,6))),
          GridRegion(27, Set((5,0), (6,0), (7,0), (8,0))),
          GridRegion(8, Set((7,1), (8,1))),
          GridRegion(12, Set((4,8), (5,8))),
          GridRegion(15, Set((0,8), (1,8), (2,8), (3,8))),
          GridRegion(8, Set((2,5), (3,5), (4,5))),
          GridRegion(22, Set((0,5), (1,4), (1,5), (2,4))),
          GridRegion(6, Set((5,6), (5,7))),
          GridRegion(9, Set((2,2), (2,3), (3,3))),
          GridRegion(17, Set((3,4), (4,4), (5,4))),
          GridRegion(15, Set((7,5), (7,6))),
          GridRegion(17, Set((3,7), (4,6), (4,7))),
          GridRegion(16, Set((0,7), (1,7))),
          GridRegion(3, Set((0,0), (0,1))),
          GridRegion(20, Set((2,6), (2,7), (3,6))),
          GridRegion(14, Set((6,7), (7,7), (6,8), (7,8))),
          GridRegion(6, Set((3,0), (4,0))),
          GridRegion(10, Set((7,3), (8,3), (6,4), (7,4)))
          )
      )
    )

  "GameHandler.loadGame(...)" should "load the regions of good JSON files properly. " in {

    for
      (path: String, regions: Array[GridRegion]) <- testData
    do
      // implementation laborious because it seems like scala is stupid
      // and it can't compare 2 arrays/sets normally
      // yeah, really
      val loadedRegions = GameHandler.loadGame(path).getGrid.getRegions
      for region <- loadedRegions do
        withClue("Region " + region.toString + " doesn't get loaded. ") {
          regions should contain (region)
        }
      for region <- regions do 
        withClue("Region" + region.toString + " is loaded, but shouldn't actually exist. ") {
          loadedRegions should contain (region)
        }
  }

end gameBackendTest
