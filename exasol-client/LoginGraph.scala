
package exasol

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws._
import akka.stream.FlowShape
import akka.stream.Inlet
import akka.stream.Outlet
import akka.stream.SourceShape
import akka.stream.SinkShape
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.GraphDSL
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.stream.scaladsl.Zip
import scala.concurrent.Future

object Login {

    def graph(system: ActorSystem, future: Future[LoginCommand.UserData], socketUrl: String) =
        Source.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>

            val in = Source
                .fromFuture(future)
                .named("LoginData")

            val fanIn = builder.add(Zip[LoginCommand.Response, LoginCommand.UserData].named("LoginDataCollector"))
            val exasolLogin = Http(system).webSocketClientFlow(WebSocketRequest(socketUrl)).named("ExasolLogin")
            val encryptLoginData = Flow[(LoginCommand.Response, LoginCommand.UserData)].map(data =>
                data._2
            ).log("Encrypting login data")
            val loginDataMessage = Flow[LoginCommand.UserData].map(data =>
                TextMessage("bar")
            ).log("Converting UserData to Message")

            val exasolAnnounce = Http(system).webSocketClientFlow(WebSocketRequest(socketUrl)).named("ExasolAnnounceLogin")
            val announceResponse = Flow[Message].map(data =>
                LoginCommand.Response.extract(data)
            ).log("Building Response")

            val loginMessage = Flow[LoginCommand].map(data => {
                system.log.info(data.toJsonString())
                TextMessage(data.toJsonString())
            }
            ).log("Converting Login to Message")
            val session = builder.add(Flow[Message].map(data =>
                LoginCommand.SessionData(0, 1, "2", "db", "w", 59, 546, 45, "q", "TZ", "TZB")
            ).named("ExasolSession"))

            import GraphDSL.Implicits._

            in ~> fanIn.in1
            Source.single(new LoginCommand) ~> loginMessage ~> exasolAnnounce ~> announceResponse ~> fanIn.in0
            fanIn.out ~> encryptLoginData ~> loginDataMessage ~> exasolLogin ~> session

            SourceShape(session.out)
    })
}
