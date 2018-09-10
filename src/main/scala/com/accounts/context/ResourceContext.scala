package com.accounts.context

import com.accounts.config.ServiceConfiguration
import com.accounts.storage.CustomerInfoDatabase


case class ResourceContext(db:CustomerInfoDatabase, sc:ServiceConfiguration)