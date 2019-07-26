package controllers

import javax.inject._

import models.Task
import play.api.i18n.I18nSupport
import play.api.mvc._

@Singleton
class TaskDetailController @Inject()(components: ControllerComponents)
  extends AbstractController (components) with I18nSupport {

  def index(taskId:Long):Action[AnyContent] = Action {implicit request =>
    Task.findById(taskId) match{
      case Some(task) => Ok(views.html.show(task))
      case None => BadRequest("該当するタスクが見つかりません")
    }

  }

}
