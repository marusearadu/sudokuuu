import game.{GameHandler, GridRegion}

import java.io.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.should

class gameBackendTest extends AnyFlatSpec with Matchers:
  // READ THIS:
  // for GameHandler, the functionality regarding storing and retrieving values is trivial
  // hence, it is not tested.
  // similarly, the isGridCorrect, isGridFull methods are trivially designed
  // the same applies to all the GridCell & Grid methods
  private val regionTestData: Seq[(String, Array[GridRegion])] = Seq(
      ("src\\test\\testFiles\\example1.json", Array(
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
      ("src\\test\\testFiles\\example2.json", Array(
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
      ("src\\test\\testFiles\\example3.json", Array(
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
    ("src\\test\\testFiles\\example1Solved.json", Map(
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
    ("src\\test\\testFiles\\example2.json", Map(
      (0,2) -> 1, (4,3) -> 3, (6,5) -> 9, (7,2) -> 4, (8,8) -> 7
    )),
    ("src\\test\\testFiles\\example3.json", Map(
      (0,8) -> 9, (3,3) -> 6, (3,5) -> 3, (4,2) -> 6, (5,1) -> 5, (7,7) -> 8, (8,1) -> 8
    ))
  )
  private val bubbleTestData: Seq[(String, Map[(Int, Int), Set[Array[Int]]])] = Seq(
    ("src\\test\\testFiles\\example2.json", Map(
      (7, 0) -> Set(Array(1, 2, 8), Array(1, 3, 7), Array(1, 4, 6), Array(2, 3, 6), Array(2, 4, 5)),
      (2, 7) -> Set(Array(2, 9), Array(3, 8), Array(4, 7), Array(5, 6)),
      (8, 7) -> Set(Array(1, 9), Array(2, 8), Array(4, 6)),
      (3, 3) -> Set(Array(1, 6), Array(2, 5), Array(3, 4)),
      (0, 8) -> Set(Array(1, 4, 9), Array(1, 5, 8), Array(1, 6, 7), Array(2, 3, 9), Array(2, 4, 8), Array(2, 5, 7), Array(3, 4, 7), Array(3, 5, 6)),
      (4, 6) -> Set(Array(4, 8, 9), Array(5, 7, 9), Array(6, 7, 8))
    )),
    ("src\\test\\testFiles\\example3.json", Map(
      (4,1) -> Set(Array(2,3), Array(1,4)),
      (1,8) -> Set(Array(1,6,8), Array(4,5,6), Array(3,4,8), Array(2,5,8), Array(2,6,7), Array(3,5,7)),
      (5,2) -> Set(Array(2,4,6), Array(3,4,5), Array(1,4,7), Array(1,5,6), Array(2,5,5), Array(2,3,7), Array(1,3,8), Array(2,2,8), Array(1,2,9)),
      (8,1) -> Set(Array(6,7,9)),
      (0,5) -> Set(Array(1,6,8), Array(4,5,6), Array(3,4,8), Array(2,5,8), Array(1,5,9), Array(2,4,9), Array(2,6,7), Array(3,5,7)),
      (3,2) -> Set(Array(1,7,9), Array(3,7,7), Array(4,4,9), Array(4,6,7), Array(1,8,8), Array(3,5,9), Array(2,7,8), Array(2,6,9), Array(5,5,7), Array(4,5,8), Array(5,6,6), Array(3,6,8)),
      (1,7) -> Set(Array(1,6,8), Array(4,5,6), Array(3,4,8), Array(2,5,8), Array(2,6,7), Array(3,5,7)),
      (8,0) -> Set(Array(6,7,9)),
      (5,6) -> Set(Array(2,5,7,9), Array(1,6,7,9), Array(2,6,7,8), Array(1,5,8,9), Array(3,5,7,8), Array(3,4,7,9), Array(4,4,7,8), Array(2,6,6,9), Array(4,5,6,8), Array(3,6,6,8), Array(3,6,7,7), Array(4,6,6,7), Array(4,4,6,9), Array(1,7,7,8), Array(4,5,7,7), Array(3,5,6,9), Array(2,3,9,9), Array(5,5,6,7), Array(1,6,8,8), Array(3,4,8,8), Array(2,7,7,7), Array(2,4,8,9), Array(5,5,5,8), Array(5,6,6,6), Array(1,4,9,9), Array(3,3,8,9), Array(4,5,5,9), Array(2,5,8,8)),
      (2,2) -> Set(Array(1,7,9), Array(3,7,7), Array(4,4,9), Array(4,6,7), Array(1,8,8), Array(3,5,9), Array(2,7,8), Array(2,6,9), Array(5,5,7), Array(4,5,8), Array(5,6,6), Array(3,6,8)),
      (5,4) -> Set(Array(1,6,8,9), Array(4,6,7,7), Array(5,5,6,8), Array(1,7,8,8), Array(2,5,8,9), Array(5,6,6,7), Array(2,6,7,9), Array(3,5,7,9), Array(3,6,6,9), Array(4,6,6,8), Array(2,7,7,8), Array(3,6,7,8), Array(4,4,7,9), Array(4,5,7,8), Array(2,4,9,9), Array(1,7,7,9), Array(3,4,8,9), Array(4,5,6,9), Array(2,6,8,8), Array(1,5,9,9), Array(3,5,8,8)),
      (4,8) -> Set(Array(4,5), Array(1,8), Array(2,7), Array(3,6)),
      (8,5) -> Set(Array(3,8), Array(2,9), Array(4,7), Array(5,6)),
      (3,4) -> Set(Array(1,7,7), Array(1,6,8), Array(3,4,8), Array(2,5,8), Array(1,5,9), Array(2,4,9), Array(2,6,7), Array(3,5,7), Array(4,5,6), Array(4,4,7)),
      (2,1) -> Set(Array(7,9)),
      (4,7) -> Set(Array(3,8,9), Array(5,7,8), Array(4,7,9), Array(5,6,9)),
      (7,8) -> Set(Array(3,9), Array(5,7), Array(4,8)),
      (7,5) -> Set(Array(2,5,9), Array(2,7,7), Array(3,4,9), Array(4,5,7), Array(4,4,8), Array(1,7,8), Array(5,5,6), Array(1,6,9), Array(4,6,6), Array(3,6,7), Array(3,5,8), Array(2,6,8)),
      (6,1) -> Set(Array(2,4,6), Array(3,4,5), Array(1,4,7), Array(1,5,6), Array(2,5,5), Array(2,3,7), Array(1,3,8), Array(2,2,8), Array(1,2,9)),
      (2,5) -> Set(Array(1,7,7), Array(1,6,8), Array(3,4,8), Array(2,5,8), Array(1,5,9), Array(2,4,9), Array(2,6,7), Array(3,5,7), Array(4,5,6), Array(4,4,7))
    ))
  )
  private val cellsVTestData: Seq[(String, Map[(Int, Int), Set[Int]])] = Seq(
    ("src\\test\\testFiles\\example1.json", Map(
      (4,1) -> Set(5, 1, 6, 9, 2, 7, 3, 8, 4), (1,8) -> Set(5, 1, 6, 9, 2, 7, 3, 8, 4),
      (8,2) -> Set(7, 9), (5,2) -> Set(1, 4, 2, 3), (8,1) -> Set(5, 1, 6, 2, 7, 3),
      (0,5) -> Set(5, 1, 6, 9, 2, 7, 3, 8, 4), (3,2) -> Set(5, 9, 6, 8),
      (1,7) -> Set(7, 9), (8,0) -> Set(5, 6, 9, 7, 3, 8, 4), (5,6) -> Set(2, 4, 1, 5),
      (2,2) -> Set(5, 1, 6, 2, 7, 3, 4), (5,4) -> Set(5, 1, 6, 9, 2, 7, 3, 8, 4),
      (4,8) -> Set(5, 9, 7, 3, 8, 4), (8,5) -> Set(5, 1, 6, 9, 2, 7, 3, 8, 4),
      (3,4) -> Set(5, 1, 6, 9, 2, 7, 3, 8, 4), (2,1) -> Set(5, 1, 6, 9, 2, 7, 3, 8, 4),
      (4,7) -> Set(5, 1, 6, 9, 2, 7, 3, 8, 4), (7,8) -> Set(5, 1, 6, 2, 7, 3, 8, 4),
      (7,5) -> Set(7, 8, 6, 9), (6,1) -> Set(1, 4, 2, 3), (2,5) -> Set(5, 1, 2, 3, 4),
      (4,2) -> Set(5, 1, 6, 9, 2, 7, 3, 8, 4), (4,4) -> Set(5, 1, 6, 9, 2, 7, 3, 8, 4),
      (2,0) -> Set(5, 1, 6, 9, 2, 7, 3, 8, 4), (2,6) -> Set(5, 6, 9, 2, 7, 3, 8, 4),
      (6,0) -> Set(5, 6, 9, 7, 3, 8, 4), (8,4) -> Set(5, 1, 6, 9, 2, 7, 3, 8, 4),
      (1,2) -> Set(8, 9), (6,5) -> Set(5, 6, 9, 2, 7, 3, 8, 4), (6,2) -> Set(1, 4, 2, 3)
    )),
    ("src\\test\\testFiles\\example2.json", Map(
      (2,4) -> Set(1, 2, 4), (4,7) -> Set(5, 6, 9, 7, 8, 4), (7,8) -> Set(1, 6, 9, 2, 8),
      (7,5) -> Set(5, 1, 6, 2, 7, 3, 8), (6,1) -> Set(5, 1, 6, 2, 7, 3, 8),
      (2,5) -> Set(5, 1, 6, 2, 7, 3, 8, 4), (4,2) -> Set(7, 8, 6, 9),
      (4,4) -> Set(5, 6, 9, 7, 8, 4), (2,0) -> Set(5, 6, 9, 7, 3, 8, 4),
      (2,6) -> Set(5, 1, 6, 2, 7, 3, 8, 4), (6,0) -> Set(5, 1, 6, 2, 7, 3, 8),
      (8,4) -> Set(5, 1, 6, 2, 3, 8, 4), (1,2) -> Set(5, 6, 2, 7, 3), (6,5) -> Set(7),
      (6,2) -> Set(5, 6, 2, 7, 3, 8), (8,2) -> Set(5, 6, 9, 8), (1,4) -> Set(1, 2, 4),
      (4,5) -> Set(5, 1, 6, 2, 7), (5,8) -> Set(5, 1, 6, 2, 3, 4), (4,6) -> Set(5, 6, 9, 7, 8, 4),
      (0,1) -> Set(5, 6, 9, 2, 7, 3, 8, 4), (1,1) -> Set(5, 6, 9, 7, 3, 8, 4),
      (6,7) -> Set(5, 1, 6, 2, 3, 4), (0,4) -> Set(2, 4), (2,7) -> Set(5, 6, 9, 2, 7, 3, 8, 4),
      (1,6) -> Set(5, 1, 6, 9, 2, 7, 3, 8, 4), (7,7) -> Set(5, 6, 9, 8), (3,8) -> Set(1, 3),
      (3,0) -> Set(5, 1, 6, 9, 2, 7, 3, 8, 4), (8,3) -> Set(5, 6, 8, 4)
    )),
    ("src\\test\\testFiles\\example3.json", Map(
      (3,7) -> Set(5, 9, 7, 4), (6,3) -> Set(5, 1, 9, 2, 7, 3, 8, 4), (8,8) -> Set(2, 3, 1, 4),
      (6,4) -> Set(5, 1, 6, 9, 2, 7, 3, 8, 4), (2,8) -> Set(2, 3, 1, 4),
      (1,5) -> Set(5, 1, 6, 9, 2, 7, 8, 4), (8,6) -> Set(5, 1, 6, 9, 2, 7, 3, 4),
      (1,0) -> Set(5, 1, 6, 2, 3, 4), (3,1) -> Set(2, 1, 4), (7,2) -> Set(2, 3, 1, 4), (7,1) -> Set(6, 7, 9),
      (0,0) -> Set(2, 4, 1, 5), (5,0) -> Set(1, 2, 7, 3), (6,8) -> Set(5, 7, 3, 4), (8,7) -> Set(2, 3, 1, 4),
      (4,0) -> Set(1, 9, 2, 7, 3, 8, 4), (3,3) -> Set(5, 1, 9, 2, 7, 8, 4), (0,7) -> Set(5, 1, 6, 2, 7, 3, 4),
      (4,3) -> Set(5, 9, 2, 7, 8, 4), (5,1) -> Set(2, 3, 1, 4), (3,6) -> Set(5, 1, 9, 2, 7, 8, 4),
      (2,3) -> Set(5, 1, 9, 2, 7, 3, 8, 4), (3,5) -> Set(5, 1, 9, 2, 7, 8, 4), (7,3) -> Set(5, 1, 2, 7, 3, 4),
      (7,0) -> Set(6, 7, 9), (1,3) -> Set(5, 9, 2, 7, 3, 8, 4), (5,5) -> Set(1, 9, 2, 7, 8, 4),
      (0,8) -> Set(5, 1, 6, 2, 7, 3, 8, 4), (0,6) -> Set(1, 3), (0,3) -> Set(5, 1, 2, 7, 3), (5,7) -> Set(6, 9, 7, 3, 4),
      (5,3) -> Set(1, 9, 2, 7, 8, 4), (7,4) -> Set(5, 1, 6, 2, 7, 3, 4), (6,6) -> Set(5, 1, 6, 9, 2, 7, 3, 4),
      (0,2) -> Set(5, 2, 7, 3, 8, 4), (7,6) -> Set(5, 1, 6, 9, 2, 7, 3, 4)
    ))
  )
  private val savingTestData: Seq[(String, String)] = Seq(
    ("src\\test\\testFiles\\example1.json", "src\\test\\saveFiles\\example1.json"),
    ("src\\test\\testFiles\\example2.json", "src\\test\\saveFiles\\example2.json"),
    ("src\\test\\testFiles\\example3.json", "src\\test\\saveFiles\\example3.json")
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

  it should "load the values correctly, given a good test file." in {
    for
      (path: String, values: Map[(Int, Int), Int]) <- valuesTestData
    do
      val game = GameHandler.loadGame(path)
      for pos <- values.keys do
        withClue("Wrong value at " + pos + "; expected " + values(pos) + ", but loaded " + game.numberAt(pos._1, pos._2)){
          game.numberAt(pos._1, pos._2) should equal (values(pos))
        }
  }

  it should "color the adjacent regions differently." in {
    for
      path <- valuesTestData.map(_._1)
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

  "gameHandler" should "display the correct split-sums." in {
    for
      (path: String, bubbleMap: Map[(Int, Int), Set[Array[Int]]]) <- bubbleTestData
    do
      val game = GameHandler.loadGame(path)
      for (pos, values) <- bubbleMap do
        game.select(pos)
        val gameValues = game.getBubble.map( _.toSet )
        for gameValue <- gameValues do
          withClue("The possible sum-splits of position " + pos + " don't contain the value " + gameValue.mkString(" + ")){
            values.map( _.toSet ) should contain (gameValue)
          }
        for value <- values.map( _.toSet) do
          withClue("The possible sum-splits of position " + pos + " shouldn't contain the value " + value.mkString(" + ")){
            gameValues should contain (value)
          }
  }

  it should "display the correct possible values for a given cell. " in {
    for
      (path: String, cellValues: Map[(Int, Int), Set[Int]]) <- cellsVTestData
    do
      val game = GameHandler.loadGame(path)
      for (pos, posVals) <- cellValues do
        val gamePosVals = game.possibleValuesAt(pos)
        for gamePosVal <- gamePosVals do
          withClue("Value " + gamePosVal + " is suggested by the game, shouldn't actually be. "){
            posVals should contain (gamePosVal)
          }
        for posVal <- posVals do
          withClue("Value " + posVal + " is not suggested by the game, should actually be. "){
            gamePosVals should contain (posVal)
          }
  }

  // gameSaving is all about making sure that reopening a (game-)file
  // will result in the same state of the game as when it was saved
  // so, saving and then reopening a file should do the trick
  "GameHandler.saveGame(...)" should "save the game in the correct format" in {
    import scala.util.Random.nextInt
    for
      (fromPath: String, toPath: String) <- savingTestData
    do
      val oldGame = GameHandler.loadGame(fromPath)
      // randomly inserting some values into the game
      (0 until 50).foreach(i => oldGame.insertValueAt(nextInt(10), nextInt(9), nextInt(9)))
      GameHandler.saveGame(oldGame, toPath)
      val openedGame = GameHandler.loadGame(toPath)
      for
        x <- 0 to 8
        y <- 0 to 8
      do
        withClue("Values at " + (x, y) + " do not coincide: expected" +
          oldGame.numberAt(x, y) + ", received " + openedGame.numberAt(x, y)){
          oldGame.numberAt(x, y) should equal (openedGame.numberAt(x, y))
        }
  }
end gameBackendTest
