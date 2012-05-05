package models.mongodb

import com.mongodb.casbah._
import com.typesafe.config._

/**
 * MongoDB をサクッと使うためのオブジェクトです。
 */
object Mongo {
  /**
   * MongoDB 接続用 URI をパースするための正規表現
   */
  val uriPattern = "mongodb://(?:([^:^@]+)(?::([^@]+))?@)?([^:^/]+)(?::(\\d+))?/(.+)".r

  def connect(uri: String) = {
    val (usernameOpt, passwordOpt, host, portOpt, database) = parseURI(uri)

    // MongoDB に接続
    val conn = portOpt map { port =>
      MongoConnection(host, port.toInt)
    } getOrElse {
      MongoConnection(host)
    }

    // DB 取得
    val db = conn(database)
    // 必要とあれば認証処理を行う
    (usernameOpt, passwordOpt) match {
      case (Some(username), Some(password)) => db.authenticate(username, password)
      case _ =>
    }
    db
  }

  /**
   * MongoDB 接続 URI をパースします。
   */
  def parseURI(uri: String) = {
    uri match {
      case uriPattern(username, password, host, port, database) =>
        (Option(username), Option(password), host, Option(port), database)
    }
  }
}

trait MongoConfig {
  def mongoDB: MongoDB
}

abstract class AbstractMongoConfig(
  config: Config,
  overrideSettings: Map[String, Any]
) extends MongoConfig {
  val env = System.getProperty("run.mode", "dev")

  def get[T](key: String): Option[T] = overrideSettings.get(key).map(_.asInstanceOf[T])
  def get[T](key: String, getter: String => T): Option[T] = try {
    Option(getter(env + "." + key))
  } catch {
    case e => None
  }
  def getString(key: String) = get[String](key).orElse(get(key, config.getString))
  def getInt(key: String) = get[Int](key).orElse(get(key, config.getInt))

  lazy val mongoURI = getString("MONGOLAB_URI")

  // default: mongodb://localhost:27017/table
  override def mongoDB = mongoURI.map(Mongo.connect(_)).getOrElse(MongoConnection()("table"))
}

case class PlayConfig(
  config: Config = ConfigFactory.load(),
  overrideSettings: Map[String, Any] = Map()
) extends AbstractMongoConfig(config, overrideSettings) {
  import play.api.Play.current
  override val env = if (play.api.Play.isProd) "prod" else "dev"
}

object MongoSettings {
  var conf: MongoConfig = _

  def initialize(implicit config: Map[String, Any] = Map()) {
    conf = PlayConfig(overrideSettings = config)
  }
}
