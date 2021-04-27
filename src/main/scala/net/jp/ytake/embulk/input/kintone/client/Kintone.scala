package net.jp.ytake.embulk.input.kintone.client

import com.cybozu.kintone.client.authentication.Auth
import com.cybozu.kintone.client.connection.Connection
import net.jp.ytake.embulk.input.kintone.PluginTask
import org.embulk.config.ConfigException

object Kintone {

  private val kintoneAuth = new Auth()

  @throws[ConfigException]
  def validateAuth(task: PluginTask): Unit = {
    // username & passwordかapi tokenのどちらかでアクセス
    if (task.getUsername.isPresent && task.getPassword.isPresent) return
    else if (task.getToken.isPresent) return
    // パラメータが不足している場合は処理停止
    throw new ConfigException("username and password or token must be provided")
  }

  def connection(task: PluginTask): Connection = {
    if (task.getUsername.isPresent && task.getPassword.isPresent) {
      this.kintoneAuth.setPasswordAuth(task.getUsername.get, task.getPassword.get)
    }
    if (task.getToken.isPresent) {
      this.kintoneAuth.setApiToken(task.getToken.get)
    }
    if (task.getBasicAuthUsername.isPresent && task.getBasicAuthPassword.isPresent) {
      this.kintoneAuth.setBasicAuth(task.getBasicAuthUsername.get, task.getBasicAuthPassword.get)
    }
    if (task.getGuestSpaceId.isPresent) {
      return new Connection(task.getDomain, this.kintoneAuth, task.getGuestSpaceId.orElse(-1))
    }
    new Connection(task.getDomain, this.kintoneAuth)
  }
}
