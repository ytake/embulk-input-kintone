package net.jp.ytake.embulk.input.kintone.client

import com.kintone.client.KintoneClient
import com.kintone.client.KintoneClientBuilder
import net.jp.ytake.embulk.input.kintone.PluginTask
import org.embulk.config.ConfigException

object Kintone {

  @throws[ConfigException]
  def validateAuth(task: PluginTask): Unit = {
    // username & passwordかapi tokenのどちらかでアクセス
    if (task.getUsername.isPresent && task.getPassword.isPresent) return
    else if (task.getToken.isPresent) return
    // パラメータが不足している場合は処理停止
    throw new ConfigException("username and password or token must be provided")
  }

  def configure(task: PluginTask): KintoneClientBuilder = {
    val c = KintoneClientBuilder.create(task.getDomain)
    if (task.getUsername.isPresent && task.getPassword.isPresent) {
      c.authByPassword(task.getUsername.get, task.getPassword.get)
    }
    if (task.getToken.isPresent) {
      c.authByApiToken(task.getToken.get)
    }
    if (task.getBasicAuthUsername.isPresent && task.getBasicAuthPassword.isPresent) {
      c.withBasicAuth(task.getBasicAuthUsername.get, task.getBasicAuthPassword.get)
    }
    if (task.getGuestSpaceId.isPresent) {
      c.setGuestSpaceId(task.getGuestSpaceId.get)
    }
    c
  }

  def client(clientBuilder: KintoneClientBuilder): KintoneClient = clientBuilder.build()
}
