package dst.solr

import java.net.URLEncoder
import java.util.Locale

// Sort method
sealed trait SortMethod

case class SortByField(field: String, order: String = "asc") extends SortMethod {
  override def toString: String = URLEncoder.encode(field + " " + order, "UTF-8")
}

case class GeospatialSort(field: String, latitude: Double, longitude: Double) extends SortMethod {
  override def toString: String = {
    URLEncoder.encode(String.format(Locale.ENGLISH, "geodist(%s,%.5f,%.5f) asc", field, latitude.asInstanceOf[java.lang.Double], longitude.asInstanceOf[java.lang.Double]), "UTF-8")
  }
}

// Pagination
case class SolrPage(
  page:     Option[Int]      = None,
  pageSize: Option[Int]      = None,
  methods:  List[SortMethod] = List()
) {
  override def toString = methods.mkString("&sort=", ",", "") + "&start=%d&rows=%d".format((page.getOrElse(1) - 1)*pageSize.getOrElse(20), pageSize.getOrElse(20))
}
