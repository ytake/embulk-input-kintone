package net.jp.ytake.embulk.input.kintone

import org.embulk.EmbulkTestRuntime
import org.embulk.spi.TestPageBuilderReader.MockPageOutput
import org.embulk.spi.`type`.Types
import org.embulk.util.config.ConfigMapperFactory
import org.embulk.util.config.modules.TypeModule
import org.embulk.util.config.units.{ColumnConfig, SchemaConfig}
import org.junit.Rule
import org.junit.Test
import org.embulk.config.{ConfigException, TaskReport, TaskSource}
import org.embulk.spi.{InputPlugin, Schema}

import java.util

class TestKintoneInputPlugin {

  val plugin = new KintoneInputPlugin
  val output = new MockPageOutput

  @Rule
  def runtime: EmbulkTestRuntime = new EmbulkTestRuntime()

  private def configFactory = ConfigMapperFactory
    .builder
    .addDefaultModules()
    .addModule(new TypeModule).build

  @Test(expected = classOf[ConfigException])
  def testShouldThrowConfigExceptionInvalidUsernameAndPassword(): Unit = {
    val cc = new ColumnConfig("column", Types.JSON, "")
    val list = new util.ArrayList[ColumnConfig]
    list.add(cc)
    val config = configFactory.newConfigSource
      .set("domain", "example.cybozu.com")
      .set("app_id", 1234)
      .set("fields", new SchemaConfig(list))
    plugin.transaction(config, new Control)
  }

  @Test(expected = classOf[ConfigException])
  def testShouldNotThrowConfigException(): Unit = {
    val cc = new ColumnConfig("column", Types.JSON, "")
    val list = new util.ArrayList[ColumnConfig]
    list.add(cc)
    val config = configFactory.newConfigSource
      .set("domain", "https://example.cybozu.com")
      .set("app_id", 1234)
      .set("fields", new SchemaConfig(list))
    plugin.transaction(config, new Control)
  }

  private class Control extends InputPlugin.Control {
    override def run(taskSource: TaskSource, schema: Schema, taskCount: Int): util.List[TaskReport] = {
      val reports = new util.ArrayList[TaskReport]
      for (i <- 0 until taskCount) {
        reports.add(plugin.run(taskSource, schema, i, output))
      }
      reports
    }
  }
}
