package com.accounts.marshallers

import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import com.google.gson.JsonParser
import org.json4s.{Formats, Serialization}


object Json4sMarshaller extends Json4sMarshaller {
}

trait Json4sMarshaller {
  implicit def unmarshaller[A: Manifest](implicit serialization: Serialization, formats: Formats): FromEntityUnmarshaller[A] =
    Unmarshaller
      .byteStringUnmarshaller
      .forContentTypes(`application/json`)
      .mapWithCharset((data, charset) =>
        try {
          val dataString = data.decodeString(charset.nioCharset.name)
          new JsonParser().parse(dataString)
          serialization.read(dataString)
        } catch {
          case e: Throwable =>
            e.printStackTrace()
            throw new IllegalArgumentException(StatusCodes.BadRequest.defaultMessage)

        })
}
