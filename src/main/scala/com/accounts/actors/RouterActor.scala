package com.accounts.actors

import akka.actor.{Actor, ActorContext, ActorRef, Props}
import akka.http.scaladsl.model.StatusCodes
import com.accounts.context.ResourceContext
import com.accounts.domain.{CreateLinkedAccount, GenericHttpResponse, GetAccountDetailsRequest, MessageType}

import scala.concurrent.ExecutionContext

case class RouterActor(resolver: Option[Any => Option[ActorRef]] = None)(implicit ec: ExecutionContext,rc:ResourceContext) extends Actor {
  private val hierarchyResolver: (Any) => Option[ActorRef] = resolver.getOrElse(RouterActor.defaultResolver)
  override def receive: Receive = {
    case command: Any => hierarchyResolver(command).fold {
      sender ! GenericHttpResponse(StatusCodes.BadRequest, "Invalid Request")
    } {
      ref =>  ref forward command
    }
  }
}


object RouterActor {
  private var actorCache = Map[MessageType, ActorRef]()
  def props()(implicit ec: ExecutionContext,rc:ResourceContext) = Props(new RouterActor())

  def defaultResolver(implicit ec: ExecutionContext,context: ActorContext,rc:ResourceContext): Any => Option[ActorRef] = {
    case c: CreateLinkedAccount => resolveFromCache(c, () => AccountsActor.props())
    case g: GetAccountDetailsRequest => resolveFromCache(g, () => AccountsActor.props())
    case x => None
  }

  def resolveFromCache(command: MessageType, props: () => Props)(implicit context: ActorContext): Option[ActorRef] = {
    actorCache.get(command) match {
      case Some(ref) => Some(ref)
      case None =>
        val ref = context.actorOf(props())
        actorCache += command -> ref
        Some(ref)
    }
  }

}

