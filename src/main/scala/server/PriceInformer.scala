package server

import java.io.File
import java.nio.file.{Path, Paths}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.{Failure, Success}
import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.stream.ActorMaterializer
import akka.stream.javadsl.Framing
import akka.stream.scaladsl.{FileIO, Sink}
import akka.util.ByteString
import model.{BookNotFound, Price, PriceRequest}


class PriceInformer(dbs: List[Path]) extends Actor with ActorLogging{

  implicit val materializer = ActorMaterializer.create(context)
  override def receive: Receive = {
    case PriceRequest(title) => handlePriceReq(title, sender())
  }

  def handlePriceReq(title: String, to: ActorRef): Unit = {
    val framing = Framing.delimiter(ByteString("\n"), Int.MaxValue)

    dbs
      .map(FileIO.fromPath(_) via framing)
      .reduce(_ merge _)
      .map(_.utf8String)
      .filter(_.contains(title))
      .take(1)
      .map(_.split("<#>")(2))
      .map(BigDecimal(_))
      .runWith(Sink.seq[BigDecimal])
      .onComplete({
        case Success(price: Seq[BigDecimal]) => {
          log.info(s"found: ${price head}")
          to ! Price(price head)
        }
        case Failure(t: Throwable) => {
          log.info("error")
          to ! BookNotFound()
        }
      })

  }
}
