package com.accounts.handlers

import akka.http.scaladsl.model.StatusCodes
import com.accounts.context.ResourceContext
import com.accounts.domain._
import com.accounts.http.TransActionManager

import scala.concurrent.{ExecutionContext, Future}


object AccountsHandler {

  def createAccount(createLinkedAccount: CreateLinkedAccount, transActionManager: TransActionManager=TransActionManager)(implicit ec:ExecutionContext,  rc:ResourceContext): Future[GenericHttpResponse] = {
    implicit val sc= rc.sc
    Future {
  rc.db.checkCustomerId(createLinkedAccount.custId) match {
    case true=> rc.db.createAccount(createLinkedAccount) match {
      case Some(accountDetail)=>
        if(createLinkedAccount.initialBalance>0){
          transActionManager.postTransaction(TransactionRequest(accountDetail.accountId,createLinkedAccount.initialBalance,"Credit")) match {
          case true =>
            GenericHttpResponse(StatusCodes.OK,accountDetail)
          case false =>
            GenericHttpResponse(StatusCodes.InternalServerError,"Unable to create Account: Transaction for Initial Balance Failed")
          }
        }
        else{
          GenericHttpResponse(StatusCodes.OK,accountDetail)
        }

      case None=> GenericHttpResponse(StatusCodes.InternalServerError,"Unable to create Account")
    }
    case false=> GenericHttpResponse(StatusCodes.NotFound,"Invalid Customer Id")
  }
}

  }

  def getAccountAndTransactions(custId:Long,transActionManager: TransActionManager=TransActionManager)(implicit ec:ExecutionContext,  rc:ResourceContext):Future[GenericHttpResponse]={
  implicit val sc= rc.sc
    Future{
    rc.db.checkCustomerId(custId) match {
      case true=> rc.db.getCustomerDetails(custId) match {
        case Some(custInfo)=>
          val accountDetailsWithTransactionsList=custInfo.account.map{a=>
          transActionManager.getTransactions(a.accountId) match {
            case Some(p)=> p
            case None=> AccountDetailsWithTransactions(a.accountId,0,List())
          }
        }.toList
          GenericHttpResponse(StatusCodes.OK,CustomerInformationResponse(custInfo.custiId,custInfo.firstName,custInfo.lastName,custInfo.createDate,custInfo.updateDate,accountDetailsWithTransactionsList))
        case None=> GenericHttpResponse(StatusCodes.NotFound,"Invalid Customer Id")
      }
      case false=> GenericHttpResponse(StatusCodes.NotFound,"Invalid Customer Id")
    }
    }
  }
}
