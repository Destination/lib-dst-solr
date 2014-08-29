package dst.solr

case class SolrIndex(
  host: String,
  name: String,
  port: Int = 8983
)
