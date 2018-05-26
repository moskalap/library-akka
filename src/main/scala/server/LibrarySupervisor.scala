package server

import java.nio.file.Path

import akka.actor.{Actor, Props}
import akka.event.Logging

import scala.io.Source
import java.nio.file.{Path, Paths}

class LibrarySupervisor(dbsPaths: List[String], path: String) extends Actor{
  val log = Logging(context.system, this)

  override def preStart():Unit ={
    log.info("Library system started")
    val paths = dbsPaths
        .map(getClass.getResource(_).toURI)
        .map(Paths.get)

    context.actorOf(Props(new PriceInformer(paths)))
  }

  override def receive: Receive = {
    case x => print(s"unrecognized: ${x.toString}")
  }
}
