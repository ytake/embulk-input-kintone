package org.embulk.input.kintone2

import com.kintone.client.KintoneClient
import com.kintone.client.api.record.{CreateCursorRequest, CreateCursorResponseBody, GetRecordRequest, GetRecordsByCursorRequest, GetRecordsByCursorResponseBody, GetRecordsRequest}
import java.util

/**
 * Kintoneから取得した全レコードに対して操作
 * @param c
 */
class Operation(private val c: KintoneClient) {
  private val FETCH_SIZE = 500

  /**
   * cursor作成
   * @param task
   * @return
   */
  def makeCursor(task: PluginTask): CreateCursorResponseBody = {
    val fields = new util.ArrayList[String]
    task.getFields.getColumns.forEach(c => fields.add(c.getName))
    val request = new CreateCursorRequest
    c.record.createCursor(request.setApp(task.getAppId)
      .setFields(fields)
      .setQuery(task.getQuery.getOrElse(""))
      .setSize(FETCH_SIZE)
    )
  }

  def retrieveResponseByCursor(res: CreateCursorResponseBody): GetRecordsByCursorResponseBody = {
    c.record.getRecordsByCursor(res.getId)
  }

  def deleteCursor(res: CreateCursorResponseBody): Unit = {
    c.record.deleteCursor(res.getId)
  }
}
