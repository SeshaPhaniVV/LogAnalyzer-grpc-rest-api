package vvakic2.uic.cs441
package RestApi

import org.apache.commons.*
import org.apache.http.*
import org.apache.http.client.*
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet, HttpPost}
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import com.google.gson.Gson
import org.apache.http.client.config.RequestConfig
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.{Logger, LoggerFactory}
import HelperUtils.{CreateLogger, Analyze}
import HelperUtils.HttpClient

/** @param http
  *   \- Http client to make requests to lambda
  */
class AnalyzeRestClient(http: HttpClient) {
  val config: Config = ConfigFactory.load("application.conf")
  val logger: Logger = CreateLogger(classOf[AnalyzeRestClient])

  /** Post request to Lambda function by getting configuration
    */
  def postRequest(): Unit = {
    logger.info("Started execution of postRequest function")
    val time          = config.getString("configuration.datetime")
    val time_duration = config.getInt("configuration.deltaTime")
    val pattern       = config.getString("configuration.pattern")
    val url           = config.getString("configuration.lambdaApiUrl")

    logger.info("Got parameters from configuratuion")

    val payload = Analyze(time, time_duration, pattern)
    // Converts payload object to json format
    val payloadAsJson = new Gson().toJson(payload)
    val response      = http.post(url, payloadAsJson)
    val entity        = response.getEntity
    val str           = EntityUtils.toString(entity, "UTF-8")

    logger.info(s"Post request is made to: $url, With following payload: $payloadAsJson")
    logger.info(s"Response is: $str ")
  }

  /** get Request to make requests to lambda
    */
  def makeGetRequest(): Unit = {
    logger.info("Started the execution of getRequest function.")
    val time          = config.getString("configuration.time")
    val date          = config.getString("configuration.date")
    val pattern       = config.getString("configuration.pattern")
    val time_duration = config.getInt("configuration.deltaTime")
    val url           = config.getString("configuration.lambdaApiUrl")

    // Updating URL with query parameters
    val newUrl = s"$url?time=$time&date=$date&time_duration=$time_duration&pattern=$pattern"

    val response = http.get(newUrl)
    val entity   = response.getEntity
    val str      = EntityUtils.toString(entity, "UTF-8")

    logger.info(s"GET request is made to: $newUrl ")
    logger.info("Response is " + str)
  }
}
