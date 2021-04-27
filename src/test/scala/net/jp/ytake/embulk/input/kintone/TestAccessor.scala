package net.jp.ytake.embulk.input.kintone

import com.cybozu.kintone.client.model.app.form.FieldType
import com.cybozu.kintone.client.model.file.FileModel
import com.cybozu.kintone.client.model.member.Member
import com.cybozu.kintone.client.model.record.SubTableValueItem
import com.cybozu.kintone.client.model.record.field.FieldValue
import scala.jdk.CollectionConverters._
import org.junit.Assert._
import org.junit.Test

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalTime, ZonedDateTime}
import java.util

final class TestAccessor {

  private val uniqueKey: Int = 1
  private val subtableValue = "{\"row1\":{\"type\":\"SINGLE_LINE_TEXT\",\"value\":\"single line\"},\"multi1\":{\"type\":\"MULTI_SELECT\",\"value\":[\"sample1\",\"sample2\"]}}"
  private val f = new Field

  @Test
  def shouldBeReturnedExpectedValues(): Unit = {

    val numberField = new FieldValue
    numberField.setType(FieldType.NUMBER)
    numberField.setValue(uniqueKey)

    f
      .add("文字列__1行", this.makeFieldValue("test single line", FieldType.SINGLE_LINE_TEXT))
      .add("数値", numberField)
      .add("文字列__複数行", this.makeFieldValue("test multi text", FieldType.MULTI_LINE_TEXT))
      .add("リッチテキスト", this.makeFieldValue("<div>test rich text<br /></div>", FieldType.RICH_TEXT))
      .add("チェックボックス", this.makeCheckbox())
      .add("table", this.makeTable())
      .add("file1", this.makeFileList())
      .add("ラジオボタン", this.makeFieldValue("radio1", FieldType.RADIO_BUTTON))
      .add("ドロップダウン", this.makeFieldValue("dropdown1", FieldType.DROP_DOWN))
      .add("複数選択", this.makeMultiSelect())
      .add("リンク", this.makeFieldValue("https://github.com/", FieldType.LINK))
      .add("年月日", this.makeFieldValue("2021-01-01", FieldType.DATE))
      .add("時刻", this.makeFieldValue("12:34", FieldType.TIME))
      .add("日時", this.makeFieldValue("2021-01-02T03:40Z", FieldType.DATETIME))
      .add("ユーザー選択", this.makeUserSelect())
      .add("グループ選択", this.makeGroupSelect())
      .add("組織選択", this.makeOrganizationSelect())
      .add("creator", this.makeMember(new Member("code1", "user1"), FieldType.CREATOR))
      .add("modifier", this.makeMember(new Member("code1", "user1"), FieldType.MODIFIER))

    val accessor = new Accessor(f.getRecords)
    assertEquals("test single line", accessor.get("文字列__1行"))
    assertEquals("1", accessor.get("数値"))
    assertEquals("test multi text", accessor.get("文字列__複数行"))
    assertEquals("<div>test rich text<br /></div>", accessor.get("リッチテキスト"))
    assertEquals("sample1\nsample2", accessor.get("チェックボックス"))
    assertEquals(this.subtableValue, accessor.get("table"))
    assertEquals("123ecfrwqefknaflwen\n123ecfrwqefknaflwen", accessor.get("file1"))
    assertEquals("radio1", accessor.get("ラジオボタン"))
    assertEquals("dropdown1", accessor.get("ドロップダウン"))
    assertEquals("multi1\nsample2", accessor.get("複数選択"))
    assertEquals("https://github.com/", accessor.get("リンク"))
    assertEquals("2021-01-01", accessor.get("年月日"))
    assertEquals("12:34", accessor.get("時刻"))
    assertEquals("2021-01-02T03:40Z", accessor.get("日時"))
    assertEquals("code1\ncode2", accessor.get("ユーザー選択"))
    assertEquals("code1\ncode2", accessor.get("グループ選択"))
    assertEquals("code1\ncode2", accessor.get("組織選択"))
    assertEquals("code1", accessor.get("creator"))
    assertEquals("code1", accessor.get("modifier"))
  }

  private def makeTable(): FieldValue = {
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
    table
  }

  private def makeFileList(): FieldValue = {
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
    fv
  }

  private def makeCheckbox(): FieldValue = {
    val checkBox = new FieldValue
    val checkList = new util.ArrayList[String]()
    checkList.add("sample1")
    checkList.add("sample2")
    checkBox.setValue(checkList)
    checkBox.setType(FieldType.CHECK_BOX)
    checkBox
  }

  private def makeMultiSelect(): FieldValue = {
    val multi = new FieldValue
    val l = new util.ArrayList[String]()
    l.add("multi1")
    l.add("sample2")
    multi.setValue(l)
    multi.setType(FieldType.MULTI_SELECT)
    multi
  }

  private def makeUserSelect(): FieldValue = {
    val userSelect = new FieldValue
    val l = new util.ArrayList[Member]()
    l.add(new Member("code1", "user1"))
    l.add(new Member("code2", "user2"))
    userSelect.setValue(l)
    userSelect.setType(FieldType.USER_SELECT)
    userSelect
  }

  private def makeGroupSelect(): FieldValue = {
    val userSelect = new FieldValue
    val l = new util.ArrayList[Member]()
    l.add(new Member("code1", "group1"))
    l.add(new Member("code2", "group2"))
    userSelect.setValue(l)
    userSelect.setType(FieldType.GROUP_SELECT)
    userSelect
  }

  private def makeOrganizationSelect(): FieldValue = {
    val orgSelect = new FieldValue
    val l = new util.ArrayList[Member]()
    l.add(new Member("code1", "org1"))
    l.add(new Member("code2", "org2"))
    orgSelect.setValue(l)
    orgSelect.setType(FieldType.ORGANIZATION_SELECT)
    orgSelect
  }

  private def makeMember(m: Member, t: FieldType): FieldValue = {
    val fv = new FieldValue
    fv.setValue(m)
    fv.setType(t)
    fv
  }

  private def makeFieldValue(o: Object, t: FieldType): FieldValue = {
    val field = new FieldValue
    field.setValue(o)
    field.setType(t)
    field
  }

  private def toLocalDate(ds: String): LocalDate = LocalDate.parse(ds, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  private def toLocalTime(ds: String): LocalTime = LocalTime.parse(ds, DateTimeFormatter.ofPattern("HH:mm"))

  private def toZonedDatetime(ds: String): ZonedDateTime = ZonedDateTime.parse(ds)
}
