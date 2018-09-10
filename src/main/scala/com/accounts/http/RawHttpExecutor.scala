package com.accounts.http

import org.apache.http.client.methods.{CloseableHttpResponse, HttpUriRequest}
import org.apache.http.impl.client.HttpClientBuilder

class RawHttpExecutor {

  def execute(request: HttpUriRequest): CloseableHttpResponse = {
    HttpClientBuilder.create().build().execute(request)
  }
}
