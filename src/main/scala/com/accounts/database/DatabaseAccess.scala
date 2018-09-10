package com.accounts.database

import com.accounts.domain.{AccountDetail, CreateLinkedAccount}


trait DatabaseAccess {

  def createLinkedAccount(createLinkedAccount: CreateLinkedAccount):AccountDetail

  def getAccountDetails(accountId:Long):AccountDetail
}
