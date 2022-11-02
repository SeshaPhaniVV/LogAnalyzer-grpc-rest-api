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
import HelperUtils.CreateLogger

import io.grpc.StatusRuntimeException

import java.util.concurrent.{CountDownLatch, TimeUnit}
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration.*
import scala.util.Random

class LogAnalyzerGrpcClient(host: String, port: Int) {
  private val logger: Logger = CreateLogger(classOf[LogAnalyzerGrpcClient])
  private val channel        = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build()
  private val blockingStub: LogAnalyzerServiceGrpc.LogAnalyzerServiceBlockingStub =
    LogAnalyzerServiceGrpc.blockingStub(channel)

  def shutdown(): Unit = channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)

  /** @param time:
    *   Time
    * @param date:
    *   Date
    * @param deltaTime:
    *   Delta added to time
    * @param pattern:
    *   pattern to search in logs
    */
  def search(time: String, date: String, deltaTime: String, pattern: String): Unit = {
    val request = RequestBody(time, date, deltaTime, pattern)
    try {
      logger.info("Send the gRPC request to the server.")
      val response = blockingStub.analyze(request)
      logger.info(s"The response received from Server: ${response.result}")
      println(s"Response Received: $response")
    } catch {
      case e: StatusRuntimeException =>
        logger.error(s"RPC failed with:${e.getStatus}")
    }
  }
}

/** Main function to start Grpc client
  */
object LogAnalyzerGrpcClient extends App {
  val logger         = CreateLogger(classOf[LogAnalyzerGrpcClient])
  val config: Config = ConfigFactory.load("application.conf")
  val client = new LogAnalyzerGrpcClient(
    config.getString("configuration.clientHost"),
    config.getInt("configuration.clientPort")
  )

  // Makes GrpcServer call
  try {
    client.search(
      config.getString("configuration.time"),
      config.getString("configuration.date"),
      config.getString("configuration.deltaTime"),
      config.getString("configuration.pattern")
    )
  } finally {
    logger.info("Shutting down the server")
    client.shutdown()
  }
}
