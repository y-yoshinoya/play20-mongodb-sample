package models.mongodb

import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.dao._

import com.mongodb.casbah.{MongoConnection, MongoURI}
import com.novus.salat.annotations.raw.Persist

abstract class MongoActiveRecord extends CRUDable with Product {
  import ReflectionUtil.toReflectable

  def id = this.getValue[ObjectId]("_id").toString

  lazy val _companion = ReflectionUtil.classToCompanion(getClass)
    .asInstanceOf[MongoActiveRecordCompanion[this.type]]

  protected def doCreate = {
    _companion.save(this.asInstanceOf[this.type])
    true
  }

  protected def doUpdate = {
    _companion.save(this.asInstanceOf[this.type])
    true
  }

  protected def doDelete = {
   _companion.delete(this.id)
   true
  }

  override def delete = super.delete
  override def save() = super.save()

}

abstract class MongoActiveRecordCompanion[T <: MongoActiveRecord](
  collectionName: Option[String] = None,
  tableName: Option[String] = None
)(implicit mot: Manifest[T], mid: Manifest[ObjectId], ctx: Context) extends ModelCompanion[T, ObjectId] {
  import MongoSettings._
  implicit def stringToMongoDBObject(id: String) = MongoDBObject("_id" -> new ObjectId(id))

  lazy val db = MongoSettings.conf.mongoDB
  lazy val collection = db(tableName.getOrElse("users"))
  lazy val dao = new SalatDAO[T, ObjectId](collection = collection) {}
  def all = findAll().toList
  def delete(id: String): Unit = this.remove(id)

  def delete(id: ObjectId): Unit = this.remove(MongoDBObject("_id" -> id))
  def findById(id: String) = this.findOne(id)
  def apply(id: String) = findById(id)
}

class MongoActiveRecordException(msg: String) extends RuntimeException(msg)

object MongoActiveRecordException {
  def unsupportedType(name: String) =
    throw new MongoActiveRecordException("Unsupported type: " + name)

  def defaultConstructorRequired =
    throw new MongoActiveRecordException("Must implement default constructor")

  def optionValueMustBeSome =
    throw new MongoActiveRecordException("Cannot detect generic type parameter when a field's default value is None because of type erasure.")

  def traversableValueMustNotBeNil =
    throw new MongoActiveRecordException("Cannot detect generic type parameter when a field's default value is Nil because of type erasure.")

  def cannotDetectType(value: Any) =
    throw new MongoActiveRecordException("Cannot detect type of %s.".format(value))
}

