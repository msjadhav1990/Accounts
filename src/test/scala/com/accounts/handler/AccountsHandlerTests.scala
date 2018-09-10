package com.accounts.handler

import java.util.concurrent.Executors

import akka.http.javadsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes
import com.accounts.config.ServiceConfiguration
import com.accounts.context.ResourceContext
import com.accounts.domain._
import com.accounts.handlers.AccountsHandler
import com.accounts.http.TransActionManager
import com.accounts.storage.CustomerInfoDatabase
import com.typesafe.config.ConfigFactory
import org.mockito.{ArgumentMatchers, Mockito}
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar

import scala.concurrent.ExecutionContext

class AccountsHandlerTests extends AsyncFunSpec with Matchers with DiagrammedAssertions
  with BeforeAndAfter with BeforeAndAfterAll with MockitoSugar  with ScalaFutures{
  val config = ConfigFactory.load("test")
  val testConfig = ServiceConfiguration(config)
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
  describe("createAccount") {
    it("Create linked account with 0 balance") {
      val createLinkedAccount= CreateLinkedAccount(1,"Current",0)
      val transActionManager=mock[TransActionManager]

     implicit  val resourceContext= ResourceContext(new CustomerInfoDatabase(),testConfig)
      whenReady(AccountsHandler.createAccount(createLinkedAccount)) {
        case GenericHttpResponse(code:StatusCode,response:AccountDetail)=>
          assert(code.intValue()==200)
          assert(response.isActive)
        case _=> fail("Should return 200 ok response with accounts information")

      }
    }

    it("Create linked account with  balance > 0") {
      val createLinkedAccount= CreateLinkedAccount(1,"Current",100)
      val transActionManager=mock[TransActionManager]

      Mockito.when(transActionManager.postTransaction(ArgumentMatchers.any())(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(true)
      implicit  val resourceContext= ResourceContext(new CustomerInfoDatabase(),testConfig)
      whenReady(AccountsHandler.createAccount(createLinkedAccount,transActionManager)) {
        case GenericHttpResponse(code:StatusCode,response:AccountDetail)=>
          assert(code.intValue()==200)
          assert(response.isActive)
        case _=> fail("Should return 200 ok response with accounts information")

      }
    }

    it("Create linked account with  balance > 0 transaction failed") {
      val createLinkedAccount= CreateLinkedAccount(1,"Current",100)
      val transActionManager=mock[TransActionManager]

      Mockito.when(transActionManager.postTransaction(ArgumentMatchers.any())(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(false)
      implicit  val resourceContext= ResourceContext(new CustomerInfoDatabase(),testConfig)
      whenReady(AccountsHandler.createAccount(createLinkedAccount,transActionManager)) {
        case GenericHttpResponse(code:StatusCode,response:String)=>
          assert(code==StatusCodes.InternalServerError)
        case _=> fail("Should return 500 error response ")

      }
    }

    it("Should not create account for invaild customer id") {
      val createLinkedAccount= CreateLinkedAccount(100,"Current",100)
      val transActionManager=mock[TransActionManager]

      Mockito.when(transActionManager.postTransaction(ArgumentMatchers.any())(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(false)
      implicit  val resourceContext= ResourceContext(new CustomerInfoDatabase(),testConfig)
      whenReady(AccountsHandler.createAccount(createLinkedAccount,transActionManager)) {
        case GenericHttpResponse(code:StatusCode,response:String)=>
          assert(code==StatusCodes.NotFound)
        case _=> fail("Should return 500 error response ")

      }
    }
  }

  describe("getAccountAndTransactions") {
    it("Should not return accounts for customer without accounts") {

      implicit  val resourceContext= ResourceContext(new CustomerInfoDatabase(),testConfig)
      whenReady(AccountsHandler.getAccountAndTransactions(2)) {
        case GenericHttpResponse(code:StatusCode,response:CustomerInformationResponse)=>
          assert(code==StatusCodes.OK)
          assert(response.custId==2)
          assert(response.accountDetailsWithTransactions.isEmpty)

        case _=> fail("Should return 200 ok response with accounts information")

      }
    }
    it("Should not return transactions for customer without transactions in account") {
      val createLinkedAccount= CreateLinkedAccount(1,"Current",0)
      val transActionManager=mock[TransActionManager]
      Mockito.when(transActionManager.getTransactions(ArgumentMatchers.any())(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(None)
      implicit  val resourceContext= ResourceContext(new CustomerInfoDatabase(),testConfig)

      whenReady(AccountsHandler.createAccount(createLinkedAccount)) {
        case GenericHttpResponse(code:StatusCode,response:AccountDetail)=>
          assert(code.intValue()==200)
          assert(response.isActive)

        case _=> fail("Should return 200 ok response with accounts information")

      }

      whenReady(AccountsHandler.getAccountAndTransactions(1,transActionManager)) {
        case GenericHttpResponse(code:StatusCode,response:CustomerInformationResponse)=>
          assert(code==StatusCodes.OK)
          assert(response.custId==1)
          assert(response.accountDetailsWithTransactions.size==1)

        case _=> fail("Should return 200 ok response with accounts information")

      }

    }

    it("Should return not found for customer not founr") {

      implicit  val resourceContext= ResourceContext(new CustomerInfoDatabase(),testConfig)
      whenReady(AccountsHandler.getAccountAndTransactions(200)) {
        case GenericHttpResponse(code:StatusCode,response:String)=>
          assert(code==StatusCodes.NotFound)
        case _=> fail("Should return 404 response for invalid customer")

      }
    }

  }


}
