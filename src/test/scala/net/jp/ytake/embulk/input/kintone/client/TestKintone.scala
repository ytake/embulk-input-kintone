package net.jp.ytake.embulk.input.kintone.client

import net.jp.ytake.embulk.input.kintone.PluginTask
import org.junit.Assert._
import org.junit.Test
import org.embulk.util.config.ConfigMapperFactory
import org.embulk.EmbulkTestRuntime
import org.embulk.config.ConfigException
import org.embulk.util.config.units.{ColumnConfig, SchemaConfig}
import org.junit.Rule
import org.embulk.spi.`type`.Types
import org.embulk.util.config.modules.TypeModule

import java.util

final class TestKintone {

  private def configFactory = ConfigMapperFactory
    .builder
    .addDefaultModules()
    .addModule(new TypeModule).build

  @Rule
  def runtime: EmbulkTestRuntime = new EmbulkTestRuntime()

  @Test
  def testShouldReturnExpectedValue(): Unit = {
    val cc = new ColumnConfig(
      "column",
      Types.JSON,
      ""
    )
    val list = new util.ArrayList[ColumnConfig]
    list.add(cc)
    val config = runtime.getExec.newConfigSource
      .set("domain", "example.cybozu.com")
      .set("app_id", 1234)
      .set("fields", new SchemaConfig(list))
    val tasks = configFactory
      .createConfigMapper
      .map(config, classOf[PluginTask])
    assertFalse(tasks.getUsername.isPresent)
    assertEquals(tasks.getAppId, 1234)
    assertEquals(tasks.getDomain, "example.cybozu.com")
    assertFalse(tasks.getMapping.isPresent)
  }

  @Test(expected = classOf[ConfigException])
  def shouldBe(): Unit = {
    val cc = new ColumnConfig(
      "column",
      Types.JSON,
      ""
    )
    val list = new util.ArrayList[ColumnConfig]
    list.add(cc)
    val config = runtime.getExec.newConfigSource
      .set("domain", "example.cybozu.com")
      .set("app_id", 1234)
      .set("fields", new SchemaConfig(list))
    Kintone.validateAuth(
      configFactory
        .createConfigMapper
        .map(config, classOf[PluginTask])
    )
  }
}
