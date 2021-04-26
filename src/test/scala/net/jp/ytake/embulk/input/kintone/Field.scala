package net.jp.ytake.embulk.input.kintone

import com.cybozu.kintone.client.model.record.field.FieldValue

import java.util

class Field {
  private val rec = new util.HashMap[String, FieldValue]

  def add(code: String, value: FieldValue): Field = {
    this.rec.put(code, value)
    this
  }

  def getRecords: util.HashMap[String, FieldValue] = rec
}
