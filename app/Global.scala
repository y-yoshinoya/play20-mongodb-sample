import play.api._

import models.mongodb._

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    MongoSettings.initialize
  }
}

