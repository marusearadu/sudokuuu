package game.gui
import scalafx.application.{JFXApp, JFXApp3}
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{Button, Label, Menu, MenuBar}
import scalafx.scene.layout.{Background, BackgroundFill, BorderPane, CornerRadii, GridPane, HBox, VBox}
import scalafx.scene.paint.Color.*

object playingWithScalaFX extends JFXApp3:
  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage:
      title = "Hello Stage"
      scene = new Scene(800, 540){
        val border = new BorderPane
        border.top    = new MenuBar{
          menus = Seq(new Menu("Menu"))
        }
        border.center = new Label("On center")
        border.bottom = new Label("On bottom")
        border.left   = new Label("On left")
        border.right  = new Label("On right")

        root = border
        minWidth = 600
        maxWidth = 900
        minHeight = 400
        maxHeight = 600
      }

//    sideBox.background = Background(Array(new BackgroundFill((Gray), CornerRadii.Empty, Insets.Empty)))
//    bottomBox.background = Background(Array(new BackgroundFill((Blue), CornerRadii.Empty, Insets.Empty)))
//    topBox.background = Background(Array(new BackgroundFill((Red), CornerRadii.Empty, Insets.Empty)))
end playingWithScalaFX
