package org.embulk.input.kintone2.client

import com.kintone.client.KintoneClient
import com.kintone.client.KintoneClientBuilder
import org.embulk.config.ConfigException
import org.embulk.input.kintone2.PluginTask

object Kintone {

  private val client = KintoneClientBuilder

  @throws[ConfigException]
  def validateAuth(task: PluginTask): Unit = {
    // username & passwordかapi tokenのどちらかでアクセス
    if (task.getUsername.nonEmpty && task.getPassword.nonEmpty) return
    else if (task.getToken.nonEmpty) return
    // パラメータが不足している場合は処理停止
    throw new ConfigException("Username and password or token must be provided")
  }

  def configure(task: PluginTask): KintoneClientBuilder = {
    val c = client.create(task.getDomain)
    if (task.getUsername.nonEmpty && task.getPassword.nonEmpty) {
      c.authByPassword(task.getUsername.get, task.getPassword.get)
    }
    if (task.getToken.nonEmpty) {
      c.authByApiToken(task.getToken.get)
    }
    if (task.getBasicAuthUsername.nonEmpty && task.getBasicAuthPassword.nonEmpty) {
      c.withBasicAuth(task.getBasicAuthUsername.get, task.getBasicAuthPassword.get)
    }
    if (task.getGuestSpaceId.nonEmpty) {
      c.setGuestSpaceId(task.getGuestSpaceId.get)
    }
    c
  }

  def client(clientBuilder: KintoneClientBuilder): KintoneClient = clientBuilder.build()
}
