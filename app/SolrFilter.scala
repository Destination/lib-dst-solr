package dst.solr.filter

import java.net.URLEncoder
import dst.solr._

sealed trait SolrFilter

case class NotFilter(exclude: SolrFilter, include: Option[SolrFilter] = None) extends SolrFilter {
  override def toString = include.fold[String]("*:*") { f => f.toString }  + "%20NOT%20" + exclude.toString
}

case class ExactFilter(field: String, value: SolrValue, weight: Option[Int] = None) extends SolrFilter {
  override def toString =
    URLEncoder.encode("""%s:"%s"%s""".format(field,value.value, weight.flatMap { w => Some("^" + w) }.getOrElse("")),"UTF-8")
}

case class StartsWithFilter(field: String, value: String, weight: Option[Int] = None) extends SolrFilter {
  override def toString =
    URLEncoder.encode("%s:(%s*)%s".format(field,value, weight.flatMap { w => Some("^" + w)}.getOrElse("")),"UTF-8")
}

case class IntervalFilter(field: String, from: SolrValue, to: SolrValue, weight: Option[Int] = None) extends SolrFilter {
  override def toString =
    URLEncoder.encode("%s:[%s TO %s]%s".format(field, from.value, to.value, weight.flatMap { w => Some("^" + w)}.getOrElse("")), "UTF-8")
}

case class SubstringFilter(field: String, substring: String, weight: Option[Int] = None) extends SolrFilter {
  override def toString = "%s:*%s*%s".format(field,URLEncoder.encode(substring,"UTF-8"), weight.fold("")("^" + _))
}

case class FilterGroupAnd(subfilters: SolrFilter*) extends SolrFilter {
  override def toString = "%28" + subfilters.mkString("%20AND%20") + "%29"
}

case class FilterGroupOr(subfilters: SolrFilter*) extends SolrFilter {
  override def toString = "%28" + subfilters.mkString("%20OR%20") + "%29"
}

case class GeoDistanceFilter(field: String, distance: Double, latitude: Double, longitude: Double) extends SolrFilter {
  override def toString = """{!geofilt sfield=%s d=%d pt=%f,%f}""".format(field, distance, latitude, longitude)
}
