package com.accounts.actors

import akka.actor.{Actor, Props}
import com.accounts.context.ResourceContext
import com.accounts.domain.{CreateLinkedAccount, GetAccountDetailsRequest}
import com.accounts.handlers.AccountsHandler

import scala.concurrent.ExecutionContext


class AccountsActor(implicit ec: ExecutionContext,rc:ResourceContext) extends Actor {

  override def receive: Receive = {
    case cmd: CreateLinkedAccount =>
      val senderRef = sender
      AccountsHandler.createAccount(cmd).map(d=>{
        senderRef ! d
      })

    case cmd:GetAccountDetailsRequest => val senderRef = sender
      AccountsHandler.getAccountAndTransactions(cmd.custId).map(d=>{
        senderRef ! d
      })


  }
}

object AccountsActor {
  def props()(implicit ec: ExecutionContext,rc:ResourceContext) = Props(new AccountsActor())
}