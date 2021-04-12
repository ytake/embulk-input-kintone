package org.embulk.input.kintone2

import org.embulk.config.Config
import org.embulk.config.ConfigDefault
import org.embulk.util.config.Task
import org.embulk.spi.SchemaConfig

import scala.collection.immutable.HashMap

trait PluginTask extends Task {

  @Config("domain")
  def getDomain: String

  @Config("app_id")
  def getAppId: Int

  @Config("guest_space_id")
  @ConfigDefault("null")
  def getGuestSpaceId: Option[Int]

  @Config("token")
  @ConfigDefault("null")
  def getToken: Option[String]

  @Config("username")
  def getUsername: Option[String]

  @Config("password")
  def getPassword: Option[String]

  @Config("basic_auth_username")
  @ConfigDefault("null")
  def getBasicAuthUsername: Option[String]

  @Config("basic_auth_password")
  @ConfigDefault("null")
  def getBasicAuthPassword: Option[String]

  @Config("query")
  @ConfigDefault("null")
  def getQuery: Option[String]

  @Config("fields")
  def getFields: SchemaConfig

  @Config("mapping")
  @ConfigDefault("null")
  def getMapping: HashMap[String, String]
}
