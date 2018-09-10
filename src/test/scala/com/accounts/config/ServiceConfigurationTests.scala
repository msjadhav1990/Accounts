package com.accounts.config

import com.typesafe.config.ConfigFactory
import org.scalatest.AsyncFunSpec

class ServiceConfigurationTests extends AsyncFunSpec {

  describe("Service configuration loaded from a test config file") {
    val config = ConfigFactory.load("test")
    val configUnderTest = ServiceConfiguration(config)



    it("should have txServiceConfig correctly configured") {
      assert(configUnderTest.txServiceConfig == HttpConfiguration("localhost", 9002, "/app/accounts/transactions"))
    }

  }
}
