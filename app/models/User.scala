package models

import mongodb._
import com.novus.salat.global._

case class User(
  name: String,
  age: Int,
  description: Option[String],
  _id: ObjectId = new ObjectId
) extends MongoActiveRecord {
  def this() = this("", 0, None)
}

object User extends MongoActiveRecordCompanion[User]
