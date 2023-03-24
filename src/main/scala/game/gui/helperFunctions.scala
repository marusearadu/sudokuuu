package game.gui

import game.{BadFilePathException, CorruptedFileException, GameHandler, UnknownException}
import scalafx.scene.control.{Alert, ButtonType}
import scalafx.stage.{FileChooser, Stage}
import scalafx.stage.FileChooser.ExtensionFilter

object helperFunctions:
  private val fileChooser = new FileChooser
  fileChooser.getExtensionFilters.add(new ExtensionFilter("JSON Files Only", "*.json"))

  private def pushDialogue(stage: Stage, dialogueType: Alert.AlertType, alertTitle: String, header: String, description: String): Option[ButtonType] =
    new Alert(dialogueType) {
      initOwner(stage)
      title = alertTitle
      headerText = header
      contentText = description
    }.showAndWait()

  def saveGame(samePlace: Boolean = true) =
    if sudokuApp.getGameHandler != null then
      try
        if GameHandler.getAddress != null && samePlace then
          GameHandler.saveGame(sudokuApp.getGameHandler)
        else
          val selectedLocation = fileChooser.showSaveDialog(sudokuApp.stage)
          if selectedLocation != null then
            GameHandler.saveGame(sudokuApp.getGameHandler, selectedLocation.toString)
      catch
        case e: UnknownException => pushDialogue(sudokuApp.stage, Alert.AlertType.Error, "Error", "Unknown exception", e.description)
        case e: Exception        => pushDialogue(sudokuApp.stage, Alert.AlertType.Error, "Error", "Unknown exception", e.getMessage)
    else
      pushDialogue(sudokuApp.stage, Alert.AlertType.Warning, "Warning!", "Can't save an empty game.",
        "Please first open a game in order to save it.")

  def loadGame() =
    try
      val selectedFile = fileChooser.showOpenDialog(sudokuApp.stage)
      if selectedFile != null then sudokuApp.setGameHandler(GameHandler.loadGame(selectedFile.toString))
      // TODO: switch view
    catch
      case e: BadFilePathException   => pushDialogue(sudokuApp.stage, Alert.AlertType.Error, "Error", "Bad File Path" , e.getMessage)
      case e: CorruptedFileException => pushDialogue(sudokuApp.stage, Alert.AlertType.Error, "Error", "Corrupted File", e.description)
      case e: UnknownException       => pushDialogue(sudokuApp.stage, Alert.AlertType.Error, "Error", "Unknown Exception", e.description)
      case e: Exception              => pushDialogue(sudokuApp.stage, Alert.AlertType.Error, "Error", "Unkwonn Exception", e.getMessage)

  def resetGame() =
    try 
      sudokuApp.getGameHandler.resetGame()
    catch
      case e: NullPointerException => pushDialogue(sudokuApp.stage, Alert.AlertType.Warning, "Warning!", "Can't reset an empty game.",
            "Please first open a game in order to reset it.")
      case e: Exception              => pushDialogue(sudokuApp.stage, Alert.AlertType.Error, "Error", "Unkwonn Exception", e.getMessage)

  def exitGame() =
    if sudokuApp.getGameHandler == null then
      sudokuApp.stage.close()
    else
      val buttonSaveTo = new ButtonType("Save, then exit")
      val buttonExit   = new ButtonType("Exit without saving")
      new Alert(Alert.AlertType.Confirmation){
        initOwner(sudokuApp.stage)
        title = "Confirm Exit"
        headerText = "Do you want to save your game before exiting?"
        buttonTypes = Seq(buttonSaveTo, buttonExit, ButtonType.Cancel)
      }.showAndWait() match
        case Some(`buttonSaveTo`) =>
          saveGame(false)
          sudokuApp.stage.close()
        case Some(`buttonExit`)   =>
          sudokuApp.stage.close()
        case _                  =>
          ()

end helperFunctions

