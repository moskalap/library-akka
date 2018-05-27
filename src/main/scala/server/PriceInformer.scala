package server

import java.nio.file.Path

import akka.NotUsed

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.javadsl.{Flow, Framing}
import akka.stream.scaladsl.{FileIO, Sink, Source}
import akka.util.ByteString
import model.{Book, BookNotFound, PriceRequest}

import scala.concurrent.Future


class PriceInformer(dbs: List[Path]) extends Actor with ActorLogging{
  implicit val materializer: ActorMaterializer = ActorMaterializer.create(context)
  override def receive: Receive = {
    case PriceRequest(title) => handlePriceReq(title, sender())
    case _ => log.info("unrecognized msg")
  }

  def handlePriceReq(title: String, to: ActorRef): Unit = {

    val sources = dbs.map( open(_) recover replacingErrorByEmptyString via framing.async
      map {_.utf8String}
      filter(_.contains(title)))

    (sources reduce (_ merge _))
     .take(1)
     .map(s => {Book(s.split("<#>")(1).trim, BigDecimal(s.split("<#>")(2).trim))})
     .runWith(Sink.seq[Book])
     .onComplete({
       case Success(book: Seq[Book]) =>
         log.info(s"found: ${(book head).title}")
         to ! (book head)

       case Failure(_) =>
         log.info("error")
         to ! BookNotFound()

     })

  }

  val framing: Flow[ByteString, ByteString, NotUsed] = Framing.delimiter(ByteString("\n"), Int.MaxValue)
  val replacingErrorByEmptyString: PartialFunction[Throwable,ByteString] = {case _: Exception => ByteString("")}
  val open: Path => Source[ByteString, Future[IOResult]] = FileIO.fromPath(_)
}
