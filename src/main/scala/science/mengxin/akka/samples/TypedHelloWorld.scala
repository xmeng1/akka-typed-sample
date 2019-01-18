package science.mengxin.akka.samples
import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{
  ActorRef,
  ActorSystem,
  Behavior,
  DispatcherSelector,
  Terminated
}

import scala.io.StdIn

/**
  *
  * <p>Date:    18/01/19
  *
  * @author mengxin
  * @version 1.0
  */
/*
 * This small piece of code defines two message types,
 *
 * one for commanding the Actor to greet someone and
 *
 * one that the Actor will use to confirm that it has done so.
 *
 * The Greet type contains not only the information of whom to greet, it also holds an ActorRef that the sender of the message supplies so that the HelloWorld Actor can send back the confirmation message.
 *
 * */
object HelloWorld {
  final case class Greet(whom: String, replyTo: ActorRef[Greeted])
  final case class Greeted(whom: String, from: ActorRef[Greet])

  /*
   * The behavior of the Actor is defined as the greeter value with the help of the receive behavior factory. Processing the next message then results in a new behavior that can potentially be different from this one. State is updated by returning a new behavior that holds the new immutable state. In this case we don’t need to update any state, so we return same, which means the next behavior is “the same as the current one”.
   *
   * */
  val greeter: Behavior[Greet] = Behaviors.receive { (context, message) ⇒
    /*
     * The type of the messages handled by this behavior is declared to be of class Greet, meaning that message argument is also typed as such. This is why we can access the whom and replyTo members without needing to use a pattern match.
     * */
    println(s"Greeter: Hello ${message.whom}!")
//    context.log.info("Greeter: receive message: {}", message)
    println(s"Greeter: receive message: $message")
    /*
     * On the last line we see the HelloWorld Actor send a message to another Actor, which is done using the ! operator (pronounced “bang” or “tell”). Since the replyTo address is declared to be of type ActorRef[Greeted], the compiler will only permit us to send messages of this type, other usage will not be accepted.
     * */
    message.replyTo ! Greeted(message.whom, context.self)
    println(s"Greeter:  send Greeted message to message.replyTo ${message.replyTo}")
//    context.log.info(
//      "Greeter: reply Greeted {} to message: {}",
//      Greeted(message.whom, context.self),
//      message.replyTo
//    )
    //  In this case we don’t need to update any state, so we return same, which means the next behavior is “the same as the current one”.
    Behaviors.same
  }
}

object HelloWorldBot {

  def bot(greetingCounter: Int, max: Int): Behavior[HelloWorld.Greeted] =
    Behaviors.receive { (context, message) ⇒
//      context.log.info("HelloWorldBot: receive message: {} and context {}", message, context)
      println(s"HelloWorldBot: receive message: $message and context $context", message, context)
      val n = greetingCounter + 1
      println(s"HelloWorldBot:  Greeting ${n} for ${message.whom}")
      if (n == max) {
        Behaviors.stopped
      } else {
        println(s"HelloWorldBot:  send Greet message to message.from ${message.from}")
        message.from ! HelloWorld.Greet(message.whom, context.self)
//        context.log.info("HelloWorldBot: send Greet {} to from {}",
//          HelloWorld.Greet(message.whom, context.self),
//          message.from
//        )
        bot(n, max)
      }
    }
}

object HelloWorldMain {

  final case class Start(name: String)

  val main: Behavior[Start] =
    Behaviors.setup { context ⇒
      val greeter = context.spawn(HelloWorld.greeter, "greeter")
//      context.log.info("HelloWorldMain: create greeter actor {}", greeter)
      println(s"HelloWorldMain: create greeter actor $greeter")
      Behaviors.receiveMessage { message ⇒
//        context.log.info("HelloWorldMain: receive message: {}", message)
        println(s"HelloWorldMain: receive message: $message")
        val replyTo = context.spawn(
          HelloWorldBot.bot(greetingCounter = 0, max = 3),
          message.name
        )
        println(s"HelloWorldMain: create replyTo actor: $replyTo")
//        context.log.info("HelloWorldMain: create replyTo actor: {}", replyTo)
        println(s"HelloWorldMain: send Greet to greeter")
        greeter ! HelloWorld.Greet(message.name, replyTo)
        Behaviors.same
      }
    }
}

object TypedHelloWorld extends App {
  val system: ActorSystem[HelloWorldMain.Start] =
    ActorSystem(HelloWorldMain.main, "hello")
  println("send to World to the HelloWorldMain")
  system ! HelloWorldMain.Start("World")
//  system ! HelloWorldMain.Start("Akka")

  println(">>> Press ENTER to exit <<<")
  try StdIn.readLine()
  finally system.terminate()
}
