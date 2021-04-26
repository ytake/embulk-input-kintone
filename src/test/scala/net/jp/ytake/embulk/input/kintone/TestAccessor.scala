package net.jp.ytake.embulk.input.kintone

import com.cybozu.kintone.client.model.app.form.FieldType
import com.cybozu.kintone.client.model.file.FileModel
import com.cybozu.kintone.client.model.record.SubTableValueItem
import com.cybozu.kintone.client.model.record.field.FieldValue
import com.cybozu.kintone.client.module.file.File
import org.junit.Assert._
import org.junit.Test

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalTime, ZonedDateTime}
import java.util

final class TestAccessor {

  private val uniqueKey = 1
  private val subtableValue = "{\"row1\":{\"type\":\"SINGLE_LINE_TEXT\",\"value\":\"single line\"},\"multi1\":{\"type\":\"MULTI_SELECT\",\"value\":[\"sample1\",\"sample2\"]}}"
  private val f = new Field

  @Test
  def shouldBeReturnedExpectedValues(): Unit = {
    // make table
    val table = new FieldValue
    val tv = new SubTableValueItem
    val rows = new util.HashMap[String, FieldValue]()
    val tableRow = new FieldValue
    tableRow.setType(FieldType.SINGLE_LINE_TEXT)
    tableRow.setValue("single line")
    val tableRowTwo = new FieldValue
    tableRowTwo.setType(FieldType.MULTI_SELECT)
    val multiSelectRow = new util.ArrayList[String]()
    multiSelectRow.add("sample1")
    multiSelectRow.add("sample2")
    tableRowTwo.setValue(multiSelectRow)
    tv.setID(1)
    tv.setValue(rows)
    rows.put("row1", tableRow)
    rows.put("multi1", tableRowTwo)
    table.setType(FieldType.SUBTABLE)
    table.setValue(rows)

    val fb = new FileModel
    fb.setName("file1")
    fb.setSize("0")
    fb.setFileKey("123ecfrwqefknaflwen")
    val fb2 = new FileModel
    fb2.setName("file1")
    fb2.setSize("0")
    fb2.setFileKey("123ecfrwqefknaflwen")
    val fl = new util.ArrayList[FileModel]()
    fl.add(fb)
    fl.add(fb2)
    val fv = new FieldValue
    fv.setValue(fl)
    fv.setType(FieldType.FILE)

    val singleLine = new FieldValue
    singleLine.setType(FieldType.SINGLE_LINE_TEXT)
    singleLine.setValue("test single line")

    val numberField = new FieldValue
    numberField.setType(FieldType.NUMBER)
    numberField.setValue(uniqueKey)

    val multiLine = new FieldValue
    multiLine.setType(FieldType.MULTI_LINE_TEXT)
    multiLine.setValue("test multi text")

    val richText = new FieldValue
    richText.setValue("<div>test rich text<br /></div>")
    richText.setType(FieldType.RICH_TEXT)

    val checkBox = new FieldValue
    val checkList = new util.ArrayList[String]()
    checkList.add("sample1")
    checkList.add("sample2")
    checkBox.setValue(checkList)
    checkBox.setType(FieldType.CHECK_BOX)
    f
      .add("文字列__1行", singleLine)
      .add("数値", numberField)
      .add("文字列__複数行", multiLine)
      .add("リッチエディター", richText)
      .add("チェックボックス", checkBox)
      .add("table", table)
      .add("file1", fv)
      /*
      .add("ラジオボタン", new RadioButtonFieldValue("radio1"))
      .add("ドロップダウン", new DropDownFieldValue("dropdown1"))
      .add("複数選択", new MultiSelectFieldValue(Seq("sample1", "sample2"): _*))
      .add("リンク", new LinkFieldValue("https://github.com/"))
      .add("日付", new DateFieldValue(toLocalDate("2021-01-01")))
      .add("時刻", new TimeFieldValue(toLocalTime("12:34")))
      .add("日時", new DateTimeFieldValue(toZonedDatetime("2021-01-02T03:40Z")))
      .add("ユーザー選択", new UserSelectFieldValue(Seq(new User("user1"), new User("user2")): _*))
      .add("グループ選択", new GroupSelectFieldValue(Seq(new Group("group1"), new Group("group2")): _*))
      .add("組織選択", new OrganizationSelectFieldValue(Seq(new Organization("org1"), new Organization("org2")): _*))
      .add("creator", new CreatorFieldValue(new User("user1")))
      .add("modifier", new ModifierFieldValue(new User("user1")))
      */


    val accessor = new Accessor(f.getRecords)
    assertEquals("test single line", accessor.get("文字列__1行"))
    assertEquals("1", accessor.get("数値"))
    assertEquals("test multi text", accessor.get("文字列__複数行"))
    assertEquals("<div>test rich text<br /></div>", accessor.get("リッチエディター"))
    assertEquals("sample1\nsample2", accessor.get("チェックボックス"))
    assertEquals(this.subtableValue, accessor.get("table"))
    assertEquals("123ecfrwqefknaflwen\n123ecfrwqefknaflwen", accessor.get("file1"))
    /*
    assertEquals("radio1", accessor.get("ラジオボタン"))
    assertEquals("dropdown1", accessor.get("ドロップダウン"))
    assertEquals("sample1\nsample2", accessor.get("複数選択"))
    assertEquals("https://github.com/", accessor.get("リンク"))
    assertEquals("2021-01-01", accessor.get("日付"))
    assertEquals("12:34", accessor.get("時刻"))
    assertEquals("2021-01-02T03:40Z", accessor.get("日時"))
    assertEquals("user1\nuser2", accessor.get("ユーザー選択"))
    assertEquals("group1\ngroup2", accessor.get("グループ選択"))
    assertEquals("org1\norg2", accessor.get("組織選択"))
    assertEquals("user1", accessor.get("creator"))
    assertEquals("user1", accessor.get("modifier"))
    */
  }

  private def toLocalDate(ds: String): LocalDate = LocalDate.parse(ds, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  private def toLocalTime(ds: String): LocalTime = LocalTime.parse(ds, DateTimeFormatter.ofPattern("HH:mm"))

  private def toZonedDatetime(ds: String): ZonedDateTime = ZonedDateTime.parse(ds)
}
