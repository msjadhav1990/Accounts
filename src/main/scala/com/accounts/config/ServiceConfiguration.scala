package com.accounts.config

import com.typesafe.config.Config

case class HttpConfiguration(hostname: String, port: Int, uri: String)
object HttpConfiguration{
  def apply(prefix: String, conf: Config): HttpConfiguration = HttpConfiguration(
    conf.getString(s"$prefix.hostname"),
    conf.getInt(s"$prefix.port"),
    conf.getString(s"$prefix.createuri"))
}
case class ServiceConfiguration (txServiceConfig:HttpConfiguration)
object ServiceConfiguration{
  def apply(conf: Config):ServiceConfiguration=ServiceConfiguration(HttpConfiguration("txService",conf)
  )
}
