package client

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory


object Client extends App {
  val config = ConfigFactory.load("client.conf")
  val system = ActorSystem.create("client", config)
  val dispatcher = system.actorOf(Props(new ClientActor(config)))
  var run = true
  while (run){
    val line = scala.io.StdIn.readLine().trim
    if (line.startsWith("/q")){
      run = false
    }else{
      dispatcher ! line
    }
  }
}
