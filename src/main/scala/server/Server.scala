package server

import java.io.File

import collection.JavaConverters._
import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.collection.mutable.ListBuffer

object Server extends App{
  val config = ConfigFactory.load("server.conf")
  val librarySystem = ActorSystem.create("library_system", config)
  val supervisor = librarySystem.actorOf(Props(new LibrarySupervisor(
    asScalaBuffer(config.getStringList("dbs")).toList,
    config.getString("orders"))), "library_supervisor")
  var run = true
  while (true){
    val line = scala.io.StdIn.readLine().trim
    if (line.startsWith("/q")){

    }else{
      supervisor ! line
    }
  }
}
