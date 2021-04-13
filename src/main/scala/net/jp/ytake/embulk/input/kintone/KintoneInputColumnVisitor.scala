package net.jp.ytake.embulk.input.kintone

import com.google.gson.{JsonParser => GsonParser}

import java.time.Instant
import java.lang.{Boolean, Double, Long}
import org.embulk.spi.{Column, ColumnVisitor, PageBuilder}
import org.msgpack.value.ValueFactory

class KintoneInputColumnVisitor(
                                 private val accessor: Accessor,
                                 private val pageBuilder: PageBuilder,
                                 private val pluginTask: PluginTask
                               )
  extends ColumnVisitor {

  private def defaultFormat = "%Y-%m-%dT%H:%M:%S%z"

  override def booleanColumn(column: Column): Unit = {
    pageBuilder.setBoolean(column, Boolean.parseBoolean(accessor.get(column.getName)))
  }

  override def longColumn(column: Column): Unit = {
    pageBuilder.setLong(column, Long.parseLong(accessor.get(column.getName)))
  }

  override def doubleColumn(column: Column): Unit = {
    pageBuilder.setDouble(column, Double.parseDouble(accessor.get(column.getName)))
  }

  override def stringColumn(column: Column): Unit = {
    val data = accessor.get(column.getName)
    if (isNull(data)) pageBuilder.setNull(column)
    else pageBuilder.setString(column, data)
    pageBuilder.setString(column, data)
  }

  override def timestampColumn(column: Column): Unit = {
    var pattern = this.defaultFormat
    pluginTask.getFields.getColumns.forEach(r => {
      if (r.getName.equals(column.getName)
        && r.getConfigSource != null
        && r.getConfigSource.getNestedOrGetEmpty("format") != null
        && !r.getConfigSource.getNestedOrGetEmpty("format").isEmpty) {
        pattern = r.getConfigSource.getNestedOrGetEmpty("format").toString
        return
      }
    })
    pageBuilder.setTimestamp(
      column,
      Instant.parse(accessor.get(column.getName))
    )
    //       TimestampParser
    //        .of(pattern, "UTC")
    //        .parse(accessor.get(column.getName))
  }

  override def jsonColumn(column: Column): Unit = {
    val v = GsonParser.parseString(accessor.get(column.getName))
    if (v.isJsonNull || v.isJsonPrimitive) pageBuilder.setNull(column)
    else pageBuilder.setJson(column, ValueFactory.newString(v.toString))
  }

  private def isNull(value: Any): scala.Boolean = Option(value) match {
    case Some(_) => true
    case None => false
  }
}
