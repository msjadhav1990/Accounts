package com.accounts.domain

import akka.http.scaladsl.model.StatusCode


case class GenericHttpResponse(statusCode:StatusCode,response:Any)
