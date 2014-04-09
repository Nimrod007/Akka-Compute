
import akka.actor._
import language.postfixOps
import AkkaMessages._
import ExampleCalc._
import scala.util.Random


object AkkaComputeApp extends App {
  val exampleSystem = ActorSystem("exampleSystem")

  val computeManager = exampleSystem.actorOf(Props[ComputeManager], name = "computing-manager")

  computeManager ! Compute(inputSet)
}


class ComputeManager extends Actor with ActorLogging{

  //will hold all the results got from my workers
  var results = collection.mutable.Set[Any]()

  //hold a list of all my workers
  var computingWorker  = collection.mutable.Set[ActorRef]()

  override def receive: Receive = {
    //we got something to compute, create a worker for each input
    case Compute(inputSet) => {
      inputSet.map{ input =>
        log.info(s"Creating new worker for $input")
        val compUnit = context.actorOf(Props(classOf[CompUnit],someCalculationFunc))
        //watch this child so we know when we finished all calculations
        context.watch(compUnit)
        //add this child to my workers list
        computingWorker add compUnit
        //let it compute the input
        compUnit ! SingleCompute(input)
      }
    }
    case Result(result) => {
      log.info(s"Got result $result")
      results add result
    }
    case Terminated(someOne) => {
      log.info(s"Got termination from  $someOne")
      computingWorker remove someOne
      if (computingWorker.isEmpty) {
        log.info(s"finished working, result : $results")
        context.stop(self)
      }
    }
  }

}


//this is a compute worker in inits with a function to preform on every input it gets later on as messages
class CompUnit(compFunc: (Any => Any) ) extends Actor with ActorLogging{

  override def receive: Receive = {
    case SingleCompute(input) => {
      log.info(s"Start to compute $input")
      //send back the result of this calculation to the sender
      sender ! Result(compFunc(input))
      log.info(s"Finished to compute $input")
      context.stop(self)
    }
  }
}

object AkkaMessages {

  sealed trait Message
  case class Compute(inputSet:Iterable[Any]) extends Message
  case class SingleCompute(input:Any) extends Message
  case class Result(result:Any) extends Message

}

object ExampleCalc {
  //functions adds 10 to the given input
  val someCalculationFunc : (Int) => Int =  _ + 10
  val inputSet = Range(1,5)
}
