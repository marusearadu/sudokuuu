import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxAssert
import org.testfx.matcher.control.LabeledMatchers
import javafx.scene.control.Button

import scala.jdk.CollectionConverters.*
import scala.jdk.OptionConverters.*
import javafx.scene.Node
import scalafx.scene.Scene

//Test class definition. Use JUnit 5 extension mechanic.
@ExtendWith(Array(classOf[ApplicationExtension]))
class gameGuiTest:
//  var gui: Option[Scene] = None
//
//  @Start
//  def start(stage: Stage): Unit =
//
//    // Create our gui and set it as the Scene of the test Stage
//    val newGui = Scene()
//    stage.setScene(newGui)
//
//    gui = Some(newGui)
//    stage.show()
//
//  @Test
//  def testButton2(robot: FxRobot): Unit =
//
//    // With CSS class.
//    FxAssert.verifyThat(".button", LabeledMatchers.hasText("Click me!"))
//    robot.clickOn(".button")
//    FxAssert.verifyThat(".button", LabeledMatchers.hasText("Clicked 1 times"))
//
//    // With CSS id.
//    FxAssert.verifyThat(
//      "#mainButton",
//      LabeledMatchers.hasText("Clicked 1 times")
//    )
//    robot.clickOn("#mainButton")
//    FxAssert.verifyThat(
//      "#mainButton",
//      LabeledMatchers.hasText("Clicked 2 times")
//    )
//
//    // Getting reference without FxAssert. Use javafx button.
//    val button: Button = robot.lookup(".button").queryAs(classOf[Button])
//
//    /*
//      Find all nodes with css class .button .
//      Resulting Java Set is converted to a Scala Set
//      by importing scala.jdk.CollectionConverters._
//      and using method asScala
//    */
//    val buttons: scala.collection.mutable.Set[Button] =
//      robot.lookup(".button").queryAllAs(classOf[Button]).asScala
//
//    // A predicate can also be used. scala.jdk.OptionConverters._ is imported.
//    val noParent: Option[Node] = robot
//      .lookup[Node]((n: Node) => n.getParent == null)
//      .tryQueryAs(classOf[Node])
//      .toScala
    
end gameGuiTest
