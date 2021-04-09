package org.embulk.input.kintone2

import com.google.common.annotations.VisibleForTesting
import org.embulk.input.kintone2.client.Kintone
// import scala.collection.JavaConversions._
import org.embulk.config.{ConfigDiff, ConfigSource, TaskReport, TaskSource}
import org.embulk.spi.{Exec, InputPlugin, PageOutput, Schema}
import org.embulk.spi.PageBuilder
import scala.util.control.Breaks
import java.util

/**
 * for Kintone Input Plugin
 */
abstract class Kintone2InputPlugin extends InputPlugin {

  private val logger = Exec.getLogger(this.getClass)

  override def transaction(config: ConfigSource, control: InputPlugin.Control): ConfigDiff = {
    val task = config.loadConfig(classOf[PluginTask])
    val schema = task.getFields.toSchema
    val taskCount = 1
    resume(task.dump, schema, taskCount, control)
  }

  override def resume(taskSource: TaskSource, schema: Schema, taskCount: Int, control: InputPlugin.Control): ConfigDiff = {
    control.run(taskSource, schema, taskCount)
    Exec.newConfigDiff
  }

  override def cleanup(taskSource: TaskSource, schema: Schema, taskCount: Int, successTaskReports: util.List[TaskReport]): Unit

  /**
   *
   * @param taskSource
   * @param schema
   * @param taskIndex
   * @param output
   * @return
   */
  override def run(taskSource: TaskSource, schema: Schema, taskIndex: Int, output: PageOutput): TaskReport = {
    val task = taskSource.loadTask(classOf[PluginTask])
    try {
      val pageBuilder = getPageBuilder(schema, output)
      try {
        // 設定が正しいか
        Kintone.validateAuth(task)
        val client = Kintone.client(Kintone.configure(task))
        val cursor = new Operation(client)
        // cursorを使ってリクエスト送信
        var cursorResponse = cursor.makeCursor(task)

        val b = new Breaks
        b.breakable {
          while (true) {
            val response = cursor.retrieveResponseByCursor(cursorResponse)
            response.getRecords.forEach(row => {
              new Accessor(row)
              pageBuilder.flush()
            })
            pageBuilder.flush()
            if (response.hasNext) {
              b.break
            }
          }
        }
        pageBuilder.finish()
      } finally if (pageBuilder != null) pageBuilder.close()
    }
    catch {
      case e: Exception =>
        logger.error(e.getMessage)
        throw e
    }
    Exec.newTaskReport
  }

  override def guess(config: ConfigSource): ConfigDiff = Exec.newConfigDiff

  @VisibleForTesting protected def getPageBuilder(schema: Schema, output: PageOutput) = new PageBuilder(
    Exec.getBufferAllocator,
    schema,
    output
  )
}
