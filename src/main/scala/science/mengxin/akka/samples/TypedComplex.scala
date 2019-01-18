package science.mengxin.akka.samples
import akka.actor.typed.ActorRef
//#fiddle_code
//#imports
import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior, DispatcherSelector, Terminated }
//#imports
//#fiddle_code

//import org.scalatest.WordSpecLike
//import akka.actor.testkit.typed.scaladsl.ActorTestKit
//import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
/**
 * 
 * <p>Date:    18/01/19
 *
 * @author mengxin
 * @version 1.0
 */
object TypedComplex {}
