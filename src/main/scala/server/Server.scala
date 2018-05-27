package server

import collection.JavaConverters._
import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory


object Server extends App{
  val config = ConfigFactory.load("server.conf")
  val librarySystem = ActorSystem.create("bookstore_system", config)
  val supervisor = librarySystem.actorOf(Props(new BookstoreSupervisor(
    asScalaBuffer(config.getStringList("dbs")).toList,
    config.getString("orders"))), "bookstore_supervisor")
  var run = true
  while (run){
    val line = scala.io.StdIn.readLine().trim
    if (line.startsWith("/q")){
      run = false
    }else{
      supervisor ! line
    }
  }
}
