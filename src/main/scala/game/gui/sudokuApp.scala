package game.gui

import game.{BadFilePathException, CorruptedFileException, GameHandler, GridCell, UnknownException}
import scalafx.application.{JFXApp3, Platform}
import scalafx.Includes.*
import scalafx.beans.property.{BooleanProperty, IntegerProperty, ObjectProperty, ReadOnlyObjectWrapper}
import scalafx.scene.{Group, Scene}
import scalafx.scene.layout.{AnchorPane, Background, BackgroundFill, Border, BorderPane, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, GridPane, Pane, Region, StackPane, TilePane, VBox}
import scalafx.scene.paint.Color.*
import scalafx.scene.control.{Alert, Button, ButtonType, Label, Menu, MenuBar, MenuItem, ScrollPane, TextArea}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.stage.{FileChooser, Stage}
import scalafx.scene.text.{Font, FontWeight, Text, TextFlow}
import javafx.beans.property.SimpleObjectProperty

// TODO: add a 'check board button' to the game
//  and why is there a delay when playing
//  SWITCH UP GAME COLORING
object sudokuApp extends JFXApp3:
  // now i do realize that probably just inserting
  // the whole GameHandler object into an ObjectProperty
  // would probably boost efficiency
  // however, one needs to self-handedly implement a listener
  // so that when the internal state of gameHandler changes
  // it fires some event
  // AND THERE'S PRACTICALLY NO DOCUMENTATION ON THIS TOPIC WHATSOEVER
  // and, well
  // too big of a pain to discover this
  // so damn it
  private val fileChooser                  = new FileChooser
  fileChooser.getExtensionFilters.add(new FileChooser.ExtensionFilter("JSON Files Only", "*.json"))
  private val selectedPos                  = ObjectProperty((-1, -1))
  private var valuesInTheSquare: Array[Array[IntegerProperty]] = ( (0 until 9).map( i => (0 until 9).map( j => IntegerProperty(0) ).toArray ).toArray )
  private val WINDOW_WIDTH                 = 960
  private val WINDOW_HEIGHT                = 720
  private val SQUARE_SIZE                  = 50
  private val BOTTOM_ROW_PADDING           = 10
  private var gameHandler: GameHandler     = _
  private var numberedButtons: Seq[Button] = _

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
      focusTraversable = true
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
    setUpControls()

  private def setUpMenuBar(): MenuBar  =
    val menuBar = new MenuBar{
      menus = Seq(
        new Menu("Game"){
          items = Seq(
            new MenuItem("Open..."){
              onAction = (event) => loadGame()
            },
            new MenuItem("Save game"){
              onAction = (event) => saveGame()
            },
            new MenuItem("Save to..."){
              onAction = (event) => saveGame(false)
            },
            new MenuItem("Reset progress"){
              onAction = (event) => resetGame()
            },
            new MenuItem("Exit"){
              onAction = (event) => exitGame()
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
      valuesInTheSquare(i)(j).value = gameHandler.getGrid.getGridCells(j)(i).getValue
      val insideText  = new Text(if valuesInTheSquare(i)(j).value == 0 then " " else valuesInTheSquare(i)(j).value.toString):
        font = Font("Niagara Solid", FontWeight.SemiBold, 30)
      valuesInTheSquare(i)(j).addListener(
        (_, _, newValue) =>
          gameHandler.insertValue(newValue.intValue())
          insideText.text = if valuesInTheSquare(i)(j).value == 0 then " " else valuesInTheSquare(i)(j).value.toString
      )

      val smallSquare = new StackPane:
        style     <== when(selectedPos === (i, j)) choose "-fx-background-color: green;" otherwise "-fx-background-color: " + gameHandler.getGrid.getRegionsMap((i, j)) + ";"
        onMouseEntered = (_) => gameHandler.possibleValuesAt((i, j)).foreach( i => numberedButtons(i - 1).style = "-fx-background-color: yellow ;")
        onMouseExited  = (_) => gameHandler.possibleValuesAt((i, j)).foreach( i => numberedButtons(i - 1).style = "-fx-background-color: #b8c6db;")
        onMouseClicked = (_) => selectedPos.value = (i, j)

      val smallSquareVisual = new Region:
        minWidth  = SQUARE_SIZE
        maxWidth  = SQUARE_SIZE
        minHeight = SQUARE_SIZE
        maxHeight = SQUARE_SIZE
      smallSquareVisual.border = new Border(new BorderStroke(Black, Black, Black, Black,
        BorderStrokeStyle.Solid, BorderStrokeStyle.Solid, BorderStrokeStyle.Solid, BorderStrokeStyle.Solid,
        CornerRadii.Empty, getBorderWidths(i, j), Insets.Empty))

      smallSquare.children ++= Seq(smallSquareVisual, insideText)
      boardSquare.add(smallSquare, j, i)
      boardSquareArray(i)(j) = smallSquare
    for
      (x, y) <- gameHandler.getGrid.getRegions.map( region => region.getCells.minBy((_ + _)) )
    do
      val anchor = new AnchorPane
      val text   = new Text(gameHandler.getGrid.getGridCells(x)(y).getRegion.getSum.toString):
        font = Font("Niagara Solid", FontWeight.Normal, 15)
      anchor.children += text
      AnchorPane.setTopAnchor(text, 5)
      AnchorPane.setLeftAnchor(text, 5)
      boardSquareArray(x)(y).children += anchor
    boardSquare

  private def setUpBottom(): TilePane =
    val buttons = new TilePane:
      margin  = Insets(20)

    numberedButtons   = (1 to 9).toSeq.map( i => new Button(i.toString){
      padding  = Insets(BOTTOM_ROW_PADDING)
      style    = "-fx-background-color: #b8c6db;"
      focusTraversable = false
      onAction = ((_) => if selectedPos.value != (-1, -1) then updateTable(i))})
    buttons.children  = numberedButtons
    buttons.children += new Button("Delete"):
      padding = Insets(BOTTOM_ROW_PADDING, 2, BOTTOM_ROW_PADDING, 2)
      focusTraversable = false
      style    = "-fx-background-color: #b8c6db;"
      onAction = (event) => if selectedPos.value != (-1, -1) then updateTable(0)
    buttons.children += new Button("Check"):
      padding = Insets(BOTTOM_ROW_PADDING, 2, BOTTOM_ROW_PADDING, 2)
      margin  = Insets(0, 0, 0, 15)
      focusTraversable = false
      style    = "-fx-background-color: #b8c6db;"
      onAction = (event) => if selectedPos.value != (-1, -1) then updateTable(0)

    buttons

  private def updateTable(i: Int) = valuesInTheSquare(selectedPos.value._1)(selectedPos.value._2).value = i

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

    val title = new Text("Possible sum-splits for the selected region: \n(note: only possible permutations shown)"):
      font = Font("Times New Roman", FontWeight.ExtraBold, 19)
      translateX = 10
      translateY = 25

    val contentVBox = new VBox:
      children = gameHandler.getBubble.map( x => new Text(x.mkString(" + ")){font = Font("Times New Roman", FontWeight.Normal, 18)} ).toSeq

    val textScrollPane = new ScrollPane:
      translateX = 12
      translateY = 53
      style      = "-fx-background: yellow"
      content    = contentVBox
      prefWidth  = 350
      prefHeight = 330

    selectedPos.onChange(
      (_, _, newValue) =>
        if newValue != (-1, -1) then gameHandler.select(newValue) else gameHandler.deselect()
        contentVBox.children = gameHandler.getBubble.toSeq.map( arr => arr.sorted ).sortBy(arr => (arr(0), arr(1)) ).map(
            x => new Text(x.mkString(" + ")){font = Font("Times New Roman", FontWeight.Normal, 18)}
          )
    )

    valuesInTheSquare.foreach(
      x => x.foreach(
        y => y.addListener(
          (_, _, newValue) => contentVBox.children = gameHandler.getBubble.map( x => new Text(x.mkString(" + ")){font = Font("Times New Roman", FontWeight.Normal, 18)} ).toSeq
        )
      )
    )

    new StackPane():
      children = Seq(new Group(bubbleVisual, title, textScrollPane))

  private def setUpControls(): Unit =
    stage.scene.value.addEventFilter(KeyEvent.KeyPressed,
      (event: KeyEvent) =>
        if selectedPos.value != (-1, -1) then
          event.code match
            case key if key.isDigitKey => updateTable(key.getName.last.asDigit)
            case KeyCode.Left          => selectedPos.value = (selectedPos.value._1, math.max(0, selectedPos.value._2 - 1))
            case KeyCode.Right         => selectedPos.value = (selectedPos.value._1, math.min(8, selectedPos.value._2 + 1))
            case KeyCode.Up            => selectedPos.value = (math.max(0, selectedPos.value._1 - 1), selectedPos.value._2)
            case KeyCode.Down          => selectedPos.value = (math.min(8, selectedPos.value._1 + 1), selectedPos.value._2)
            case KeyCode.BackSpace     => updateTable(0)
            //            case KeyCode.Escape        => selectedPos.value = (-1, -1)
            //          TODO: I want to add this functionality but it looks like every time after escaping it takes 2 clicks to select a region
            case _                     => ()
    )

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
        valuesInTheSquare = ( (0 until 9).map( i => (0 until 9).map( j => IntegerProperty(0) ).toArray ).toArray )
        selectedPos.value = (-1, -1)
        gameHandler = GameHandler.loadGame(selectedFile.toString)

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
      selectedPos.value = (-1, -1)
      valuesInTheSquare.foreach(
        arr => arr.foreach(
          intProp =>
            intProp.value = 0
        )
      )
    catch
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
