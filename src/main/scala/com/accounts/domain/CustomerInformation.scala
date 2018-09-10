package com.accounts.domain

import java.util.Date

import scala.collection.mutable.ListBuffer


case class CustomerInformation(custiId:Long, firstName:String, lastName:String, createDate:Date,updateDate:Date, account:ListBuffer[AccountDetail])
