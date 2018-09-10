import java.util.concurrent.Executors

import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Application extends App  {
  var hook: Option[ActorSystem] = None
  var portBindingHook: Option[Done] = None



      implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
      implicit val actorSystem = ActorSystem("Accounts-System")
      implicit val mat = ActorMaterializer()
      AccountsServiceApp.start().onComplete {
        case Success(o) => {
          portBindingHook = Some(o)
          hook = Some(actorSystem)
        }
        case Failure(e) => {
          e.printStackTrace()
          //loggerF.error("Service could not be started")
        }
      }


}
