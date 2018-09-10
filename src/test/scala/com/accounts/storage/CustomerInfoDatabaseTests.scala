package com.accounts.storage

import com.accounts.domain.CreateLinkedAccount
import org.scalatest.AsyncFunSpec

class CustomerInfoDatabaseTests extends AsyncFunSpec {

  val custInfo= new CustomerInfoDatabase()

  describe("Customer Details") {

    it("Should return customer info details successfully") {
      custInfo.getCustomerDetails(1) match {
        case Some(c)=>
          assert(c.custiId==1)
          assert(c.firstName=="Mahesh")
          assert(c.lastName=="Jadhav")
        case None=> fail("Should Return existing customer")
      }


    }

    it("Should not return customer info for invalid customer id") {
      custInfo.getCustomerDetails(10) match {
        case Some(_)=> fail("Should not return existing customer")
        case None=> succeed
      }


    }

  }

  describe("Check Customer") {

    it("Should return true for existing customer") {
      assert(custInfo.checkCustomerId(1),"Should return true for existing customer")

    }

    it("Should return false for invalid customer") {
      assert(!custInfo.checkCustomerId(10),"Should return false for invalid customer")

    }
  }


  describe("Create Linked Account") {

    it("Should Create Linked Account for existing custome") {
      val createLinkedAccount= CreateLinkedAccount(1,"Current",0)
      custInfo.createAccount(createLinkedAccount) match{
        case Some(c)=>assert(c.accountId==100)
          assert(c.isActive)
        case None=> fail("Should create linked account")
      }

    }
    it("Should not Create Linked Account for invalid custome") {
      val createLinkedAccount= CreateLinkedAccount(10,"Current",0)
      custInfo.createAccount(createLinkedAccount) match{
        case Some(c)=> fail("Should create linked account")
        case None=> succeed
      }

    }

  }
}
