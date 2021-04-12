package org.embulk.input.kintone2.client

import org.embulk.config.{ConfigLoader, Task}
import org.junit.Test
import org.scalatestplus.junit.AssertionsForJUnit
import org.embulk.input.kintone2.PluginTask
import org.embulk.spi.{Exec, ExecSession}
import org.embulk.test.TestingEmbulk
import org.embulk.util.config.{Compat, ConfigMapperFactory}
import org.embulk.test.EmbulkTests

final class TestKintone {

  private def configFactory = ConfigMapperFactory.builder().addDefaultModules().build();

  @Test
  def testShouldBe(): Unit = {
    val config = configFactory.newConfigSource()
      .set("type", "kintone2")
      .set("app_id", 1234)
      .set("username", "ytake")
      .set("a", "aaa")
    val tasks = configFactory.createConfigMapper
      .map(config, classOf[PluginTask])
    println(tasks, config)

  }
}
