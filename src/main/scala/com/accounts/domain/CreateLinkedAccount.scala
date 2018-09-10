package com.accounts.domain


case class CreateLinkedAccount (custId:Long, accountType:String,initialBalance:Double)extends MessageType
