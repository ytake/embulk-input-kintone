package net.jp.ytake.embulk.input.kintone

import org.embulk.util.config.Config
import org.embulk.util.config.ConfigDefault
import org.embulk.util.config.Task
import org.embulk.util.config.units.SchemaConfig
import java.util

trait PluginTask extends Task {

  @Config("domain")
  def getDomain: String

  @Config("app_id")
  def getAppId: Int

  @Config("guest_space_id")
  @ConfigDefault("null")
  def getGuestSpaceId: util.Optional[Int]

  @Config("token")
  @ConfigDefault("null")
  def getToken: util.Optional[String]

  @Config("username")
  @ConfigDefault("null")
  def getUsername: util.Optional[String]

  @Config("password")
  @ConfigDefault("null")
  def getPassword: util.Optional[String]

  @Config("basic_auth_username")
  @ConfigDefault("null")
  def getBasicAuthUsername: util.Optional[String]

  @Config("basic_auth_password")
  @ConfigDefault("null")
  def getBasicAuthPassword: util.Optional[String]

  @Config("query")
  @ConfigDefault("null")
  def getQuery: util.Optional[String]

  @Config("fields")
  @ConfigDefault("null")
  def getFields: util.Optional[SchemaConfig]

  @Config("mapping")
  @ConfigDefault("null")
  def getMapping: util.Optional[util.Map[String, String]]
}
