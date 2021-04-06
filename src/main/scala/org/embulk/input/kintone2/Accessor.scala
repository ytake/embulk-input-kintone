package org.embulk.input.kintone2

import com.google.gson.Gson
import com.kintone.client.model.record.{FieldType, FieldValue, Record}
import scala.jdk.CollectionConverters._
import java.util
import java.lang.reflect.Method

class Accessor(records: Record) {

  private val gson = new Gson
  private val delimiter = "\n"

  def get(name: String): String = {

    this.records.getFieldType(name) match {
      case FieldType.USER_SELECT => this.records.getUserSelectFieldValue(name)
        .asScala
        .map(row => row.getCode)
        .reduce(_ + this.delimiter + _)
      case FieldType.ORGANIZATION_SELECT => this.records.getOrganizationSelectFieldValue(name)
        .asScala
        .map(row => row.getCode)
        .reduce(_ + this.delimiter + _)
      case FieldType.GROUP_SELECT => this.records.getGroupSelectFieldValue(name)
        .asScala
        .map(row => row.getCode)
        .reduce(_ + this.delimiter + _)
      case FieldType.STATUS_ASSIGNEE => this.records.getStatusAssigneeFieldValue
        .asScala
        .map(row => row.getCode)
        .reduce(_ + this.delimiter + _)
      case FieldType.SUBTABLE => gson.toJson(this.records.getSubtableFieldValue(name))
      case FieldType.CREATOR => this.records.getCreatorFieldValue.getCode
      case FieldType.MODIFIER => this.records.getModifierFieldValue.getCode
      // check box向け
      case FieldType.CHECK_BOX | FieldType.MULTI_SELECT | FieldType.CATEGORY =>
        val value = this.records.getFieldValue(name)
        getReduceValue(value, getValuesMethod(value))
      //
      case FieldType.FILE => this.records.getFileFieldValue(name)
        .asScala
        .map(row => row.getFileKey)
        .reduceLeft(_ + this.delimiter + _)
      case _ =>
        val value = this.records.getFieldValue(name)
        getSimpleValue(value, getValueMethod(value))
    }
  }

  private def getSimpleValue(value: FieldValue, m: Method): String = m.invoke(value).toString

  private def getReduceValue(value: FieldValue, m: Method): String = {
    m.invoke(value)
      .asInstanceOf[util.List[util.List[String]]]
      .toArray
      .reduceLeft(_ + this.delimiter + _)
      .toString
  }

  private def getValueMethod(value: FieldValue): Method = value.getClass.getMethod("getValue")

  private def getValuesMethod(value: FieldValue): Method = value.getClass.getMethod("getValues")
}
