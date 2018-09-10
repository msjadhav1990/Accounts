package com.accounts.domain

import java.util.Date

import com.accounts.common.JsonResponse
import org.json4s.NoTypeHints
import org.json4s.native.Serialization


case class CustomerInformationResponse (custId:Long,firstName:String, lastName:String, createDate:Date,updateDate:Date, accountDetailsWithTransactions: List[AccountDetailsWithTransactions]) extends JsonResponse{
  implicit val formats = Serialization.formats(NoTypeHints)
  override def toJson = Serialization.write(this)
}
