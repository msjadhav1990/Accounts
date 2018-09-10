package com.accounts.routes

import java.util.concurrent.Executors

import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.ActorMaterializer
import akka.testkit.TestKit
import akka.util.Timeout
import com.accounts.domain.{CreateLinkedAccount, GenericHttpResponse, GetAccountDetailsRequest}
import com.typesafe.config.ConfigFactory
import org.scalatest._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try

class AccountsRoutesTests extends AsyncFunSpec with Matchers with DiagrammedAssertions
with BeforeAndAfter with BeforeAndAfterAll  with ScalatestRouteTest {

  val conf = ConfigFactory.load()
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
  implicit val actorSystem = ActorSystem("SourceRefRoutesTests", conf)
  implicit val mat = ActorMaterializer()
  implicit val timeout = Timeout(5000 millis)


  val rootActor = system.actorOf(Props(new Actor() {
    override def receive: Receive = {
      case d: CreateLinkedAccount => sender ! GenericHttpResponse(StatusCodes.OK, "")
      case d: GetAccountDetailsRequest => sender ! GenericHttpResponse(StatusCodes.OK, "")
      case _ => sender ! GenericHttpResponse(StatusCodes.InternalServerError, "")
    }
  }))

  val rootActorError =system.actorOf(Props(new Actor() {
    override def receive: Receive = {
      case _ => sender ! GenericHttpResponse(StatusCodes.InternalServerError, "")
    }
  }))

  val rootActorException =system.actorOf(Props(new Actor() {
    override def receive: Receive = {
      case _ => sender ! Try(throw new RuntimeException("Generic Error"))
    }
  }))

  val allRoutes = new AccountsRoutes(rootActor)



  describe("Get Customer Details Route") {
    it("Should return 200 OK") {

      Get("/app/accounts/1") ~> AccountsOperationRoute.route(rootActor) ~> check {
        response.status.intValue() shouldEqual StatusCodes.OK.intValue
      }

    }

    it("Should return 500 Internal server error") {

      Get("/app/accounts/1") ~> AccountsOperationRoute.route(rootActorError)  ~> check {
        response.status.intValue() shouldEqual StatusCodes.InternalServerError.intValue
      }

    }

    it("Should return 500 Internal server error for exception") {

      Get("/app/accounts/1") ~> AccountsOperationRoute.route(rootActorException)  ~> check {
        response.status.intValue() shouldEqual StatusCodes.InternalServerError.intValue
      }

    }
  }
  describe("CreateLinkedAccount Route") {
    it("Should return 200 OK") {

      Post("/app/accounts").withEntity(ContentTypes.`application/json`,"{\n  \"custId\":1,\n  \"accountType\":\"Current\",\n  \"initialBalance\":100\n}") ~> AccountsOperationRoute.route(rootActor) ~> check {
        response.status.intValue() shouldEqual StatusCodes.OK.intValue
      }

    }

    it("Should return 500 Internal server error") {

      Post("/app/accounts").withEntity(ContentTypes.`application/json`,"{\n  \"custId\":1,\n  \"accountType\":\"Current\",\n  \"initialBalance\":100\n}") ~> AccountsOperationRoute.route(rootActorError)  ~> check {
        response.status.intValue() shouldEqual StatusCodes.InternalServerError.intValue
      }

    }

    it("Should return 500 Internal server error for exception") {

      Post("/app/accounts").withEntity(ContentTypes.`application/json`,"{\n  \"custId\":1,\n  \"accountType\":\"Current\",\n  \"initialBalance\":100\n}") ~> AccountsOperationRoute.route(rootActorException)  ~> check {
        response.status.intValue() shouldEqual StatusCodes.InternalServerError.intValue
      }

    }
  }

  describe("All Routes") {

    it("Should return 200 OK | GetAccountDetails") {

      Get("/app/accounts/1") ~> allRoutes.apiRoutes ~> check {
        response.status.intValue() shouldEqual StatusCodes.OK.intValue
      }

    }

    it("Should return 200 OK | CreateLinkedAccount") {

      Post("/app/accounts").withEntity(ContentTypes.`application/json`,"{\n  \"custId\":1,\n  \"accountType\":\"Current\",\n  \"initialBalance\":100\n}") ~> allRoutes.apiRoutes ~> check {
        response.status.intValue() shouldEqual StatusCodes.OK.intValue
      }

    }

  }

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(actorSystem)
}
