package vvakic2.uic.cs441
package gRPC

import LogAnalyzerService.{
  LogAnalyzerServiceGrpc,
  LogAnalyzerServiceProto,
  RequestBody,
  ResponseBody
}
import com.google.api.Http
import HelperUtils.CreateLogger

import io.grpc.stub.StreamObserver
import io.grpc.{Server, ServerBuilder}

import scala.io.Source
import scala.concurrent.ExecutionContext
import org.slf4j.{Logger, LoggerFactory}
import com.typesafe.config.{Config, ConfigFactory}
import HelperUtils.HttpClient

/** LogAnalyzerGrpc class
  *
  * @param server
  *   \- gRpc Server created to return lambda function response
  */
class LogAnalyzerGrpcServer(server: Server) {
  val config: Config = ConfigFactory.load("application.conf")
  val logger: Logger = CreateLogger(classOf[LogAnalyzerGrpcServer])

  /** grpc Server Start function
    */
  def start(): Unit = {
    server.start()
    logger.info(s"Server started, listening on ${server.getPort}")
    sys.addShutdownHook {
      System.err.println("Shutting down gRPC server")
      logger.info(s"Server shutting down")
      stop()
      System.err.println("server shut down")
    }
    ()
  }

  /** Stop the Grpc server
    */
  def stop(): Unit = {
    server.shutdown()
  }

  def blockUntilShutdown(): Unit = {
    server.awaitTermination()
  }
}

/** Main function to start the grpc server
  */
object LogAnalyzerGrpcServer extends App {
  val logger           = CreateLogger(classOf[LogAnalyzerGrpcServer])
  val http: HttpClient = new HttpClient(logger)

  // Create an instance of Server class.
  logger.info("Created an instance of SearchGrpcServer class with passing all the parameters")
  val server = new LogAnalyzerGrpcServer(
    ServerBuilder
      .forPort(8980)
      .addService(
        LogAnalyzerServiceGrpc.bindService(
          new AnalyzeGrpcService(http),
          scala.concurrent.ExecutionContext.global
        )
      )
      .build()
  )

  logger.info("Starting the gRPC server")
  server.start()

  // This will keep the server alive until it is killed by passing the input from keyboard
  server.blockUntilShutdown()
  logger.info("Keeping the gRPC server alive until it is killed by passing the input from keyboard")
}
