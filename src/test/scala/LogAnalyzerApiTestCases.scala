package vvakic2.uic.cs441

import HelperUtils.{Analyze, HttpClient}
import gRPC.*

import LogAnalyzerService.{
  LogAnalyzerServiceGrpc,
  LogAnalyzerServiceProto,
  RequestBody,
  ResponseBody
}
import RestApi.AnalyzeRestClient
import com.google.gson.Gson
import org.mockito.Mockito
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import org.scalatest.funsuite.AnyFunSuite
import org.mockito.Mockito.*
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.*
import com.typesafe.config.{Config, ConfigFactory}
import io.grpc.{ManagedChannelBuilder, Server, ServerBuilder}
import org.apache.http.HttpEntity
import org.slf4j.{Logger, LoggerFactory}

class LogAnalyzerApiTestCases extends AnyFunSuite with MockitoSugar {
  test("Test to see if Search is getting initiated") {
    val request = new Analyze("0:0:0.000", 1, "aaa")
    assert(request.isInstanceOf[Analyze])
  }

  test("Test LogAnalyzerGrpcServer start method is calling gRPC Start method") {
    val mockServer               = mock[Server]
    val logAnalyzerGrpcServerObj = new LogAnalyzerGrpcServer(mockServer)

    logAnalyzerGrpcServerObj.start()
    Mockito.verify(mockServer).start()
  }

  test("Test LogAnalyzerGrpcServer stop method is calling grpc shutdown method") {
    val mockServer               = mock[Server]
    val logAnalyzerGrpcServerObj = new LogAnalyzerGrpcServer(mockServer)

    logAnalyzerGrpcServerObj.stop()
    Mockito.verify(mockServer).shutdown()
  }

  test("Test to check Rest Client Get Request is called with correct params") {
    val mockHttp       = mock[HttpClient]
    val mockResponse   = mock[CloseableHttpResponse]
    val mockEntity     = mock[HttpEntity]
    val config: Config = ConfigFactory.load("application.conf")

    val time          = config.getString("configuration.time")
    val date          = config.getString("configuration.date")
    val pattern       = config.getString("configuration.pattern")
    val time_duration = config.getInt("configuration.deltaTime")
    val url           = config.getString("configuration.lambdaApiUrl")
    val newUrl        = s"$url?time=$time&date=$date&time_duration=$time_duration&pattern=$pattern"

    println(newUrl)
    Mockito.doReturn(mockResponse).when(mockHttp).get(newUrl)
    Mockito.doReturn(mockEntity).when(mockResponse).getEntity

    val searchRestService = new AnalyzeRestClient(mockHttp)
    searchRestService.makeGetRequest()
    Mockito.verify(mockHttp).get(newUrl)

    reset(mockHttp)
    reset(mockResponse)
    reset(mockEntity)
  }

  test("Test to check Rest Client Post Request is called with correct params") {
    val mockHttp       = mock[HttpClient]
    val mockResponse   = mock[CloseableHttpResponse]
    val mockEntity     = mock[HttpEntity]
    val config: Config = ConfigFactory.load("application.conf")

    val time          = config.getString("configuration.datetime")
    val time_duration = config.getInt("configuration.deltaTime")
    val pattern       = config.getString("configuration.pattern")
    val url           = config.getString("configuration.lambdaApiUrl")
    val payload       = new Analyze(time, time_duration, pattern)
    val payloadAsJson = new Gson().toJson(payload)

    Mockito.doReturn(mockResponse).when(mockHttp).post(url, payloadAsJson)
    Mockito.doReturn(mockEntity).when(mockResponse).getEntity

    val searchRestService = new AnalyzeRestClient(mockHttp)
    searchRestService.postRequest()
    Mockito.verify(mockHttp).post(url, payloadAsJson)

    reset(mockHttp)
    reset(mockResponse)
    reset(mockEntity)
  }

  test("Test to check if GET request is done successfully for Grpc with correct params.") {
    val mockHttp       = mock[HttpClient]
    val mockResponse   = mock[CloseableHttpResponse]
    val mockEntity     = mock[HttpEntity]
    val config: Config = ConfigFactory.load("application.conf")

    val request1      = new RequestBody("0:0:0.000", "2022-10-30", "1", "aaa")
    val url           = config.getString("configuration.lambdaApiUrl")
    val time          = request1.time
    val pattern       = request1.pattern
    val date          = request1.date
    val time_duration = request1.deltaTime
    val newUrl        = s"$url?time=$time&date=$date&time_duration=$time_duration&pattern=$pattern"

    Mockito.doReturn(mockResponse).when(mockHttp).get(newUrl)
    Mockito.doReturn(mockEntity).when(mockResponse).getEntity

    val searchGrpcService = new AnalyzeGrpcService(mockHttp)
    searchGrpcService.analyze(request1)
    Mockito.verify(mockHttp).get(newUrl)

    reset(mockHttp)
    reset(mockResponse)
    reset(mockEntity)
  }
}
