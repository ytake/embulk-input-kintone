package net.jp.ytake.embulk.input.kintone

import java.util.{Optional, List => JList, Map => JMap}
import scala.jdk.CollectionConverters._
import scala.language.implicitConversions

object implicits {
  implicit def JList2Seq[A](a: JList[A]): Seq[A] = a.asScala.toSeq

  implicit def Seq2JList[A](a: Seq[A]): JList[A] = a.asJava

  implicit def OptionalJList2OptionSeq[A](
                                           a: Optional[JList[A]]
                                         ): Option[Seq[A]] = a.map(JList2Seq(_))

  implicit def OptionSeq2OptionalJList[A](
                                           a: Option[Seq[A]]
                                         ): Optional[JList[A]] = a.map(Seq2JList)

  implicit def JMap2Map[K, V](a: JMap[K, V]): Map[K, V] = a.asScala.toMap

  implicit def Map2JMap[K, V](a: Map[K, V]): JMap[K, V] = a.asJava

  implicit def OptionalJMap2OptionMap[K, V](
                                             a: Optional[JMap[K, V]]
                                           ): Option[Map[K, V]] = a.map(JMap2Map(_))

  implicit def OptionMap2Optional2JMap[K, V](
                                              a: Option[Map[K, V]]
                                            ): Optional[JMap[K, V]] = a.map(Map2JMap)

  implicit def Optional2Option[A](a: Optional[A]): Option[A] =
    if (a.isPresent) Some(a.get()) else None

  implicit def Option2Optional[A](a: Option[A]): Optional[A] = a match {
    case Some(v) => Optional.of(v)
    case None => Optional.empty()
  }
}
