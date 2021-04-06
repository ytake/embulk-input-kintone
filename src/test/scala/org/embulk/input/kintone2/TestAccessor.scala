package org.embulk.input.kintone2

import com.kintone.client.model.{Group, Organization, User}
import com.kintone.client.model.record.{CheckBoxFieldValue, CreatorFieldValue, DateFieldValue, DateTimeFieldValue, DropDownFieldValue, FieldType, GroupSelectFieldValue, LinkFieldValue, ModifierFieldValue, MultiLineTextFieldValue, MultiSelectFieldValue, NumberFieldValue, OrganizationSelectFieldValue, RadioButtonFieldValue, RichTextFieldValue, SingleLineTextFieldValue, SubtableFieldValue, TableRow, TimeFieldValue, UserSelectFieldValue}
import org.junit.Assert._
import org.junit.Test
import org.scalatestplus.junit.AssertionsForJUnit

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalTime, ZonedDateTime}

final class TestAccessor extends AssertionsForJUnit {

  private val uniqueKey = 1
  private val subtableValue = "[{\"fields\":{\"row1\":{\"value\":\"single line\"},\"multi1\":{\"values\":[\"sample1\",\"sample2\"]}},\"id\":1}]"
  private val f = new Field

  @Test
  def shouldBeReturnedExpectedValues(): Unit = {
    // make table
    val table = new TableRow(1)
    table
      .putField("row1", new SingleLineTextFieldValue("single line"))
      .putField("multi1", new MultiSelectFieldValue(Seq("sample1", "sample2"): _*))
    f
      .add("文字列__1行", new SingleLineTextFieldValue("test single line"))
      .add("数値", new NumberFieldValue(uniqueKey))
      .add("文字列__複数行", new MultiLineTextFieldValue("test multi text"))
      .add("リッチエディター", new RichTextFieldValue("<div>test rich text<br /></div>"))
      .add("チェックボックス", new CheckBoxFieldValue(Seq("sample1", "sample2"): _*))
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
      .add("table", new SubtableFieldValue(Seq(table): _*))
      .add("creator", new CreatorFieldValue(new User("user1")))
      .add("modifier", new ModifierFieldValue(new User("user1")))

    val accessor = new Accessor(f.getRecords)
    assertEquals("test single line", accessor.get("文字列__1行"))
    assertEquals("1", accessor.get("数値"))
    assertEquals("test multi text", accessor.get("文字列__複数行"))
    assertEquals("<div>test rich text<br /></div>", accessor.get("リッチエディター"))
    assertEquals("sample1\nsample2", accessor.get("チェックボックス"))
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
    assertEquals(this.subtableValue, accessor.get("table"))
    assertEquals("user1", accessor.get("creator"))
    assertEquals("user1", accessor.get("modifier"))
  }

  private def toLocalDate(ds: String): LocalDate = LocalDate.parse(ds, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  private def toLocalTime(ds: String): LocalTime = LocalTime.parse(ds, DateTimeFormatter.ofPattern("HH:mm"))

  private def toZonedDatetime(ds: String): ZonedDateTime = ZonedDateTime.parse(ds)
}
