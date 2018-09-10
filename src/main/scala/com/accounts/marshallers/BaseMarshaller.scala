package com.accounts.marshallers

import org.json4s.{Formats, native}

import scala.util.{Success, Try}

trait BaseMarshaller {
  implicit val serialization = native.Serialization

  def classList: List[Class[_]]

  implicit def formats: Formats
}