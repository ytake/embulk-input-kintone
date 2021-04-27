package net.jp.ytake.embulk.input.kintone

import com.cybozu.kintone.client.model.app.form.FieldType
import com.cybozu.kintone.client.model.file.FileModel
import com.cybozu.kintone.client.model.member.Member
import com.cybozu.kintone.client.model.record.field.FieldValue
import com.google.gson.Gson
import scala.jdk.CollectionConverters._
import java.util
import scala.collection.mutable

class Accessor(record: util.HashMap[String, FieldValue]) {

  private val gson = new Gson
  private val delimiter = "\n"

  def get(name: String): String = {
    this.record.get(name).getType match {
      case FieldType.USER_SELECT | FieldType.ORGANIZATION_SELECT | FieldType.GROUP_SELECT | FieldType.STATUS_ASSIGNEE =>
        val buf = this.record.get(name).getValue.asInstanceOf[util.ArrayList[Member]].asScala
        if (buf.nonEmpty) {
          return this.userBufferToReduce(buf)
        }
        ""
      case FieldType.SUBTABLE => gson.toJson(this.record.get(name).getValue)
      case FieldType.CREATOR | FieldType.MODIFIER  => this.record.get(name).getValue.asInstanceOf[Member].getCode
      //
      case FieldType.CHECK_BOX | FieldType.MULTI_SELECT | FieldType.CATEGORY =>
        val value = this.record.get(name).getValue.asInstanceOf[util.ArrayList[String]].asScala
        if(value.nonEmpty) {
          return value.reduce(_ + this.delimiter + _)
        }
        ""
      //
      case FieldType.FILE =>
        val buf = this.record.get(name).getValue.asInstanceOf[util.ArrayList[FileModel]].asScala
        if (buf.nonEmpty) {
          return buf.map(row => row.getFileKey)
            .reduceLeft(_ + this.delimiter + _)
        }
        ""
      case _ => this.record.get(name).getValue.toString
    }
  }

  private def userBufferToReduce(buf: mutable.Buffer[Member]): String = {
    buf.map(row => row.getCode)
      .reduce(_ + this.delimiter + _)
  }
}
