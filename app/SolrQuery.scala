package dst.solr

import java.net.URLEncoder

import dst.solr.filter._

case class SolrQuery(
  searchString:   Option[String]     = None,
  searchFilter:   Option[SolrFilter] = None,
  filters:        List[SolrFilter]   = List(),
  page:           SolrPage           = SolrPage(),
  facetMinCount:  Int                = -1,
  facetFields:    List[String]       = List(),
  groupField:     Option[String]     = None,
  statsFields:    List[String]       = List()
) {
  private def statsParameters: String = {
    if (!statsFields.isEmpty) {
      "&stats=true" + statsFields.map("&stats.field=" + _).mkString("")
    }
    else ""
  }

  private def groupParameters: String =
    groupField.fold("")(field => "&fq=" + URLEncoder.encode("{!collapse field=%s}".format(field), "UTF-8"))

  private def facetParameters: String = {
    if (!facetFields.isEmpty) {
      "&facet=true&facet.limit=-1&facet.mincount=" + facetMinCount  + facetFields.map("&facet.field="+_).mkString("")
    }
    else ""
  }

  private def functionQueries: String =
    filters.map("&fq=" + _).mkString("")

  private def pageParameters: String =
    page.toString

  def searchParameter =
    "&q=" + searchString.fold(searchFilter.flatMap { s => Some(s.toString) }) { s =>  Some(URLEncoder.encode(s,"UTF-8")) }.getOrElse("*:*")

  override def toString : String = {
    "?wt=json" + searchParameter + functionQueries + facetParameters + groupParameters + pageParameters + statsParameters
  }

  def withPaging(page: SolrPage) = copy(page = page)
  def withStatsFor(fields: List[String]) = copy(statsFields = fields)
  def groupBy(field: Option[String]) = copy(groupField = field)
  def withFacets(fields: List[String], minCount: Int = -1) = copy(facetFields = fields, facetMinCount = minCount)
}
