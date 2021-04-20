package net.jp.ytake.embulk.input.kintone

import com.google.gson.{JsonParser => GsonParser}
import scala.util.control.Exception._
import java.time.{LocalDate, LocalDateTime, LocalTime, ZoneId, ZonedDateTime}
import java.lang.{Boolean, Double, Long}
import org.embulk.spi.{Column, ColumnVisitor, PageBuilder}
import org.msgpack.value.ValueFactory

import java.time.format.{DateTimeFormatter, DateTimeParseException}

class KintoneInputColumnVisitor(
                                 private val accessor: Accessor,
                                 private val pageBuilder: PageBuilder,
                                 private val pluginTask: PluginTask
                               )
  extends ColumnVisitor {

  private val datetimeFormatPattern = "yyyy-MM-dd'T'HH:mm'Z'"
  private val dateFormatPattern = "yyyy-MM-dd"

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
    val value = accessor.get(column.getName)
    if (value == "") {
      pageBuilder.setNull(column)
      return
    }
    val toInstant = failAsValue(classOf[DateTimeParseException])(
      ZonedDateTime.of(
        LocalDateTime.of(detectLocalDate(value, this.dateFormatPattern), LocalTime.MIN),
        ZoneId.of("UTC")
      ).toInstant
    )
    val instant = toInstant(
      ZonedDateTime.of(
        detectLocalDateTime(value, this.datetimeFormatPattern),
        ZoneId.of("UTC"))
        .toInstant
    )
    pageBuilder.setTimestamp(column, instant)
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

  private def detectLocalDateTime(datetime: CharSequence, pattern: String): LocalDateTime = {
    LocalDateTime.parse(
      datetime,
      DateTimeFormatter.ofPattern(pattern)
    )
  }

  private def detectLocalDate(date: CharSequence, pattern: String): LocalDate = {
    LocalDate.parse(
      date,
      DateTimeFormatter.ofPattern(pattern)
    )
  }
}
