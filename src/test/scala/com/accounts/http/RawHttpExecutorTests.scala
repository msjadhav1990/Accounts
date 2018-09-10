package com.accounts.http

import org.apache.http.client.methods.HttpGet
import org.scalatest._
import org.scalatest.mockito.MockitoSugar

class RawHttpExecutorTests extends AsyncFunSpec with Matchers with DiagrammedAssertions
  with BeforeAndAfter with BeforeAndAfterAll with MockitoSugar {

  describe("RawHttpExecutorTests") {
    it("Http Request executed succesfully") {
      val requestExecutor = new RawHttpExecutor();
      val httpGet = new HttpGet("http://www.google.com")
      val response = requestExecutor.execute(httpGet)
      assert(response.getStatusLine.getStatusCode == 200)

    }
  }

}
