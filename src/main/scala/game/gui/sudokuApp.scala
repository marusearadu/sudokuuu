package game.gui

import game.{BadFilePathException, CorruptedFileException, GameHandler, UnknownException}
import scalafx.application.JFXApp3
import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, Background, BackgroundFill, Border, BorderPane, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, GridPane, Pane, Region, StackPane}
import scalafx.scene.paint.Color.*
import scalafx.scene.control.{Alert, ButtonType, Label, Menu, MenuBar, MenuItem}
import scalafx.geometry.Insets
import scalafx.scene.input.{KeyCombination, MouseEvent}
import scalafx.stage.{FileChooser, Stage}
import scalafx.scene.text.{Font, FontWeight, Text}

object sudokuApp extends JFXApp3:
  private val fileChooser = new FileChooser
  fileChooser.getExtensionFilters.add(new FileChooser.ExtensionFilter("JSON Files Only", "*.json"))
  private val WINDOW_WIDTH  = 800
  private val WINDOW_HEIGHT = 540
  private var gameHandler: GameHandler = _

  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage:
      title     = "Hello Stage"
      width     = WINDOW_WIDTH
      height    = WINDOW_HEIGHT
      minWidth  = WINDOW_WIDTH
      maxWidth  = WINDOW_WIDTH
      minHeight = WINDOW_HEIGHT
      maxHeight = WINDOW_HEIGHT

    val root = new BorderPane:
      top    = setUpMenuBar()
      bottom = new Pane()          // TODO: IMPLEMENT THE BUTTONS
      right  = new Pane()          // TODO: IMPLEMENT THE ACCOUNTING BUBBLE
      left   = new Pane()          // TODO: PADDING (?)
      center = new StackPane{
        children = List(new Text("To start a game, double click here or go 'Game > Open...' and select a file."){font = Font("Times New Roman", FontWeight.Normal, 20)})
        onMouseClicked =
          (event: MouseEvent) => if event.clickCount == 2 then loadGame()
      }

    stage.scene = new Scene(parent = root, WINDOW_WIDTH, WINDOW_HEIGHT)

  private def setUpMenuBar(): MenuBar  =
    val menuBar = new MenuBar{
      menus = Seq(
        new Menu("Game"){
          items = Seq(
            new MenuItem("Open..."){
              onAction = (event) => loadGame()
              accelerator = KeyCombination.keyCombination("Ctrl + O")
            },
            new MenuItem("Save game"){
              onAction = (event) => saveGame()
              accelerator = KeyCombination.keyCombination("Ctrl + S")
            },
            new MenuItem("Save to..."){
              onAction = (event) => saveGame(false)
            },
            new MenuItem("Reset progress"){
              onAction = (event) => resetGame()
              accelerator = KeyCombination.keyCombination("Ctrl + R")
            },
            new MenuItem("Exit"){
              onAction = (event) => exitGame()
              accelerator = KeyCombination.keyCombination("Ctrl + X")
            }
          )
        }
      )
    }
    menuBar.background = Background(Array(new BackgroundFill((Gray), CornerRadii.Empty, Insets.Empty)))
    menuBar

  // TODO: FINISH THIS FUNCTION
  private def setUpBoard(): GridPane =
    def getBorderWidths(i: Int, j: Int): BorderWidths =
      new BorderWidths(
        if i % 3 == 0 then 3 else 2,
        if j % 3 == 2 then 3 else 2,
        if i % 3 == 2 then 3 else 2,
        if j % 3 == 0 then 3 else 2
      )

    val boardSquare = new GridPane()
    for
      i <- (0 to 8)
      j <- (0 to 8)
    do
      val smallSquare = new StackPane
      val insideText = new Text(i.toString):
        font = Font("Niagara Solid", FontWeight.SemiBold, 30)
      val smallSquareVisual = new Region:
        minWidth = 50
        maxWidth = 50
        minHeight = 50
        maxHeight = 50
        background = Background(Array(new BackgroundFill((Red), CornerRadii.Empty, Insets.Empty)))
      smallSquareVisual.border = new Border(new BorderStroke(Black, Black, Black, Black,
        BorderStrokeStyle.Solid, BorderStrokeStyle.Solid, BorderStrokeStyle.Solid, BorderStrokeStyle.Solid,
        CornerRadii.Empty, getBorderWidths(j, i), Insets.Empty))
      smallSquare.children ++= Seq(smallSquareVisual, insideText)
      boardSquare.add(smallSquare, i, j)
    boardSquare

  private def pushDialogue(stage: Stage, dialogueType: Alert.AlertType, alertTitle: String, header: String, description: String): Option[ButtonType] =
    new Alert(dialogueType) {
      initOwner(stage)
      title = alertTitle
      headerText = header
      contentText = description
    }.showAndWait()

  private def saveGame(samePlace: Boolean = true) =
    if gameHandler != null then
      try
        if GameHandler.getAddress != null && samePlace then
          GameHandler.saveGame(gameHandler)
        else
          val selectedLocation = fileChooser.showSaveDialog(stage)
          if selectedLocation != null then
            GameHandler.saveGame(gameHandler, selectedLocation.toString)
      catch
        case e: UnknownException => pushDialogue(stage, Alert.AlertType.Error, "Error", "Unknown exception", e.description)
        case e: Exception        => pushDialogue(stage, Alert.AlertType.Error, "Error", "Unknown exception", e.getMessage)
    else
      pushDialogue(stage, Alert.AlertType.Warning, "Warning!", "Can't save an empty game.",
        "Please first open a game in order to save it.")

  private def loadGame() =
    try
      val selectedFile = fileChooser.showOpenDialog(stage)
      if selectedFile != null then
        gameHandler = GameHandler.loadGame(selectedFile.toString)
        val newRoot = new BorderPane:
          top    = setUpMenuBar()
          bottom = new Pane()          // TODO: IMPLEMENT THE BUTTONS
          right  = new Pane()          // TODO: IMPLEMENT THE ACCOUNTING BUBBLE
          left   = new Pane()          // TODO: PADDING (?)
          center = setUpBoard()
        stage.scene = new Scene(parent = newRoot)
    catch
      case e: BadFilePathException   => pushDialogue(stage, Alert.AlertType.Error, "Error", "Bad File Path" , e.getMessage)
      case e: CorruptedFileException => pushDialogue(stage, Alert.AlertType.Error, "Error", "Corrupted File", e.description)
      case e: UnknownException       => pushDialogue(stage, Alert.AlertType.Error, "Error", "Unknown Exception", e.description)
      case e: Exception              => pushDialogue(stage, Alert.AlertType.Error, "Error", "Unknown Exception", e.getMessage)

  private def resetGame() =
    try
      gameHandler.resetGame()
    catch
      case e: NullPointerException => pushDialogue(stage, Alert.AlertType.Warning, "Warning!", "Can't reset an empty game.",
            "Please first open a game in order to reset it.")
      case e: Exception              => pushDialogue(stage, Alert.AlertType.Error, "Error", "Unkwonn Exception", e.getMessage)

  private def exitGame() =
    if gameHandler == null then
      stage.close()
    else
      val buttonSaveTo = new ButtonType("Save, then exit")
      val buttonExit   = new ButtonType("Exit without saving")
      new Alert(Alert.AlertType.Confirmation){
        initOwner(stage)
        title = "Confirm Exit"
        headerText = "Do you want to save your game before exiting?"
        buttonTypes = Seq(buttonSaveTo, buttonExit, ButtonType.Cancel)
      }.showAndWait() match
        case Some(`buttonSaveTo`) =>
          saveGame(false)
          stage.close()
        case Some(`buttonExit`)   =>
          stage.close()
        case _                  =>
          ()
end sudokuApp