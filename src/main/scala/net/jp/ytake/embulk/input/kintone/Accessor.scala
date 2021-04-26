package net.jp.ytake.embulk.input.kintone

import com.google.gson.Gson
import com.kintone.client.model.{FileBody, Group, Organization, User}
import com.kintone.client.model.record.{FieldType, FieldValue, Record}

import scala.jdk.CollectionConverters._
import java.util
import java.lang.reflect.Method
import scala.collection.mutable

class Accessor(record: Record) {

  private val gson = new Gson
  private val delimiter = "\n"

  def get(name: String): String = {
    if (name == "$id") {
      return this.record.getId.toString
    }
    if (name == "$revision") {
      return this.record.getRevision.toString
    }
    this.record.getFieldType(name) match {
      case FieldType.USER_SELECT =>
        val buf = this.record.getUserSelectFieldValue(name).asScala
        if (buf.nonEmpty) {
          return this.userBufferToReduce(buf)
        }
        ""
      case FieldType.ORGANIZATION_SELECT =>
        val buf = this.record.getOrganizationSelectFieldValue(name).asScala
        if (buf.nonEmpty) {
          return this.organizationBufferToReduce(buf)
        }
        ""
      case FieldType.GROUP_SELECT =>
        val buf = this.record.getGroupSelectFieldValue(name).asScala
        if (buf.nonEmpty) {
          return this.groupBufferToReduce(buf)
        }
        ""
      case FieldType.STATUS_ASSIGNEE =>
        val buf = this.record.getStatusAssigneeFieldValue.asScala
        if (buf.nonEmpty) {
          return this.userBufferToReduce(buf)
        }
        ""
      case FieldType.SUBTABLE => gson.toJson(this.record.getSubtableFieldValue(name))
      case FieldType.CREATOR => this.record.getCreatorFieldValue.getCode
      case FieldType.MODIFIER => this.record.getModifierFieldValue.getCode
      //
      case FieldType.CHECK_BOX | FieldType.MULTI_SELECT | FieldType.CATEGORY =>
        val value = this.record.getFieldValue(name)
        getReduceValue(value, getValuesMethod(value))
      //
      case FieldType.FILE =>
        val buf = this.record.getFileFieldValue(name).asScala
        if (buf.nonEmpty) {
          return buf.map(row => row.getFileKey)
            .reduceLeft(_ + this.delimiter + _)
        }
        ""
      case _ =>
        val value = this.record.getFieldValue(name)
        getSimpleValue(value, getValueMethod(value))
    }
  }

  private def getSimpleValue(value: FieldValue, m: Method): String = {
    val v = m.invoke(value)
    if (v != null) {
      return v.toString
    }
    ""
  }

  private def getReduceValue(value: FieldValue, m: Method): String = {
    val values = m.invoke(value).asInstanceOf[util.List[util.List[String]]]
    if (values.size() != 0) {
      return values.toArray
        .reduceLeft(_.toString + this.delimiter + _.toString)
        .toString
    }
    ""
  }

  private def getValueMethod(value: FieldValue): Method = value.getClass.getMethod("getValue")

  private def getValuesMethod(value: FieldValue): Method = value.getClass.getMethod("getValues")

  private def userBufferToReduce(buf: mutable.Buffer[User]): String = {
    buf.map(row => row.getCode).reduce(_ + this.delimiter + _)
  }

  private def groupBufferToReduce(buf: mutable.Buffer[Group]): String = {
    buf.map(row => row.getCode).reduce(_ + this.delimiter + _)
  }

  private def organizationBufferToReduce(buf: mutable.Buffer[Organization]): String = {
    buf.map(row => row.getCode).reduce(_ + this.delimiter + _)
  }
}
