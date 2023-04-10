import game.{GameHandler, GridRegion}

import java.io.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.should

class gameBackendTest extends AnyFlatSpec with Matchers:
  private val regionTestData: Seq[(String, Array[GridRegion])] = Seq(
      ("src\\test\\scala\\testFiles\\example1.json", Array(
          GridRegion(14, Set((3,1), (3,2))),
          GridRegion(4,  Set((0,6), (1,6))),
          GridRegion(13, Set((4,1), (4,2), (5,1))),
          GridRegion(20, Set((4,3), (5,3), (6,3))),
          GridRegion(25, Set((1,0), (1,1), (2,0), (2,1))),
          GridRegion(16, Set((7,2), (8,2))),
          GridRegion(17, Set((8,7), (8,8))),
          GridRegion(15, Set((0,2), (0,3), (0,4))),
          GridRegion(6,  Set((5,2), (6,1), (6,2))),
          GridRegion(20, Set((5,5), (6,5), (6,6))),
          GridRegion(17, Set((1,2), (1,3))),
          GridRegion(13, Set((8,4), (8,5), (8,6))),
          GridRegion(27, Set((5,0), (6,0), (7,0), (8,0))),
          GridRegion(8,  Set((7,1), (8,1))),
          GridRegion(12, Set((4,8), (5,8))),
          GridRegion(15, Set((0,8), (1,8), (2,8), (3,8))),
          GridRegion(8,  Set((2,5), (3,5), (4,5))),
          GridRegion(22, Set((0,5), (1,4), (1,5), (2,4))),
          GridRegion(6,  Set((5,6), (5,7))),
          GridRegion(9,  Set((2,2), (2,3), (3,3))),
          GridRegion(17, Set((3,4), (4,4), (5,4))),
          GridRegion(15, Set((7,5), (7,6))),
          GridRegion(17, Set((3,7), (4,6), (4,7))),
          GridRegion(16, Set((0,7), (1,7))),
          GridRegion(3,  Set((0,0), (0,1))),
          GridRegion(20, Set((2,6), (2,7), (3,6))),
          GridRegion(14, Set((6,7), (7,7), (6,8), (7,8))),
          GridRegion(6,  Set((3,0), (4,0))),
          GridRegion(10, Set((7,3), (8,3), (6,4), (7,4)))
          )),
      ("src\\test\\scala\\testFiles\\example2.json", Array(
        GridRegion(9,  Set((2,5), (2,6))),
        GridRegion(13, Set((6,2), (7,2), (7,3))),
        GridRegion(13, Set((3,6), (3,7))),
        GridRegion(22, Set((6,6), (7,6), (7,7))),
        GridRegion(21, Set((4,6), (4,7), (5,7))),
        GridRegion(16, Set((2,1), (3,0), (3,1))),
        GridRegion(9,  Set((7,4), (8,4))),
        GridRegion(11, Set((2,7), (2,8))),
        GridRegion(12, Set((5,0), (5,1), (5,2))),
        GridRegion(16, Set((6,4), (6,5))),
        GridRegion(10, Set((0,2), (1,2), (2,2))),
        GridRegion(19, Set((5,3), (5,4), (6,3))),
        GridRegion(7,  Set((2,3), (3,3))),
        GridRegion(13, Set((1,7), (1,8))),
        GridRegion(17, Set((8,7), (8,8), (7,8))),
        GridRegion(18, Set((3,2), (4,2), (4,3))),
        GridRegion(11, Set((0,0), (0,1))),
        GridRegion(4,  Set((3,8), (4,8))),
        GridRegion(8,  Set((3,5), (4,5))),
        GridRegion(14, Set((0,6), (0,7), (0,8))),
        GridRegion(7,  Set((0,4), (1,4), (2,4))),
        GridRegion(6,  Set((5,5), (5,6))),
        GridRegion(10, Set((7,5), (8,5), (8,6))),
        GridRegion(13, Set((8,2), (8,3))),
        GridRegion(6,  Set((4,0), (4,1))),
        GridRegion(15, Set((0,5), (1,5), (1,6))),
        GridRegion(8,  Set((6,7), (6,8), (5,8))),
        GridRegion(11, Set((6,0), (6,1), (7,0))),
        GridRegion(16, Set((0,3), (1,3))),
        GridRegion(17, Set((7,1), (8,0), (8,1))),
        GridRegion(20, Set((1,0), (1,1), (2,0))),
        GridRegion(13, Set((3,4), (4,4))))),
      ("src\\test\\scala\\testFiles\\example3.json", Array(
        GridRegion(19, Set((4,3), (4,4), (4,5))),
        GridRegion(30, Set((7,0), (8,0), (7,1), (8,1))),
        GridRegion(4, Set((0,6), (1,6))),
        GridRegion(5, Set((7,2), (8,2))),
        GridRegion(6, Set((0,0), (0,1))),
        GridRegion(24, Set((0,7), (0,8), (1,7), (1,8))),
        GridRegion(18, Set((6,1), (6,2), (5,2), (4,2))),
        GridRegion(23, Set((5,5), (5,6), (6,5), (6,6))),
        GridRegion(23, Set((2,2), (3,3), (2,3), (3,2))),
        GridRegion(10, Set((7,3), (8,3), (7,4))),
        GridRegion(20, Set((0,2), (1,2), (1,3))),
        GridRegion(18, Set((2,4), (2,5), (3,4), (3,5))),
        GridRegion(7, Set((1,0), (2,0))),
        GridRegion(11, Set((8,4), (8,5))),
        GridRegion(8, Set((0,3), (0,4))),
        GridRegion(24, Set((5,3), (6,3), (5,4), (6,4))),
        GridRegion(9, Set((4,8), (5,8))),
        GridRegion(15, Set((0,5), (1,4), (1,5))),
        GridRegion(16, Set((1,1), (2,1))),
        GridRegion(8, Set((5,0), (6,0))),
        GridRegion(16, Set((7,5), (7,6), (8,6))),
        GridRegion(10, Set((3,1), (4,1), (5,1))),
        GridRegion(5, Set((2,8), (3,8))),
        GridRegion(20, Set((3,7), (4,7), (5,7))),
        GridRegion(20, Set((4,6), (3,6), (2,6), (2,7))),
        GridRegion(12, Set((6,8), (7,8))),
        GridRegion(5, Set((8,7), (8,8))),
        GridRegion(10, Set((3,0), (4,0))),
        GridRegion(9, Set((6,7), (7,7)))))
    )
  private val valuesTestData: Seq[(String, Map[(Int, Int), Int])] = Seq(
    ("src\\test\\scala\\testFiles\\example1Solved.json", Map(
      (0,0) -> 2, (0,1) -> 1, (0,2) -> 5, (0,3) -> 6, (0,4) -> 4, (0,5) -> 7, (0,6) -> 3, (0,7) -> 9, (0,8) -> 8,
      (1,0) -> 3, (1,1) -> 6, (1,2) -> 8, (1,3) -> 9, (1,4) -> 5, (1,5) -> 2, (1,6) -> 1, (1,7) -> 7, (1,8) -> 4,
      (2,0) -> 7, (2,1) -> 9, (2,2) -> 4, (2,3) -> 3, (2,4) -> 8, (2,5) -> 1, (2,6) -> 6, (2,7) -> 5, (2,8) -> 2,
      (3,0) -> 5, (3,1) -> 8, (3,2) -> 6, (3,3) -> 2, (3,4) -> 7, (3,5) -> 4, (3,6) -> 9, (3,7) -> 3, (3,8) -> 1,
      (4,0) -> 1, (4,1) -> 4, (4,2) -> 2, (4,3) -> 5, (4,4) -> 9, (4,5) -> 3, (4,6) -> 8, (4,7) -> 6, (4,8) -> 7,
      (5,0) -> 9, (5,1) -> 7, (5,2) -> 3, (5,3) -> 8, (5,4) -> 1, (5,5) -> 6, (5,6) -> 4, (5,7) -> 2, (5,8) -> 5,
      (6,0) -> 8, (6,1) -> 2, (6,2) -> 1, (6,3) -> 7, (6,4) -> 3, (6,5) -> 9, (6,6) -> 5, (6,7) -> 4, (6,8) -> 6,
      (7,0) -> 6, (7,1) -> 5, (7,2) -> 9, (7,3) -> 4, (7,4) -> 2, (7,5) -> 8, (7,6) -> 7, (7,7) -> 1, (7,8) -> 3,
      (8,0) -> 4, (8,1) -> 3, (8,2) -> 7, (8,3) -> 1, (8,4) -> 6, (8,5) -> 5, (8,6) -> 2, (8,7) -> 8, (8,8) -> 9
    )),
    ("src\\test\\scala\\testFiles\\example2.json", Map(
      (0,2) -> 1, (4,3) -> 3, (6,5) -> 9, (7,2) -> 4, (8,8) -> 7
    )),
    ("src\\test\\scala\\testFiles\\example3.json", Map(
      (0,8) -> 9, (3,3) -> 6, (3,5) -> 3, (4,2) -> 6, (5,1) -> 5, (7,7) -> 8, (8,1) -> 8
    ))
  )
  
  private def areTwoRegionsNeighbouring(region1: GridRegion, region2: GridRegion): Boolean =
    for
      (x1, y1) <- region1.getCells
      (x2, y2) <- region2.getCells
    do
      if math.abs(x1 - x2) + math.abs(y1 - y2) == 1 then return true
    false
  
  "GameHandler.loadGame(...)" should "load the regions of good JSON files properly. " in {
    for
      (path: String, regions: Array[GridRegion]) <- regionTestData
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
        withClue("Region " + region.toString + " is loaded, but shouldn't actually exist. ") {
          loadedRegions should contain (region)
        }
  }

  "GameHandler.loadGame(...)" should "load the values correctly, given a good test file" in {
    for
      (path: String, values: Map[(Int, Int), Int]) <- valuesTestData
    do
      val game = GameHandler.loadGame(path)
      for pos <- values.keys do
        withClue("Wrong value at " + pos + "; expected " + values(pos) + ", but loaded " + game.numberAt(pos._1, pos._2)){
          game.numberAt(pos._1, pos._2) should equal (values(pos))
        }
  }

  "GameHandler.loadGame(...)" should "color the adjacent regions differently" in {
    for
      path <- valuesTestData.unzip._1
    do
      val regionSet = GameHandler.loadGame(path).getGrid.getRegions
      for region <- regionSet do
        val regionNeighs = regionSet.filter(
          potentialNeighbour => areTwoRegionsNeighbouring(potentialNeighbour, region) &&
            potentialNeighbour != region
        )
        withClue("Region " + region + " has neighbour(s) of the same color: " + regionNeighs.filter( _.getColor == region.getColor )){
          regionNeighs.map( _.getColor ) should not contain (region.getColor)
        }
  }

  "GameHandler.loadGame(...)" should "" in {

  }
end gameBackendTest
