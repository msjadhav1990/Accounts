package com.accounts.domain

import java.util.Date

import com.accounts.common.JsonResponse
import org.json4s.NoTypeHints
import org.json4s.native.Serialization

case class TransactionDetails(transactionId:Long, value:Double,`type`:String, balance:Double, txDate:Date)extends JsonResponse{
  implicit val formats = Serialization.formats(NoTypeHints)
  override def toJson = Serialization.write(this)
}
