package server

import java.nio.file.Paths

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, Flow, Sink, Source}
import akka.util.ByteString
import model.{BookContent, BookNotFound, StreamRequest}

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit

import akka.Done
import akka.stream.javadsl.Framing

import scala.util.Random
class BookStreamer extends Actor with ActorLogging{
  val loremIpsum = "/database/lorem.txt"
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  override def receive: Receive = {
    case StreamRequest(title) => handleStream(title, sender())
    case _ => log.info("unrecognized msg")
  }


  def handleStream(title: String, sender: ActorRef): Unit = {
    val contentPath = getClass.getResource(s"/database/$title.txt")
    if (contentPath != null)
      streamTo(sender, FileIO.fromPath(Paths.get(contentPath.toURI)))
    else
      if(new Random().nextBoolean())
        streamTo(sender, FileIO.fromPath(Paths.get(getClass.getResource(loremIpsum).toURI)))
    else sender ! BookNotFound()
  }

  def streamTo( sender: ActorRef, source: Source[ByteString, Future[IOResult]]): Unit = {
    Flow[ByteString].via(Framing.delimiter(ByteString("."), Int.MaxValue))
      .map(_.utf8String)
      .map(_.replaceAll("\\s", " "))
      .filter(_.nonEmpty)
      .map(_++".")
      .map(BookContent)
      .throttle(1, FiniteDuration(1, TimeUnit.SECONDS))
      .runWith(source, Sink.actorRef(sender, Done))

  }

}