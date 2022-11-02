package vvakic2.uic.cs441
package gRPC

import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import LogAnalyzerService.{
  LogAnalyzerServiceGrpc,
  LogAnalyzerServiceProto,
  RequestBody,
  ResponseBody
}
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.*
import org.apache.http.*
import org.apache.http.client.*
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet, HttpPost}
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import com.google.gson.Gson
import HelperUtils.CreateLogger
import org.apache.http.client.config.RequestConfig
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import scala.concurrent.Future
import scala.io.Source
import org.slf4j.{Logger, LoggerFactory}
import HelperUtils.HttpClient

/** @param http
  *   \- http class to make lambda calls
  */
class AnalyzeGrpcService(http: HttpClient) extends LogAnalyzerServiceGrpc.LogAnalyzerService {
  val config: Config = ConfigFactory.load("application.conf")
  val logger: Logger = CreateLogger(classOf[AnalyzeGrpcService])

  /** @param request
    *   \- Search request object containing params to make lambda call
    * @return
    */
  override def analyze(request: RequestBody): Future[ResponseBody] = {
    val time          = request.time
    val pattern       = request.pattern
    val date          = request.date
    val time_duration = request.deltaTime
    logger.info(
      s"Executing the search function for the input values -> time: $time, deltaTime = $time_duration"
    )

    logger.info("Performing the GET request to AWS Lambda function.")
    val url    = config.getString("configuration.lambdaApiUrl")
    val newUrl = s"$url?time=$time&date=$date&time_duration=$time_duration&pattern=$pattern"

    logger.info(s"GET request is made to: $newUrl ")
    val response = http.get(newUrl)
    logger.info(s"GET request is made to: $newUrl ")

    val entity = response.getEntity
    val str    = EntityUtils.toString(entity, "UTF-8")
    logger.info(s"Sending back the receive response: $str")
    Future.successful(ResponseBody(str))
  }
}
