package com.accounts.actors

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.http.javadsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes
import akka.pattern.ask
import akka.util.Timeout
import com.accounts.config.ServiceConfiguration
import com.accounts.context.ResourceContext
import com.accounts.domain._
import com.accounts.handlers.AccountsHandler
import com.accounts.storage.CustomerInfoDatabase
import com.typesafe.config.ConfigFactory
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

class AccountsActorTests extends AsyncFunSpec with Matchers with DiagrammedAssertions
  with BeforeAndAfter with BeforeAndAfterAll with MockitoSugar  with ScalaFutures{
  val config = ConfigFactory.load("test")
  val testConfig = ServiceConfiguration(config)
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
  val actorSystem = ActorSystem("CreateStreamActorTests", config)
  implicit  val resourceContext= ResourceContext(new CustomerInfoDatabase(),testConfig)
  val accountsActor = actorSystem.actorOf(AccountsActor.props())
  implicit val timeout = Timeout(30 seconds)
  describe("createAccount") {
    it("Create linked account with 0 balance") {

      val createLinkedAccount= CreateLinkedAccount(1,"Current",0)

      whenReady(accountsActor ? createLinkedAccount) {
        case GenericHttpResponse(code:StatusCode,response:AccountDetail)=>
          assert(code.intValue()==200)
          assert(response.isActive)
        case _=> fail("Should return 200 ok response with accounts information")

      }
    }


  }

  describe("getAccountAndTransactions") {
    it("Should not return accounts for customer without accounts") {

      whenReady(accountsActor ? GetAccountDetailsRequest(2)) {
        case GenericHttpResponse(code:StatusCode,response:CustomerInformationResponse)=>
          assert(code==StatusCodes.OK)
          assert(response.custId==2)
          assert(response.accountDetailsWithTransactions.isEmpty)

        case _=> fail("Should return 200 ok response with accounts information")

      }
    }


  }


}
