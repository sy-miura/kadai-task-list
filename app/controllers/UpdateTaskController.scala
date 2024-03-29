package controllers

import javax.inject._

import forms.TaskForm

import models.Task
import play.api.i18n.{ I18nSupport, Messages }
import java.time.ZonedDateTime

import play.api.mvc._
import scalikejdbc._, jsr310._ // 手動でインポートしてください。

@Singleton
class UpdateTaskController @Inject()(components: ControllerComponents)
  extends AbstractController (components)
    with I18nSupport
    with TaskConrollerSupport {

  def index(taskId:Long): Action[AnyContent] = Action{ implicit request =>
    val result = Task.findById(taskId).get
    val filledForm = form.fill(TaskForm(result.id, result.status.getOrElse("未対応"), result.content))
    Ok(views.html.edit(filledForm))

  }

  def update:Action[AnyContent] = Action {implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => BadRequest(views.html.edit(formWithErrors)),
        {model =>
          implicit val session = AutoSession
          val result:Option[Long] = model.id.map(id => Task.updateById(id).withAttributes(
            'status -> model.status,
            'content -> model.content,
            'updateAt -> ZonedDateTime.now()
          ))

          result match {
            case Some(rtn_num) if rtn_num > 0 => Redirect(routes.TaskListController.index())
            case Some(_) => InternalServerError(Messages("UpdateTaskError"))
            case None => InternalServerError(Messages("Id404Error"))
          }
        }
      )
  }

}
