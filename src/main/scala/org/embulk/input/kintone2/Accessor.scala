package org.embulk.input.kintone2

import com.google.gson.Gson
import com.kintone.client.model.{FileBody, User}
import org.embulk.spi.Exec
import com.kintone.client.model.record.{FieldType, FieldValue, Record}

import scala.jdk.CollectionConverters._
import java.util

class Accessor(records: Record) {

  private val logger = Exec.getLogger(this.getClass)
  private val gson = new Gson
  private val delimiter = "\n"

  def get(name: String): String = {

    this.records.getFieldType(name) match {
      case FieldType.USER_SELECT | FieldType.ORGANIZATION_SELECT | FieldType.GROUP_SELECT | FieldType.STATUS_ASSIGNEE =>
        val members = this.records.getFieldValue(name).asInstanceOf[util.ArrayList[User]]
        members
          .asScala
          .map(row => row.getCode)
          .reduce((index, value) => index + this.delimiter + value)
      case FieldType.SUBTABLE =>
        val subTableValueItem = this.records.getSubtableFieldValue(name)
        gson.toJson(subTableValueItem)
      case FieldType.CREATOR | FieldType.MODIFIER =>
        val m = this.records.getFieldValue(name).asInstanceOf[User]
        m.getCode
      case FieldType.CHECK_BOX | FieldType.MULTI_SELECT | FieldType.CATEGORY =>
        val selectedItemList = this.records.getFieldValue(name).asInstanceOf[util.ArrayList[String]]
        selectedItemList
          .stream
          .reduce((accum: String, value: String) => accum + this.delimiter + value)
          .orElse("")
      case FieldType.FILE =>
        val cbFileList = this.records.getFieldValue(name).asInstanceOf[util.ArrayList[FileBody]]
        cbFileList
          .asScala
          .map(row => row.getFileKey).reduce((accum: Any, value: Any) => accum + this.delimiter + value)
      case FieldType.NUMBER => String.valueOf(this.records.getFieldValue(name))
      case _ =>
        this.records.getFieldValue(name).asInstanceOf[String]
    }
  }
}
