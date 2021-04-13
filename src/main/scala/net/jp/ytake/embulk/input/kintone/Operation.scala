package net.jp.ytake.embulk.input.kintone

import com.kintone.client.KintoneClient
import com.kintone.client.api.record.{CreateCursorRequest, CreateCursorResponseBody, GetRecordsByCursorResponseBody}
import java.util

class Operation(private val c: KintoneClient) {

  private val FETCH_SIZE = 500

  /**
   * cursor作成
   */
  def makeCursor(task: PluginTask): CreateCursorResponseBody = {
    val fields = new util.ArrayList[String]
    task.getFields.getColumns.forEach(c => fields.add(c.getName))
    val request = new CreateCursorRequest
    c.record.createCursor(request.setApp(task.getAppId)
      .setFields(fields)
      .setQuery(task.getQuery.orElse(""))
      .setSize(FETCH_SIZE)
    )
  }

  def retrieveResponseByCursor(res: CreateCursorResponseBody): GetRecordsByCursorResponseBody = c.record.getRecordsByCursor(res.getId)

  def deleteCursor(res: CreateCursorResponseBody): Unit = c.record.deleteCursor(res.getId)
}
