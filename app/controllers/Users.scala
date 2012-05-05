package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views.html.{user => view}

object Users extends Controller {

  def userForm(user: User = new User) = Form(
    mapping(
      "name" -> nonEmptyText(maxLength = 128),
      "age" -> number(max = 999),
      "description" -> optional(text(maxLength = 3000))
    )((name, age, description) =>
      user.copy(name, age, description)
    )((user) => Some(user.name, user.age, user.description))
  ).fill(user)

  def index = Action {
    Ok(view.index(User.all.toList))
  }

  def show(id: String) = Action {
    User(id) match {
      case Some(user) => Ok(view.show(user))
      case _ => NotFound
    }
  }

  def newPage = Action {
    Ok(view.edit(userForm(), routes.Users.create, "Create", "User create"))
  }

  def create = Action { implicit request =>
    userForm().bindFromRequest.fold(
      errors => BadRequest(view.edit(errors, routes.Users.create, "Create", "User create")), {
      user =>
        user.save
        Redirect(routes.Users.show(user.id))
    })
  }

  def edit(id: String) = Action {
    User(id) match {
      case Some(user) => Ok(view.edit(userForm(user), routes.Users.update(id), "Update", "User edit"))
      case _ => NotFound
    }
  }

  def update(id: String) = Action { implicit request =>
    User(id) match {
      case Some(user) =>
        userForm(user).bindFromRequest.fold(
          errors => BadRequest(view.edit(errors, routes.Users.update(id), "Update", "User edit")), {
          user =>
            user.save
            Redirect(routes.Users.index)
        })
      case _ => NotFound
    }
  }

  def delete(id: String) = Action {
    User(id) match {
      case Some(user) =>
        user.delete
        Ok
      case _ => NotFound
    }
  }

}

