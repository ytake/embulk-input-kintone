package net.jp.ytake.embulk.input.kintone

import com.cybozu.kintone.client.model.cursor.{CreateRecordCursorResponse, GetRecordCursorResponse}
import com.cybozu.kintone.client.module.recordCursor.RecordCursor
import java.util

class Operation(private val c: RecordCursor) {

  private val fetchSize = 500

  /**
   * cursor
   */
  def makeCursor(task: PluginTask): CreateRecordCursorResponse = {
    val fields = new util.ArrayList[String]
    if (task.getFields.isPresent) {
      task.getFields.get().getColumns.forEach(c => fields.add(c.getName))
    }
    c.createCursor(
      task.getAppId,
      fields,
      task.getQuery.orElse(""),
      fetchSize
    )
  }

  def retrieveResponseByCursor(res: CreateRecordCursorResponse): GetRecordCursorResponse = c.getRecords(res.getId)

  def deleteCursor(res: CreateRecordCursorResponse): Unit = c.deleteCursor(res.getId)
}
