package com.accounts.http

import com.accounts.config.ServiceConfiguration
import com.accounts.domain.TransactionRequest
import com.typesafe.config.ConfigFactory
import org.apache.http.StatusLine
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.entity.{ContentType, StringEntity}
import org.mockito.{ArgumentMatchers, Mockito}
import org.scalatest.AsyncFunSpec
import org.scalatest.mockito.MockitoSugar

import scala.util.{Failure, Success, Try}

class TransActionManagerTests extends AsyncFunSpec with MockitoSugar{

  val config = ConfigFactory.load("test")
  val testConfig = ServiceConfiguration(config)

  describe("Get Transactions") {

    it("Should return transction details successfully for valid customer") {

      val rawHttpExecutor= mock[RawHttpExecutor]
      val response = mock[CloseableHttpResponse]
      val statusLine= mock[StatusLine]
      Mockito.when(statusLine.getStatusCode).thenReturn(200)
      Mockito.when(response.getStatusLine).thenReturn(statusLine)
      Mockito.when(response.getEntity()).thenReturn(new StringEntity("{\n    \"accountId\": 2,\n    \"balance\": 200,\n    \"txList\": [\n        {\n            \"transactionId\": 1,\n            \"value\": 200,\n            \"type\": \"Credit\",\n            \"balance\": 200,\n            \"txDate\": \"2018-09-02T16:03:36.033Z\"\n        }\n    ]\n}", ContentType.APPLICATION_JSON))

    Mockito.when(rawHttpExecutor.execute(ArgumentMatchers.any())).thenReturn(response)

      TransActionManager.getTransactions(1)(testConfig,rawHttpExecutor) match {
        case Some(r)=> assert(r.accountId==2)
          assert(r.balance==200)
          assert(r.txList.size==1)
        case None=>  fail("Should return transction details for valid customer")
      }


    }

    it("Should return not transaction details successfully for invalid customer") {

      val rawHttpExecutor= mock[RawHttpExecutor]
      val response = mock[CloseableHttpResponse]
      val statusLine= mock[StatusLine]
      Mockito.when(statusLine.getStatusCode).thenReturn(404)
      Mockito.when(response.getStatusLine).thenReturn(statusLine)
      Mockito.when(rawHttpExecutor.execute(ArgumentMatchers.any())).thenReturn(response)

      TransActionManager.getTransactions(1)(testConfig,rawHttpExecutor) match {
        case Some(_)=> fail("Should not return transaction details for valid customer")
        case None=>  succeed
      }


    }
    it("Should throw exception for 500 from transaction service") {

      val rawHttpExecutor= mock[RawHttpExecutor]
      val response = mock[CloseableHttpResponse]
      val statusLine= mock[StatusLine]
      Mockito.when(statusLine.getStatusCode).thenReturn(500)
      Mockito.when(response.getStatusLine).thenReturn(statusLine)
      Mockito.when(rawHttpExecutor.execute(ArgumentMatchers.any())).thenReturn(response)

      Try(TransActionManager.getTransactions(1)(testConfig,rawHttpExecutor)) match {
        case Success(_)=> fail("Should not return transaction details for valid customer")
        case Failure(_)=>  succeed
      }


    }

  }

describe("Post Transaction"){

  it("Should return true after tx success"){

    val transactionRequest= TransactionRequest(1,100,"Credit")
    val rawHttpExecutor= mock[RawHttpExecutor]
    val response = mock[CloseableHttpResponse]
    val statusLine= mock[StatusLine]
    Mockito.when(statusLine.getStatusCode).thenReturn(200)
    Mockito.when(response.getStatusLine).thenReturn(statusLine)
    Mockito.when(response.getEntity()).thenReturn(new StringEntity("{\"txId\":1}", ContentType.APPLICATION_JSON))
    Mockito.when(rawHttpExecutor.execute(ArgumentMatchers.any())).thenReturn(response)
    assert(TransActionManager.postTransaction(transactionRequest)(testConfig,rawHttpExecutor))
  }

  it("Should return false for tx failure"){

    val transactionRequest= TransactionRequest(1,100,"Credit")
    val rawHttpExecutor= mock[RawHttpExecutor]
    val response = mock[CloseableHttpResponse]
    val statusLine= mock[StatusLine]
    Mockito.when(statusLine.getStatusCode).thenReturn(500)
    Mockito.when(response.getStatusLine).thenReturn(statusLine)
    Mockito.when(rawHttpExecutor.execute(ArgumentMatchers.any())).thenReturn(response)
    assert(!TransActionManager.postTransaction(transactionRequest)(testConfig,rawHttpExecutor))
  }
}

}
