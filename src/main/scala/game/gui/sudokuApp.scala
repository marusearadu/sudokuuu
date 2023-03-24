package game.gui

import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{Background, BackgroundFill, ColumnConstraints, CornerRadii, GridPane, HBox, Pane, RowConstraints, VBox}
import scalafx.scene.paint.Color.*
import scalafx.scene.control.{Menu, MenuBar, MenuItem}
import game.GameHandler
import scalafx.geometry.Insets
import scalafx.scene.canvas.Canvas
import scalafx.scene.input.KeyCombination

object sudokuApp extends JFXApp3:
  private var gameHandler: GameHandler = null

  def getGameHandler: GameHandler = this.gameHandler
  def setGameHandler(newGameHandler: GameHandler) = gameHandler = newGameHandler
  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage:
      title = "Killer Sudoku"
      width = 800
      height = 540

    val root = Pane()
    val scene = Scene(parent = root)
    stage.scene = scene

//    val bottomBox = new HBox
//    val sideBox   = new VBox
//    val canvas    = new Canvas(400, 400)

    root.children += setUpMenuBar()
//    root.children += mainView
//    mainView.add(canvas   , 0, 0)
//    mainView.add(sideBox  , 1, 0)
//    mainView.add(bottomBox, 0, 1, 2, 1)

//    val column0 = new ColumnConstraints:
//      percentWidth = 65
//    val column1 = new ColumnConstraints:
//      percentWidth = 35
////    val row0    = new RowConstraints:
////      percentHeight = 6
//    val row1    = new RowConstraints:
//      percentHeight = 70
//    val row2    = new RowConstraints:
//      percentHeight = 20
//
//    mainView.columnConstraints = Array[ColumnConstraints](column0, column1)
//    mainView.rowConstraints    = Array[RowConstraints](row1, row2)

  private def setUpMenuBar(): MenuBar =
    val menuBar = new MenuBar{
      menus = Seq(
        new Menu("Game"){
          items = Seq(
            new MenuItem("Open new game"){
              onAction = (event) => helperFunctions.loadGame()
              accelerator = KeyCombination.keyCombination("Ctrl + O")
            },
            new MenuItem("Save game"){
              onAction = (event) => helperFunctions.saveGame()
              accelerator = KeyCombination.keyCombination("Ctrl + S")
            },
            new MenuItem("Save to..."){
              onAction = (event) => helperFunctions.saveGame(false)
            },
            new MenuItem("Reset progress"){
              accelerator = KeyCombination.keyCombination("Ctrl + R")
              onAction = (event) => helperFunctions.resetGame()
            },
            new MenuItem("Exit"){
              accelerator = KeyCombination.keyCombination("Ctrl + X")
              onAction = (event) => helperFunctions.exitGame()
            }
          )
        }
      )
    }
    menuBar.background = Background(Array(new BackgroundFill((Gray), CornerRadii.Empty, Insets.Empty)))
    menuBar

end sudokuApp
