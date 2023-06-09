package game.gui

import scala.collection.mutable.{Map as MMap, Set as MSet}
import game.{BadFilePathException, CorruptedFileException, GameHandler, GridCell, UnknownException}
import scalafx.application.{JFXApp3, Platform}
import scalafx.Includes.*
import scalafx.beans.property.{BooleanProperty, IntegerProperty, ObjectProperty, ReadOnlyObjectWrapper}
import scalafx.scene.{Group, Node, Scene}
import scalafx.scene.layout.{AnchorPane, Background, BackgroundFill, Border, BorderPane, BorderStroke, BorderStrokeStyle, BorderWidths, ColumnConstraints, CornerRadii, GridPane, HBox, Pane, Region, StackPane, TilePane, VBox}
import scalafx.scene.paint.Color.*
import scalafx.scene.control.{Alert, Button, ButtonType, Label, Menu, MenuBar, MenuItem, ScrollPane, TextArea}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.input.{KeyCode, KeyCombination, KeyEvent, MouseEvent}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.stage.{FileChooser, Stage}
import scalafx.scene.text.{Font, FontWeight, Text, TextFlow}
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.{FXCollections, ObservableList}
import scalafx.beans.binding.{Bindings, ObjectBinding}
import scalafx.beans.value.ObservableValue
import scalafx.scene.effect.{DropShadow, Effect}

