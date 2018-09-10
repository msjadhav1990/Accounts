package com.accounts.domain

import com.accounts.common.JsonResponse
import org.json4s.NoTypeHints
import org.json4s.native.Serialization


case class TransactionResponse(txId:Long)extends JsonResponse{
  implicit val formats = Serialization.formats(NoTypeHints)
  override def toJson = Serialization.write(this)
}
