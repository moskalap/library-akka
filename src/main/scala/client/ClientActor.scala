package client

import akka.actor.{Actor, ActorLogging, ActorSelection}
import com.typesafe.config.Config
import model._

class ClientActor(config: Config) extends Actor with ActorLogging {
  val priceActor: ActorSelection = context.actorSelection(config.getString("pricePath"))
  val orderActor: ActorSelection = context.actorSelection(config.getString("orderPath"))
  val streamActor: ActorSelection = context.actorSelection(config.getString("streamPath"))

  override def receive: Receive = {
    case msg: String =>
      if (msg startsWith "price") priceActor ! PriceRequest(msg.split("price ")(1))
      if (msg startsWith "order") orderActor ! Order(msg.split("order ")(1))
      if (msg startsWith "stream") streamActor ! StreamRequest(msg.split("stream ")(1))

    case Book(title, price) => println(s"Book: $title price: $price".toString)

    case BookContent(content) => println(content)

    case BookNotFound() => println("Book not found")

    case OrderStatus(confirmed) => if (confirmed) println("Order confirmed")
  }

}
