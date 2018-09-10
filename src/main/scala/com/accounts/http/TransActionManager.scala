package com.accounts.http

import java.lang.reflect.{ParameterizedType, Type}

import com.accounts.config.ServiceConfiguration
import com.accounts.domain.{AccountDetailsWithTransactions, TransactionRequest}
import com.google.gson._
import org.apache.http.client.entity.EntityBuilder
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet, HttpPost}
import org.apache.http.entity.ContentType
import org.apache.http.util.EntityUtils


trait TransActionManager {

  def getTransactions(accountNo:Long)(implicit sc:ServiceConfiguration,httpExecutor: RawHttpExecutor= new RawHttpExecutor):Option[AccountDetailsWithTransactions]
  def postTransaction(transactionRequest:TransactionRequest)(implicit sc:ServiceConfiguration,httpExecutor: RawHttpExecutor= new RawHttpExecutor):Boolean
}
case class GsonListAdapter() extends JsonSerializer[List[_]] with JsonDeserializer[List[_]] {
  import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl

  import scala.collection.JavaConverters._

  @throws(classOf[JsonParseException])
  def deserialize(jsonElement: JsonElement, t: Type, jdc: JsonDeserializationContext): List[_] = {
    val p = scalaListTypeToJava(t.asInstanceOf[ParameterizedType]) // Safe casting because List is a ParameterizedType.
    val javaList: java.util.List[_ <: Any] = jdc.deserialize(jsonElement, p)
    javaList.asScala.toList
  }

  override def serialize(obj: List[_], t: Type, jdc: JsonSerializationContext): JsonElement = {
    val p = scalaListTypeToJava(t.asInstanceOf[ParameterizedType]) // Safe casting because List is a ParameterizedType.
    jdc.serialize(obj.asInstanceOf[List[Any]].asJava, p)
  }

  private def scalaListTypeToJava(t: ParameterizedType): ParameterizedType = {
    ParameterizedTypeImpl.make(classOf[java.util.List[_]], t.getActualTypeArguments, null)
  }
}
 object TransActionManager extends TransActionManager{

   val json_parser = new GsonBuilder().registerTypeHierarchyAdapter(classOf[List[_]], new GsonListAdapter()).create()

   override def getTransactions(accountNo: Long)(implicit sc:ServiceConfiguration,httpExecutor: RawHttpExecutor= new RawHttpExecutor):Option[AccountDetailsWithTransactions] = {
     val httpGet: HttpGet = new HttpGet(s"http://${sc.txServiceConfig.hostname}:${sc.txServiceConfig.port}${sc.txServiceConfig.uri}/${accountNo}")
     val response: CloseableHttpResponse = httpExecutor.execute(httpGet)

     response.getStatusLine.getStatusCode match {
       case 200=>
         json_parser.fromJson(EntityUtils.toString(response.getEntity), classOf[AccountDetailsWithTransactions]) match {
           case null=> None
           case p=> Some(p)
         }
       case 404=> None
       case _=> throw new RuntimeException("Error in getting transactions")
     }


   }

   override def postTransaction(transactionRequest: TransactionRequest)(implicit sc:ServiceConfiguration,httpExecutor: RawHttpExecutor= new RawHttpExecutor): Boolean = {

     val httpPost: HttpPost = new HttpPost(s"http://${sc.txServiceConfig.hostname}:${sc.txServiceConfig.port}${sc.txServiceConfig.uri}")


     val entity= EntityBuilder.create().setText(transactionRequest.toJson).setContentType(ContentType.APPLICATION_JSON).build()
     httpPost.setEntity(entity)
     val response: CloseableHttpResponse = httpExecutor.execute(httpPost)

     response.getStatusLine.getStatusCode match {
       case 200=> true
       case _=> false
     }




   }
 }