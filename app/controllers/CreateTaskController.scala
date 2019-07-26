package controllers

import java.time.ZonedDateTime
import javax.inject._

import models.Task
import play.api.i18n.{ I18nSupport, Messages }
import play.api.mvc._
import scalikejdbc.AutoSession

@Singleton
class CreateTaskController @Inject()(components: ControllerComponents)
  extends AbstractController(components)
    with I18nSupport
    with TaskConrollerSupport {

  def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.create(form))
  }

  def create: Action[AnyContent] = Action { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => BadRequest(views.html.create(formWithErrors)), { model =>
          implicit val session = AutoSession
          val now              = ZonedDateTime.now()
          val task          = Task(None, Some(model.status),model.content, now, now)
          val result           = Task.create(task)
          if (result > 0) {
            Redirect(routes.TaskListController.index())
          } else {
            InternalServerError(Messages("CreateTaskError"))
          }
        }
      )
  }
}
