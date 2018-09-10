package com.accounts.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.LogEntry
import akka.http.scaladsl.server.{Directive0, ExceptionHandler, Route, RouteResult}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.accounts.common.JsonResponse
import com.accounts.domain.{CreateLinkedAccount, GenericHttpResponse, GetAccountDetailsRequest}
import com.accounts.marshallers.AccountsMarshaller._
import com.accounts.marshallers.Json4sMarshaller._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

case class AccountsRoutes(routingActor: ActorRef)(implicit ec:ExecutionContext ) extends CORSHandler{

  def exceptionHandler: ExceptionHandler = ExceptionHandler {
    case t: Exception =>
      t.printStackTrace()
      extractUri { uri =>
        complete(HttpResponse(StatusCodes.InternalServerError, entity = "Error " + t.getMessage))
      }
  }

  implicit val timeout: Timeout  = Timeout(6000 milliseconds)
  def routes(implicit mat: ActorMaterializer): Route = {
    AccountsOperationRoute.route(routingActor)
  }

  def apiRoutes(implicit mat: ActorMaterializer): Route =
    handleExceptions(exceptionHandler) {
      logAccess{
        corsHandler(
          routes
        )

      }

    }

  def logAccess(innerRoute: Route): Route = {
    def toLogEntry(marker: String, f: Any => String) = (r: Any) => LogEntry(marker + f(r), akka.event.Logging.InfoLevel)
    extractRequest { request =>
      logResult(toLogEntry(s"${request.method.name} ${request.uri} ==> ", {
        case c: RouteResult.Complete => c.response.status.toString()
        case x                       => s"unknown response part of type ${x.getClass}"
      }))(innerRoute)
    }
  }

}




object AccountsOperationRoute{
  def route(rootActor: ActorRef)(implicit timeout: Timeout): Route =  pathPrefix("app" / "accounts") {

    get {
      path(LongNumber){ custId=>

        val getAccountsMessage= GetAccountDetailsRequest(custId)
        val genericHttpResponse= rootActor ? getAccountsMessage
         onSuccess(genericHttpResponse) {
           case GenericHttpResponse(StatusCodes.OK, t: JsonResponse) =>  {
             complete(HttpEntity(`application/json`, t.toJson)) }
           case GenericHttpResponse(x: StatusCode, msg: String) => { complete(HttpResponse(status = x, entity = HttpEntity(`application/json`, msg)))  }
           case p => {
             println(p)
             complete(StatusCodes.InternalServerError, "Unknown error occurred. Please contact administrator.") }
        }

      }

    } ~
      post {
        pathEnd {

          entity(as[CreateLinkedAccount]) { c =>
            val genericHttpResponse= rootActor ? c
            onSuccess(genericHttpResponse) {
              case GenericHttpResponse(StatusCodes.OK, t: JsonResponse) =>  {
                complete(HttpEntity(`application/json`, t.toJson)) }
              case GenericHttpResponse(x: StatusCode, msg: String) => { complete(HttpResponse(status = x, entity = HttpEntity(`application/json`, msg)))  }
              case p => {
                println(p)
                complete(StatusCodes.InternalServerError, "Unknown error occurred. Please contact administrator.") }
            }
          }
        }
      }
  }

}


trait CORSHandler{

  private val corsResponseHeaders = List(

    RawHeader("Access-Control-Allow-Origin","*"),

    RawHeader("Access-Control-Allow-Credentials","true"),

    RawHeader("Access-Control-Allow-Headers","Authorization, Content-Type, X-Requested-With")

  )

  //this directive adds access control headers to normal responses

  private def addAccessControlHeaders: Directive0 = {

    respondWithHeaders(corsResponseHeaders)

  }

  //this handles preflight OPTIONS requests.

  private def preflightRequestHandler: Route = options {

    complete(HttpResponse(StatusCodes.OK).withHeaders(List(RawHeader("Access-Control-Allow-Methods","OPTIONS, POST, PUT, GET, DELETE"))))

  }

  // Wrap the Route with this method to enable adding of CORS headers

  def corsHandler(r: Route): Route = addAccessControlHeaders {

    preflightRequestHandler ~ r

  }

  // Helper method to add CORS headers to HttpResponse

  // preventing duplication of CORS headers across code

  def addCORSHeaders(response: HttpResponse):HttpResponse =

    response.withHeaders(corsResponseHeaders)

}