// TODO: WEIRD BUT THE 'X' BUTTON IN ENDGAME DOESN'T CLOSE WHEN PRESSED (M) ???
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
  private val fileChooser                                        = new FileChooser
  fileChooser.getExtensionFilters.add(new FileChooser.ExtensionFilter("JSON Files Only", "*.json"))
  private val selectedPos                                        = ObjectProperty((-1, -1))
  private var valuesInTheSquare: Array[Array[IntegerProperty]]   = _
  private val activationTrackingProperty: Array[BooleanProperty] = (0 to 8).map(i => BooleanProperty(false)).toArray
  private var isMyGridFull                                       = BooleanProperty(false)
  private val WINDOW_WIDTH                                       = 960
  private val WINDOW_HEIGHT                                      = 720
  private val SQUARE_SIZE                                        = 50
  private val BOTTOM_ROW_PADDING                                 = 10
  private var boardSquareArray                                   = (0 to 8).toArray.map( x => Array.ofDim[StackPane](9) )
  private var gameHandler: GameHandler                           = _
  private var numberedButtons: Seq[Button]                       = _

  override def start(): Unit =
    // Using fixed-length window sizes because otherwise it becomes quite the mess
    stage = new JFXApp3.PrimaryStage:
      title     = "Killer Sudoku"
      width     = WINDOW_WIDTH
      height    = WINDOW_HEIGHT
      minWidth  = WINDOW_WIDTH
      maxWidth  = WINDOW_WIDTH
      minHeight = WINDOW_HEIGHT
      maxHeight = WINDOW_HEIGHT
      onCloseRequest = (event) =>
        event.consume()
        exitGame()

    val root = new BorderPane:
      top    = setUpMenuBar()
      bottom = new Pane()     // place-holder Pane, to be filled once a game file has been chosen
      right  = new Pane()     // place-holder Pane, to be filled once a game file has been chosen
      left   = new Pane()     // place-holder Pane, to be filled once a game file has been chosen
      center = new StackPane{
        children = List(new Text("To start a game, double click here or go 'Game > Open...' and select a file."){font = Font("Times New Roman", FontWeight.Normal, 18)})
        onMouseClicked =
          (event: MouseEvent) => if event.clickCount == 2 then loadGame()
      }
      id     = "abecedar"
    root.background = Background(Array(new BackgroundFill(("#FAF9F6"), CornerRadii.Empty, Insets.Empty)))

    stage.scene = new Scene(parent = root, WINDOW_WIDTH, WINDOW_HEIGHT)
    setUpControls()

  /** Sets up the window's MenuBar; gets called once, at the beginning. */
  private def setUpMenuBar(): MenuBar  =
    val menuBar = new MenuBar{
      menus = Seq(
        new Menu("Game"){
          style = "-fx-font-weight: bold;"
          items = Seq(
            new MenuItem("Open..."){
              style = "-fx-font-weight: normal;"
              onAction = (event) => loadGame()
              accelerator = KeyCombination("Ctrl + O")
            },
            new MenuItem("Save game"){
              style = "-fx-font-weight: normal;"
              onAction = (event) => saveGame()
              accelerator = KeyCombination("Ctrl + S")
            },
            new MenuItem("Save to..."){
              style = "-fx-font-weight: normal;"
              onAction = (event) => saveGame(false)
            },
            new MenuItem("Reset progress"){
              style = "-fx-font-weight: normal;"
              onAction = (event) => resetGame()
              accelerator = KeyCombination("Ctrl + R")
            },
            new MenuItem("Exit"){
              style = "-fx-font-weight: normal;"
              onAction = (event) => exitGame()
              accelerator = KeyCombination("Ctrl + X")
            }
          )
        }
      )
    }
    menuBar.background = Background(Array(new BackgroundFill((Gray), CornerRadii.Empty, Insets.Empty)))
    menuBar

  /** Sets up the visual representation of the sudoku grid. */
  private def setUpBoard()  : GridPane =
    // Cells bordering the edge of a 3x3 square need to be a bit thicker
    // Hence, a private method giving out the BorderWidth needed for all the cells
    def getBorderWidths(i: Int, j: Int): BorderWidths =
      new BorderWidths(
        if i % 3 == 0 then 3 else 2,
        if j % 3 == 2 then 3 else 2,
        if i % 3 == 2 then 3 else 2,
        if j % 3 == 0 then 3 else 2
      )

    // The Pane holding the visual representation of whole sudoku grid
    val boardSquare = new GridPane():
      alignment = Pos.Center

    // filling up this Pane
    for
      i <- (0 to 8)
      j <- (0 to 8)
    do
      // first, setting some values for the valuesInTheSquare IntegerProperty-s
      valuesInTheSquare(i)(j).value = gameHandler.numberAt(i, j)

      // the text displaying the value inside of the cell
      val insideText  = new Text(if valuesInTheSquare(i)(j).value == 0 then " " else valuesInTheSquare(i)(j).value.toString):
        font = Font("Niagara Solid", FontWeight.SemiBold, 30)

      // adding a listener for changes inside the valuesInTheSquare property
      // it handles all the actions happening when the user inserts/deletes a new value on the table
      valuesInTheSquare(i)(j).addListener(
        (_, oldValue, newValue) =>
          val old = oldValue.intValue()
          val upd = newValue.intValue()
          numberedButtons.zipWithIndex.filterNot( (_, i) => activationTrackingProperty(i).value ).map(_._1).foreach(
            button => button.style = "-fx-background-color: #b8c6db;"
          )
          gameHandler.insertValueAt(upd, i, j)
          isMyGridFull.value = gameHandler.isGridFull
          if old != 0 then activationTrackingProperty(old - 1).value = (0 to 8).toSet.flatMap( x => (0 to 8).map( y => (y, x) )).count(pos => gameHandler.numberAt(pos._1, pos._2) == old) == 9
          if upd != 0 then activationTrackingProperty(upd - 1).value = (0 to 8).toSet.flatMap( x => (0 to 8).map( y => (y, x) )).count(pos => gameHandler.numberAt(pos._1, pos._2) == upd) == 9
          insideText.text = if valuesInTheSquare(i)(j).value == 0 then " " else valuesInTheSquare(i)(j).value.toString
      )

      // the visual representation of the cell (i.e., a square with some background color)
      val smallSquareVisual = new Region:
        minWidth  = SQUARE_SIZE
        maxWidth  = SQUARE_SIZE
        minHeight = SQUARE_SIZE
        maxHeight = SQUARE_SIZE
      smallSquareVisual.border = new Border(new BorderStroke(Black, Black, Black, Black,
        BorderStrokeStyle.Solid, BorderStrokeStyle.Solid, BorderStrokeStyle.Solid, BorderStrokeStyle.Solid,
        CornerRadii.Empty, getBorderWidths(i, j), Insets.Empty))

      // the Pane that is going to hold the 2 things created above - the text, "stacked" over the cell representation
      val smallSquare = new StackPane:
        style          = if selectedPos.value == (i, j) then "-fx-background-color: green;" else "-fx-background-color: " + gameHandler.getGrid.getRegionsMap((i, j)) + ";"
        onMouseEntered = (_) => if gameHandler.numberAt(i, j) == 0 then gameHandler.possibleValuesAt((i, j)).foreach(
          i => if !activationTrackingProperty(i - 1).value then numberedButtons(i - 1).style = "-fx-background-color: #f0b846 ;"
        ) // coloring the buttons representing possible cell-values in yellow
        onMouseExited  = (_) => if gameHandler.numberAt(i, j) == 0 then gameHandler.possibleValuesAt((i, j)).foreach(
          i => numberedButtons(i - 1).style = "-fx-background-color: #b8c6db;"
        ) // reverting the buttons to their original color
        onMouseClicked = (_) => selectedPos.value = (i, j) // changing the selectedPos value

      // actually adding the text & the physical representation to the common-holder
      smallSquare.children ++= Seq(smallSquareVisual, insideText)
      // adding the holder to the sudoku-grid
      boardSquare.add(smallSquare, j, i)
      // setting the boardSquareArray to smallSquare
      boardSquareArray(i)(j) = smallSquare

    // all this other for-loop does is:
    // for each region, draw the number representing the region's sum
    // in the region's top-left-most corner
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

  /** Sets up the lower line-of-buttons. */
  private def setUpBottom() : GridPane =
    // setting up the grid that's going to contain the buttons
    val buttons = new GridPane:
      padding   = Insets(10)
      alignment = Pos.Center
      columnConstraints =  (0 to 10).map(_ => new ColumnConstraints(){percentWidth = 100.0 / 11})

    // first, we deal with the numbered buttons
    // we bind their able/disable attribute to their activation tracking property
    // and for hovering we color the cells on the grid containing the respective number
    // for click, we obviously color insert a number
    numberedButtons   = (1 to 9).toSeq.map( i => new Button(i.toString){
      padding  = Insets(BOTTOM_ROW_PADDING)
      alignmentInParent = Pos.Center
      style    = "-fx-background-color: #b8c6db;"
      focusTraversable = false
      effect = new DropShadow()
      disable <== activationTrackingProperty(i - 1)
      onMouseEntered = (_)  =>
        (0 to 8).flatMap( x => (0 to 8).map( y => (y, x) ))
          .filter( pos => gameHandler.numberAt(pos._1, pos._2) == i )
          .foreach( pos => boardSquareArray(pos._1)(pos._2).style = "-fx-background-color: #FAF9F6;")
      onMouseExited = (_)   =>
        (0 to 8).flatMap( x => (0 to 8).map( y => (y, x) ))
          .filter( pos => gameHandler.numberAt(pos._1, pos._2) == i )
          .foreach( pos => boardSquareArray(pos._1)(pos._2).style = "-fx-background-color:" + gameHandler.getGrid.getRegionsMap(pos) + ";")
        if selectedPos.value != (-1, -1) then boardSquareArray(selectedPos.value._1)(selectedPos.value._2).style = "-fx-background-color: green;"
      onMouseClicked = ((_) => if selectedPos.value != (-1, -1) then updateTable(i))})
    (0 to 8).foreach(i => buttons.add(numberedButtons(i), i, 0)) // adding the numbers to the line

    // button to delete cell values on the board
    buttons.add(new Button("Delete"){
      padding = Insets(BOTTOM_ROW_PADDING, 2, BOTTOM_ROW_PADDING, 2)
      focusTraversable = false
      style    = "-fx-background-color: #b8c6db;"
      alignmentInParent = Pos.Center
      effect = new DropShadow()
      onMouseClicked = (event) => if selectedPos.value != (-1, -1) then updateTable(0)}, 9, 0)
    // button to check whether the board was completed successfullly
    // activates only when the board is full
    buttons.add(new Button("Check") {
      padding = Insets(BOTTOM_ROW_PADDING, 2, BOTTOM_ROW_PADDING, 2)
      style    = "-fx-background-color: #b8c6db;"
      focusTraversable = false
      alignmentInParent = Pos.Center
      onMouseClicked = (event) => endGame()
      disable <== !isMyGridFull
      effect = new DropShadow()
    }, 10, 0)

    buttons

  /** Inserts a value inside an IntegerProperty. */
  // and, obv, triggers the object's onChange method, hence updating the table
  private def updateTable(i: Int) =
    if i == 0 || !activationTrackingProperty(i - 1).value then valuesInTheSquare(selectedPos.value._1)(selectedPos.value._2).value = i

  /** Sets up the square containing the possible sum-splits on the right of the grid. */
  private def setUpBubble(): StackPane =
    // the "background" of the bubble
    val bubbleVisual = new Region:
      minWidth = 400
      maxWidth = 400
      minHeight = 400
      maxHeight = 400
      effect = new DropShadow()
      background = Background(Array(new BackgroundFill(("#a2a4c1"), CornerRadii(5), Insets(0, 20, 0, 0))))
    bubbleVisual.border = new Border(new BorderStroke(Black, Black, Black, Black,
      BorderStrokeStyle.Solid, BorderStrokeStyle.Solid, BorderStrokeStyle.Solid, BorderStrokeStyle.Solid,
      CornerRadii(5), BorderWidths(2),
      Insets(0, 20, 0, 0)))

    // setting up the title
    val title = new Text("Possible sum-splits for the selected region: \n(note: only possible permutations shown)"):
      font = Font("Times New Roman", FontWeight.ExtraBold, 19)
      translateX = 10
      translateY = 25

    // the actual content, saved in a VBox, which is to be wrapped inside a ScrollPane to facilitate viewing.
    val contentVBox = new VBox:
      children = gameHandler.getBubble.map( x => new Text(x.mkString(" + ")){font = Font("Times New Roman", FontWeight.Normal, 18)} ).toSeq
    val textScrollPane = new ScrollPane:
      translateX = 12
      translateY = 53
      style      = "-fx-background: #a2a3c1"
      content    = contentVBox
      prefWidth  = 350
      prefHeight = 330

    // in case there's no selected cell, a default text, contained inside a VBox, appears, urging the user to select a cell
    val selectCellToSeeCombos = new VBox():
      alignment = Pos.Center
      translateX = 12
      translateY = 53
      style      = "-fx-background: #a2a3c1"
      prefWidth  = 350
      prefHeight = 330
      children = Seq(
        new Text("Select/press on a cell"){font = Font("Times New Roman", FontWeight.Normal, 18)},
        new Text("to see possible sum-splits."){font = Font("Times New Roman", FontWeight.Normal, 18)}
      )
    val switchablePane = new Pane: // the pane which acts as a switch, changing views between selecteCellToSeeCombos & textScrollPane
      children = if selectedPos.value == (-1, -1) then selectCellToSeeCombos else textScrollPane

    selectedPos.onChange(
      (_, oldValue, newValue) =>
        if oldValue != (-1, -1) then
          boardSquareArray(oldValue._1)(oldValue._2).style = "-fx-background-color: " + gameHandler.getGrid.getRegionsMap(oldValue) + ";" // changing the back the value of the formerly-selected cell
        if newValue != (-1, -1) then
          gameHandler.select(newValue) // selecting the new cell inside the gameHandler
          boardSquareArray(newValue._1)(newValue._2).style = "-fx-background-color: green;" // setting a new background for the newly selected cell
          switchablePane.children = textScrollPane // setting the sum-splits as the switchable pane content
        else
          gameHandler.deselect()      // deselecting any cell
          switchablePane.children = selectCellToSeeCombos // switching the content back to "please select a cell..."
        contentVBox.children = gameHandler.getBubble.toSeq.map( arr => arr.sorted ).map(
            x => new Text(x.mkString(" + ")){font = Font("Times New Roman", FontWeight.Normal, 18)} // re-setting the sum-split values
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
      children = Seq(new Group(bubbleVisual, title, switchablePane))

  /** Method setting up button controls for the game itself. Called once, at the start of the app. */
  private def setUpControls(): Unit =
    stage.scene.value.onKeyPressed =
      (event: KeyEvent) =>
        if selectedPos.value != (-1, -1) then
          event.code match
            case key if key.isDigitKey => updateTable(key.getName.last.asDigit)                                             // inserting digit values
            case KeyCode.Left          => selectedPos.value = (selectedPos.value._1, math.max(0, selectedPos.value._2 - 1)) // moving the selected cell to the left
            case KeyCode.Right         => selectedPos.value = (selectedPos.value._1, math.min(8, selectedPos.value._2 + 1)) // moving the selected cell to the right
            case KeyCode.Up            => selectedPos.value = (math.max(0, selectedPos.value._1 - 1), selectedPos.value._2) // moving the selected cell up
            case KeyCode.Down          => selectedPos.value = (math.min(8, selectedPos.value._1 + 1), selectedPos.value._2) // moving the selected cell down
            case KeyCode.BackSpace     => updateTable(0)                                                                    // removing any cell content, same as pressing 0
            case KeyCode.Escape        => selectedPos.value = (-1, -1)                                                      // deselecting
            case _                     => ()                                                                                // nothing

  /** Handy private message to create pop-up windows. */
  private def pushDialogue(stage: Stage, dialogueType: Alert.AlertType, alertTitle: String, header: String, description: String): Option[ButtonType] =
    new Alert(dialogueType) {
      initOwner(stage)
      title = alertTitle
      headerText = header
      contentText = description
    }.showAndWait()

  /** Saves the game, by default to the same place, but can also save it to different locations. */
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

  /** Loads the game. */
  private def loadGame(): Unit   =
    if gameHandler != null then
      val buttonSaveTo = new ButtonType("Save, then open")
      val buttonExit = new ButtonType("Open without saving")
      new Alert(Alert.AlertType.Confirmation) {
        initOwner(stage)
        title = "Confirm Exit"
        headerText = "Do you want to save your game before exiting?"
        buttonTypes = Seq(buttonSaveTo, buttonExit, ButtonType.Cancel)
      }.showAndWait() match
        case Some(`buttonSaveTo`) =>
          saveGame()
        case Some(`buttonExit`) =>
        case _ => return ()
    try
      val selectedFile = fileChooser.showOpenDialog(stage)
      if selectedFile != null then
        gameHandler = GameHandler.loadGame(selectedFile.toString)
        selectedPos.value = (-1, -1)
        valuesInTheSquare = ( (0 until 9).map( i => (0 until 9).map( j => IntegerProperty(gameHandler.numberAt(i, j)) ).toArray ).toArray )
        isMyGridFull.value = gameHandler.isGridFull
        for
          i <- 1 to 9
        do
          activationTrackingProperty(i - 1).value = (0 to 8).toSet.flatMap( x => (0 to 8).map( y => (y, x) )).count( pos => gameHandler.numberAt(pos._1, pos._2) == i ) == 9

        val myBorderPane = stage.scene.getValue.lookup("#abecedar").asInstanceOf[javafx.scene.layout.BorderPane]
        myBorderPane.bottom = setUpBottom()
        myBorderPane.center = setUpBoard()
        myBorderPane.right  = setUpBubble()
    catch
      case e: BadFilePathException   => pushDialogue(stage, Alert.AlertType.Error, "Error", "Bad File Path" , e.getMessage)
      case e: CorruptedFileException => pushDialogue(stage, Alert.AlertType.Error, "Error", "Corrupted File", e.description)
      case e: UnknownException       => pushDialogue(stage, Alert.AlertType.Error, "Error", "Unknown Exception", e.description)
      case e: Exception              => pushDialogue(stage, Alert.AlertType.Error, "Error", "Unknown Exception", e.getMessage)

  /** Resets all the game progress. */
  private def resetGame(): Unit  =
    try
      selectedPos.value = (-1, -1)
      gameHandler.resetGame()
      valuesInTheSquare.foreach(
        arr => arr.foreach(
          intProp =>
            intProp.value = 0
        )
      )
    catch
      case e: Exception => pushDialogue(stage, Alert.AlertType.Error, "Error", "Unkwonn Exception", e.getMessage)

  /** Exits the game. Requires a confirmation before exiting. */
  private def exitGame(): Unit   =
    if gameHandler == null then
      stage.close()
    else
      val buttonSaveTo = new ButtonType("Save, then exit")
      val buttonExit = new ButtonType("Exit without saving")
      new Alert(Alert.AlertType.Confirmation) {
        initOwner(stage)
        title = "Confirm Exit"
        headerText = "Do you want to save your game before exiting?"
        buttonTypes = Seq(buttonSaveTo, buttonExit, ButtonType.Cancel)
      }.showAndWait() match
        case Some(`buttonSaveTo`) =>
          saveGame(false)
          stage.close()
        case Some(`buttonExit`) =>
          stage.close()
        case _ =>

  /** Checks the game at the end. In case everything is good, congratulates the user and asks on the user's intended course of action. Otherwise, just tells the user they have a mistake. */
  private def endGame(): Unit    =
    if gameHandler.isGridCorrect then
      val exitTheApp = new ButtonType("Exit the app")
      val rsetGame  = new ButtonType("Reset the game")
      val closePopUp = new ButtonType("Close pop-up window")
      new Alert(Alert.AlertType.Confirmation) {
        initOwner(stage)
        title = "Congratulations!"
        headerText = "You've won the game."
        contentText = "How do you want to proceed?"
        buttonTypes = Seq(exitTheApp, rsetGame, closePopUp)
      }.showAndWait() match
        case Some(`exitTheApp`) =>  exitGame()
        case Some(`rsetGame`)  => resetGame()
        case Some(`closePopUp`) => ()
        case _            => ()
    else
      pushDialogue(stage, Alert.AlertType.Warning, "Incorrect table.", "Hmmm... it seems like you made a mistake somewhere.", "")
end sudokuApp
