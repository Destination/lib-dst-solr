package dst.solr

import java.util.Date
import java.text.SimpleDateFormat
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.language.implicitConversions

object Implicits {
  implicit def String2SolrValue(s: String) : SolrValue = SolrValue(s)

  implicit def Int2Solrvalue(i: Int) : SolrValue = SolrValue(i.toString)

  implicit def Date2SolrValue(d: Date) : SolrValue = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    SolrValue(dateFormat.format(d))
  }

  implicit def DateTime2SolrValue(d: DateTime) : SolrValue = {
    val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    SolrValue(dateFormat.print(d))
  }

  implicit def SolrValue2Date(v: SolrValue) : Date = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    dateFormat.parse(v.value)
  }

  implicit def SolrValue2DateTime(v: SolrValue) : DateTime = {
    val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    dateFormat.parseDateTime(v.value)
  }
}
