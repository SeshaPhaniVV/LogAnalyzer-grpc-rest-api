package vvakic2.uic.cs441
package RestApi

import HelperUtils.{CreateLogger, Analyze, HttpClient}
import org.slf4j.Logger

/** Main function to execute post request using Apache HTTP to Lambda API Gateway.
  */
object AnalyzePostRestObj extends App {
  val logger: Logger      = CreateLogger(classOf[AnalyzeRestClient])
  val http: HttpClient    = new HttpClient(logger)
  val searchRestClientObj = new AnalyzeRestClient(http)
  logger.info("Post Request started by AnalyzeRestClient")
  searchRestClientObj.postRequest()
}
