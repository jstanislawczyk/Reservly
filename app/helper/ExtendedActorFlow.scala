package helper

import akka.actor._
import akka.stream.scaladsl.{Keep, Sink, Source, Flow}
import akka.stream.{Materializer, OverflowStrategy}

object ExtendedActorFlow {
  def actorRef[In, Out]
  (props: ActorRef => Props, bufferSize: Int = 16, overflowStrategy: OverflowStrategy = OverflowStrategy.dropNew, maybeName: Option[String] = None)
  (implicit factory: ActorRefFactory, mat: Materializer): Flow[In, Out, _] = {

    val (outActor, publisher) = Source.actorRef[Out](bufferSize, overflowStrategy)
      .toMat(Sink.asPublisher(false))(Keep.both).run()

    def flowActorProps: Props = {
      Props(new Actor {
        val flowActor: ActorRef = context.watch(context.actorOf(props(outActor), "flowActor"))

        def receive: PartialFunction[Any, Unit] = {
          case Status.Success(_) | Status.Failure(_) => flowActor ! PoisonPill
          case Terminated(_) => context.stop(self)
          case other => flowActor ! other
        }

        override def supervisorStrategy: OneForOneStrategy = OneForOneStrategy() { case _ => SupervisorStrategy.Stop }
      })
    }

    def actorRefForSink =
      maybeName.fold(factory.actorOf(flowActorProps)) { name => factory.actorOf(flowActorProps, name) }

    Flow.fromSinkAndSource(Sink.actorRef(actorRefForSink, Status.Success(())), Source.fromPublisher(publisher))
  }
}
