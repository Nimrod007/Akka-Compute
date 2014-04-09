import akka.actor.ActorSystem
import language.postfixOps


object AkkaMessages {
  sealed trait Message
  case object Start extends Message

}

object AkkaComputeApp extends App {
  val exampleSystem = ActorSystem("exampleSystem")


}
