package server

import java.io.FileWriter

import akka.actor.{Actor, ActorLogging, ActorRef}
import model.{Order, OrderStatus}

class OrderTaker(writer: FileWriter) extends Actor with ActorLogging{

  override def receive: Receive = {
    case msg: String => println(msg)
    case Order(title) => handleOrder(title, sender())
  }


  def handleOrder(title: String, ref: ActorRef): Unit = synchronized {
    try{
      writer.write(title + "\n")
      writer.flush()
      ref ! OrderStatus(true)
    }catch{
      case _: Exception => ref ! OrderStatus(false)
    }

  }



}
