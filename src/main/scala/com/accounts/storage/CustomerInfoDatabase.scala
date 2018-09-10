package com.accounts.storage

import java.util.Date

import com.accounts.domain.{AccountDetail, CreateLinkedAccount, CustomerInformation}

import scala.collection.mutable


class CustomerInfoDatabase {

  private var customerInfo: mutable.Map[Long,CustomerInformation]= mutable.HashMap.empty

private def init(): Unit ={

  val cust1= CustomerInformation(1, "Mahesh","Jadhav", new Date(), new Date(),mutable.ListBuffer.empty[AccountDetail])
  val cust2= CustomerInformation(2, "Dinesh","Bhujari", new Date(), new Date(),mutable.ListBuffer.empty[AccountDetail])
  val cust3= CustomerInformation(3, "Amit","Kulkarni", new Date(), new Date(),mutable.ListBuffer.empty[AccountDetail])
  val cust4= CustomerInformation(4, "Siddarth","Ananad", new Date(), new Date(),mutable.ListBuffer.empty[AccountDetail])
  customerInfo.put(cust1.custiId,cust1)
  customerInfo.put(cust2.custiId,cust2)
  customerInfo.put(cust3.custiId,cust3)
  customerInfo.put(cust4.custiId,cust4)
}
  init()

  def createAccount(createLinkedAccount: CreateLinkedAccount): Option[AccountDetail] ={

    customerInfo.get(createLinkedAccount.custId) match {
      case None => None
      case Some(custInfo)=>
        val accountDetails= AccountDetail(((custInfo.custiId * 100) + custInfo.account.size), new Date(),true)
        custInfo.account.append(accountDetails)
        Some(accountDetails)

    }

  }
  def getCustomerDetails(custId:Long): Option[CustomerInformation] ={
    customerInfo.get(custId)
  }

  def checkCustomerId(custId:Long):Boolean={
  customerInfo.contains(custId)
  }

}
