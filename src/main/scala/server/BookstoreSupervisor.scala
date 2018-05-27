package server

import java.io.FileWriter

import akka.actor.{Actor, OneForOneStrategy, Props, SupervisorStrategy}
import akka.event.Logging

import scala.concurrent.duration._
import java.nio.file.Paths

import akka.actor.SupervisorStrategy.Restart

class BookstoreSupervisor(dbsPaths: List[String], path: String) extends Actor{
  val log = Logging(context.system, this)

  override def preStart():Unit ={
    log.info("Library system started")
    val paths = dbsPaths
        .map(getClass.getResource(_).toURI)
        .map(Paths.get)

    val orderPath = getClass.getResource(path).getPath
    context.actorOf(Props(new PriceInformer(paths)), "price")
    context.actorOf(Props(new OrderTaker(new FileWriter(orderPath))), "order")
    context.actorOf(Props(new BookStreamer()), "stream")
  }

  override def receive: Receive = {
    case x => print(s"unrecognized: ${x.toString}")
  }

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(10, 1 minute) {
      case _: Exception => Restart
    }
}
