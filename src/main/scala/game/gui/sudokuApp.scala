package game.gui

import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle
import scalafx.scene.control.{Menu, MenuBar, MenuItem}
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter
import game.GameHandler

object sudokuApp extends JFXApp3:
  private val fileChooser = new FileChooser
  private val gameHandler: GameHandler = null
  fileChooser.getExtensionFilters.add(new ExtensionFilter("JSON Files Only", "*.json"))

  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage:
      title = "Killer Sudoku"
      width = 600
      height = 450

    val root = Pane()
    val scene = Scene(parent = root)
    stage.scene = scene

    root.children += setUpMenuBar()

  private def setUpMenuBar(): MenuBar =
    val menuBar = new MenuBar()
    val optionsMenu = new Menu("File")

    // add a null-handler
    val openFile = new MenuItem("Open new game")
    openFile.onAction =
      (event) =>
        val selectedFile = fileChooser.showOpenDialog(stage)
        println("The selected file is: " + selectedFile)

    val saveFile = new MenuItem("Save game")
    saveFile.onAction =
      (event) =>
        val selectedLocation = fileChooser.showSaveDialog(stage)
        println("The selected file locaiton is: " + selectedLocation)

    val resetGame = new MenuItem("Reset progress")
    resetGame.onAction =
      (event) =>
        println("Resetting the game progress!")

    optionsMenu.getItems.addAll(resetGame, openFile, saveFile)
    menuBar.getMenus.add(optionsMenu)

    menuBar
end sudokuApp

