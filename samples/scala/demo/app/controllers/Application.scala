/**
 * Copyright 2012 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package controllers

import play.api.mvc._
import securesocial.core.{IdentityId, UserService, Identity, Authorization}
import play.api.{Logger, Play}
import service.InMemoryUserService

object Application extends Controller with securesocial.core.SecureSocial {
  val logger = Logger("application.controllers.Application")
  def index = SecuredAction { implicit request =>
    logger.warn("logging from application")
    Ok(views.html.index(request.user))
  }

  // a sample action using the new authorization hook
  def onlyTwitter = SecuredAction(WithProvider("twitter")) { implicit request =>
    val logger = Logger("application.controllers.Application.onlyTwitter")
    logger.error("only twitter")
//
//    Note: If you had a User class and returned an instance of it from UserService, this
//          is how you would convert Identity to your own class:
//
//    request.user match {
//      case user: User => // do whatever you need with your user class
//      case _ => // did not get a User instance, should not happen,log error/thow exception
//    }
    Ok("You can see this because you logged in using Twitter")
  }

  def linkResult = SecuredAction { implicit request =>
    import com.typesafe.plugin._
    import play.api.Play.current
    val identities = use[InMemoryUserService].users.values.map {
      case user if user.identities.exists(_.identityId == request.user.identityId) => user.identities
      case user => List()
    }.flatten
    Ok(views.html.linkResult(request.user, identities))
  }
}

// An Authorization implementation that only authorizes uses that logged in using twitter
case class WithProvider(provider: String) extends Authorization {
  def isAuthorized(user: Identity) = {
    user.identityId.providerId == provider
  }
}
