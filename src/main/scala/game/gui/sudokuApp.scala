package game.gui

import game.{BadFilePathException, CorruptedFileException, GameHandler, GridCell, UnknownException}
import scalafx.application.{JFXApp3, Platform}
import scalafx.Includes.*
import scalafx.beans.property.{IntegerProperty, ObjectProperty}
import scalafx.scene.{Group, Scene}
import scalafx.scene.layout.{AnchorPane, Background, BackgroundFill, Border, BorderPane, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, GridPane, Pane, Region, StackPane, TilePane, VBox}
import scalafx.scene.paint.Color.*
import scalafx.scene.control.{Alert, Button, ButtonType, Label, Menu, MenuBar, MenuItem, ScrollPane, TextArea}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.input.{KeyCombination, MouseEvent}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.stage.{FileChooser, Stage}
import scalafx.scene.text.{Font, FontWeight, Text, TextFlow}
import javafx.beans.property.SimpleObjectProperty

import scala.language.postfixOps

object sudokuApp extends JFXApp3:
  private val fileChooser              = new FileChooser
  fileChooser.getExtensionFilters.add(new FileChooser.ExtensionFilter("JSON Files Only", "*.json"))
  private val selectedPos              = ObjectProperty((-1, -1))
  private val bubbleSums               = ObjectProperty(Set[Array[Int]]())
  private val WINDOW_WIDTH             = 960
  private val WINDOW_HEIGHT            = 720
  private val SQUARE_SIZE              = 50
  private var gameHandler: SimpleObjectProperty[GameHandler] = _

  // TODO: color the whole background in one color
  //  and change cells coloring
  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage:
      title     = "Killer Sudoku"
      width     = WINDOW_WIDTH
      height    = WINDOW_HEIGHT
      minWidth  = WINDOW_WIDTH
      maxWidth  = WINDOW_WIDTH
      minHeight = WINDOW_HEIGHT
      maxHeight = WINDOW_HEIGHT

    val root = new BorderPane:
      top    = setUpMenuBar()
      bottom = new Pane()
      right  = new Pane()
      left   = new Pane()
      center = new StackPane{
        children = List(new Text("To start a game, double click here or go 'Game > Open...' and select a file."){font = Font("Times New Roman", FontWeight.Normal, 18)})
        onMouseClicked =
          (event: MouseEvent) => if event.clickCount == 2 then loadGame()
      }
      id     = "abecedar"

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

  private def setUpBoard(): GridPane =
    def getBorderWidths(i: Int, j: Int): BorderWidths =
      new BorderWidths(
        if i % 3 == 0 then 3 else 2,
        if j % 3 == 2 then 3 else 2,
        if i % 3 == 2 then 3 else 2,
        if j % 3 == 0 then 3 else 2
      )
    val boardSquare = new GridPane():
      alignment = Pos.Center
    var boardSquareArray = (0 to 8).toArray.map( x => Array.ofDim[StackPane](9) )
    for
      i <- (0 to 8)
      j <- (0 to 8)
    do
      val smallSquare = new StackPane:
        onMouseClicked = (event) =>
          gameHandler.value.select((i, j))
          bubbleSums.value  = gameHandler.value.getBubble
          selectedPos.value = (i, j)

      val inside      = ObjectProperty(gameHandler.value.getGrid.getGridCells(j)(i).getValue)
      val insideText  = new Text(if inside.value == 0 then " " else inside.value.toString):
        font = Font("Niagara Solid", FontWeight.SemiBold, 30)
      inside.onChange((_, _, newValue) => gameHandler.value.getGrid.getGridCells(j)(i).setValue(newValue))

      val smallSquareVisual = new Region:
        minWidth  = SQUARE_SIZE
        maxWidth  = SQUARE_SIZE
        minHeight = SQUARE_SIZE
        maxHeight = SQUARE_SIZE
        style     <== when(selectedPos === (i, j)) choose "-fx-background-color: green;" otherwise "-fx-background-color: " + gameHandler.value.getGrid.getRegionsMap((i, j)) + ";"
      smallSquareVisual.border = new Border(new BorderStroke(Black, Black, Black, Black,
        BorderStrokeStyle.Solid, BorderStrokeStyle.Solid, BorderStrokeStyle.Solid, BorderStrokeStyle.Solid,
        CornerRadii.Empty, getBorderWidths(i, j), Insets.Empty))

      smallSquare.children ++= Seq(smallSquareVisual, insideText)
      boardSquare.add(smallSquare, j, i)
      boardSquareArray(i)(j) = smallSquare
    for
      (x, y) <- gameHandler.value.getGrid.getRegions.map( region => region.getCells.minBy((_ + _)) )
    do
      val anchor = new AnchorPane
      val text   = new Text(gameHandler.value.getGrid.getGridCells(x)(y).getRegion.getSum.toString):
        font = Font("Niagara Solid", FontWeight.Normal, 15)
      anchor.children += text
      AnchorPane.setTopAnchor(text, 5)
      AnchorPane.setLeftAnchor(text, 5)
      boardSquareArray(x)(y).children += anchor
    boardSquare

  // TODO: link the buttons to the methods
  //   and set up the padding/margin values as private vals
  //   and set up button coloring on cell hovering
  private def setUpBottom(): TilePane =
    val buttons = new TilePane:
      margin  = Insets(10)
    for i <- 1 to 9 do
      val nrButton = new Button(i.toString):
        padding = Insets(10)
        onAction =
          (_) =>
            gameHandler.value.insertValue(i)
            println("changed cell")
      buttons.children += nrButton
    buttons.children += new Button("Delete entry"):
      padding = Insets(10, 0, 10, 0)
      margin  = Insets(0, 0, 0, 20)
    buttons

  // prety much done?
  private def setUpBubble(): StackPane =
    val bubbleVisual = new Region:
      minWidth = 400
      maxWidth = 400
      minHeight = 400
      maxHeight = 400
      background = Background(Array(new BackgroundFill((Yellow), CornerRadii(15), Insets(0, 20, 0, 0))))
    bubbleVisual.border = new Border(new BorderStroke(Black, Black, Black, Black,
      BorderStrokeStyle.Solid, BorderStrokeStyle.Solid, BorderStrokeStyle.Solid, BorderStrokeStyle.Solid,
      CornerRadii(5), BorderWidths(10),
      Insets(0, 20, 0, 0)))

    val title = new Text("Possible sum-splits for the selected region: "):
      font = Font("Times New Roman", FontWeight.ExtraBold, 19)
      translateX = 10
      translateY = 25

    val textScrollPane = new ScrollPane:
      translateX = 12
      translateY = 34
      style      = "-fx-background: yellow"
      content    = new VBox:
        children = gameHandler.value.getBubble.map( x => new Text(x.mkString(" + ")) ).toSeq
      prefWidth  = 350
      prefHeight = 350

    bubbleSums.onChange(
      (_, _, newValue) =>
      textScrollPane.content = new VBox:
        children = bubbleSums.getValue.toSeq.sortBy( _.min ).map(
          x => new Text(x.mkString(" + ")):
            font = Font("Times New Roman", FontWeight.Normal, 18)
        )
    )

    new StackPane():
      children = Seq(new Group(bubbleVisual, title, textScrollPane))

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
          GameHandler.saveGame(gameHandler.value)
        else
          val selectedLocation = fileChooser.showSaveDialog(stage)
          if selectedLocation != null then
            GameHandler.saveGame(gameHandler.value, selectedLocation.toString)
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
        gameHandler = new SimpleObjectProperty[GameHandler](GameHandler.loadGame(selectedFile.toString))
        gameHandler.addListener((_, oldValue, newValue) => println("Changed!"))

        val myBorderPane = stage.scene.getValue.lookup("#abecedar").asInstanceOf[javafx.scene.layout.BorderPane]
        myBorderPane.center = setUpBoard()
        myBorderPane.right  = setUpBubble()
        myBorderPane.bottom = setUpBottom()
    catch
      case e: BadFilePathException   => pushDialogue(stage, Alert.AlertType.Error, "Error", "Bad File Path" , e.getMessage)
      case e: CorruptedFileException => pushDialogue(stage, Alert.AlertType.Error, "Error", "Corrupted File", e.description)
      case e: UnknownException       => pushDialogue(stage, Alert.AlertType.Error, "Error", "Unknown Exception", e.description)
      case e: Exception              => pushDialogue(stage, Alert.AlertType.Error, "Error", "Unknown Exception", e.getMessage)

  private def resetGame() =
    try
      gameHandler.value.resetGame()
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
