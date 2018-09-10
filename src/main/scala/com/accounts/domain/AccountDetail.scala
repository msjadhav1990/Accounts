package com.accounts.domain

import java.util.Date

import com.accounts.common.JsonResponse
import org.json4s.NoTypeHints
import org.json4s.native.Serialization


case class AccountDetail(accountId:Long, createDate:Date,isActive:Boolean) extends JsonResponse{
  implicit val formats = Serialization.formats(NoTypeHints)
  override def toJson = Serialization.write(this)
}
