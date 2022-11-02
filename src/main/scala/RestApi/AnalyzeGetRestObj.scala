package vvakic2.uic.cs441
package RestApi

import HelperUtils.{CreateLogger, Analyze, HttpClient}
import org.slf4j.Logger

object AnalyzeGetRestObj extends App {
  val logger: Logger      = CreateLogger(classOf[AnalyzeRestClient])
  val http: HttpClient    = new HttpClient(logger)
  val searchRestClientObj = new AnalyzeRestClient(http)
  logger.info("Get request started by AnalyzeRestClient.")
  searchRestClientObj.makeGetRequest()
}
