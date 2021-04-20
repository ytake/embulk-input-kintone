package net.jp.ytake.embulk.input.kintone

import org.embulk.util.config.Config
import org.embulk.util.config.ConfigDefault
import org.embulk.util.config.Task
import org.embulk.util.config.units.SchemaConfig

import java.util.Optional
import scala.collection.mutable
import scala.collection.mutable.Map
import scala.util.chaining._

trait PluginTask extends Task {

  @Config("domain")
  def getDomain: String

  @Config("app_id")
  def getAppId: Int

  @Config("guest_space_id")
  @ConfigDefault("null")
  def getGuestSpaceId: Optional[Int]

  @Config("token")
  @ConfigDefault("null")
  def getToken: Optional[String]

  @Config("username")
  @ConfigDefault("null")
  def getUsername: Optional[String]

  @Config("password")
  @ConfigDefault("null")
  def getPassword: Optional[String]

  @Config("basic_auth_username")
  @ConfigDefault("null")
  def getBasicAuthUsername: Optional[String]

  @Config("basic_auth_password")
  @ConfigDefault("null")
  def getBasicAuthPassword: Optional[String]

  @Config("query")
  @ConfigDefault("null")
  def getQuery: Optional[String]

  @Config("fields")
  def getFields: Optional[SchemaConfig]

  @Config("mapping")
  @ConfigDefault("null")
  def getMapping: Optional[mutable.Map[String, String]]
}
