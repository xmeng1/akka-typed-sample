package science.mengxin.akka.samples
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors

import scala.io.StdIn
/**
 * 
 * <p>Date:    18/01/19
 *
 * @author mengxin
 * @version 1.0
 */

object PrintMyActorRefActor {
  def apply(): Behavior[String] =
    Behaviors.setup(context ⇒ new PrintMyActorRefActor(context))
}

class PrintMyActorRefActor(context: ActorContext[String]) extends AbstractBehavior[String] {

  override def onMessage(msg: String): Behavior[String] =
    msg match {
      case "printit" ⇒
        val secondRef = context.spawn(Behaviors.empty[String], "second-actor")
        println(s"Second: $secondRef")
        this
    }
}

object Main {
  def apply(): Behavior[String] =
    Behaviors.setup(context ⇒ new Main(context))

}

class Main(context: ActorContext[String]) extends AbstractBehavior[String] {
  override def onMessage(msg: String): Behavior[String] =
    msg match {
      case "start" ⇒
        val firstRef = context.spawn(PrintMyActorRefActor(), "first-actor")
        println(s"First: $firstRef")
        firstRef ! "printit"
        this
    }
}

object ActorHierarchyExperiments extends App {
  println(s"startup")
  val system = ActorSystem(Main(), "testSystem")
  system ! "start"
//    [info] First: Actor[akka://testSystem/user/first-actor#-337447116]
//    [info] Second: Actor[akka://testSystem/user/first-actor/second-actor#-1314725328]


  println(">>> Press ENTER to exit <<<")
  try StdIn.readLine()
  finally system.terminate()
}