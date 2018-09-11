
package exasol

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.HttpExt
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

    def announceGraph(http: HttpExt, socketUrl: String) =
        Source.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
            val exasolAnnounce = http
                .webSocketClientFlow(WebSocketRequest(socketUrl))
                    .named("ExasolAnnounceLogin")
                    .log("SendAnnounce")

            val announceResponse = builder.add(Flow[Message].map(data =>
                LoginCommand.Response.extract(data)
            ).named("Building Response").log("AnnouncementResponse"))
            val loginMessage = TextMessage((new LoginCommand).toJsonString())

            import GraphDSL.Implicits._

            Source.single(loginMessage) ~> exasolAnnounce ~> announceResponse

            SourceShape(announceResponse.out)
        })

    def graph(http: HttpExt, future: Future[LoginCommand.UserData], socketUrl: String) =
        Source.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>

            val in = Source
                .fromFuture(future)
                .named("LoginData")

            val fanIn = builder.add(Zip[LoginCommand.Response, LoginCommand.UserData].named("LoginDataCollector"))
            val exasolLogin = http
                .webSocketClientFlow(WebSocketRequest(socketUrl))
                    .named("ExasolLogin")
                    .log("UserLogin")

            val encryptLoginData = Flow[(LoginCommand.Response, LoginCommand.UserData)].map(data =>
                data._2
            ).named("Encrypting login data")
            val loginDataMessage = Flow[LoginCommand.UserData].map(data =>
                TextMessage("bar")
            ).named("Converting UserData to Message")

            val session = builder.add(Flow[Message].map(data =>
                LoginCommand.SessionData.extract(data)
            ).named("ExasolSession"))

            import GraphDSL.Implicits._

            in ~> fanIn.in1
            announceGraph(http, socketUrl) ~> fanIn.in0
            fanIn.out ~> encryptLoginData ~> loginDataMessage ~> exasolLogin ~> session

            SourceShape(session.out)
        })
}
