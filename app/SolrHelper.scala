package dst.solr

import play.api.Play
import play.api.Logger

import scala.concurrent._
import scala.language.implicitConversions

import play.api.libs.json._
import play.api.libs.ws.WS

import java.net.URLEncoder

object SolrHelper {
  def selectUrl(index: SolrIndex) =                   "http://%s:%d/%s/select".format(index.host, index.port, index.name)
  def updateUrl(index: SolrIndex, commit: Boolean) =  "http://%s:%d/%s/update/json".format(index.host,index.port,index.name) + { if (commit) "?commitWithin=30000" else "" }

  def select(index: SolrIndex, queryString: String, rows: Int = 20)(implicit context: ExecutionContext) = {
    WS.url(selectUrl(index) + "?wt=json&rows=%d&q=%s".format(rows,queryString)).withRequestTimeout(5000).get.map { response =>
      val result : Option[JsObject] = try {
        Some(response.json.asInstanceOf[JsObject])
      }
      catch {
        case t: Throwable => {
          Logger.error("Request failed: " + selectUrl(index) + "?wt=json&q=" + queryString, t)
          None
        }
      }

      result
    }
  }

  def select(index: SolrIndex, query: SolrQuery)(implicit context: ExecutionContext) = {
    WS.url(selectUrl(index) +  query.toString).withRequestTimeout(5000).get().map { response =>
      if (response.status == 200) {
        Some(response.json.asInstanceOf[JsObject])
      }
      else {
        Logger.debug("Request failed: " + selectUrl(index) + query.toString + " (status code=" + response.status + ")")
        None
      }
    }
  }

  def insert(value: JsArray, index: SolrIndex, commit: Boolean = true)(implicit context: ExecutionContext) = {
    WS.url(updateUrl(index, commit)).withHeaders("Content-type" -> "application/json").post (value)
  }

  def delete(query: String, index: SolrIndex, commit: Boolean = true)(implicit context: ExecutionContext) = {
    WS.url(updateUrl(index, commit)).post(Json.obj( "delete" -> Json.obj("query" ->query)))
  }
}
