package vvakic2.uic.cs441
package HelperUtils

import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet, HttpPost}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.{CloseableHttpClient, HttpClientBuilder}
import org.slf4j.Logger

/** HttpClient to make Http API requests
  *
  * @param logger
  *   \- Logger Object
  */
class HttpClient(logger: Logger) {
  private val timeout = 1800
  // Pre defined request config set for the api calls
  private val requestConfig = RequestConfig
    .custom()
    .setConnectTimeout(timeout * 1000)
    .setConnectionRequestTimeout(timeout * 1000)
    .setSocketTimeout(timeout * 1000)
    .build()

  /** Creates Http Client and returns it
    * @return
    */
  private def getClient: CloseableHttpClient = {
    HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build()
  }

  /** Post request wrapper
    * @param url
    *   \- Url to which http request to be made
    * @param payload
    *   \- JSON string payload for the body
    * @return
    */
  def post(url: String, payload: String): CloseableHttpResponse = {
    val client         = getClient
    val post: HttpPost = new HttpPost(url)
    this.logger.info(url)
    post.addHeader("Content-Type", "application/json")
    post.setEntity(new StringEntity(payload))
    client.execute(post)
  }

  /** Get Request wrapper
    * @param url
    *   \- URL to which http request to be made including query parameters
    * @return
    */
  def get(url: String): CloseableHttpResponse = {
    val client       = getClient
    val get: HttpGet = new HttpGet(url)
    this.logger.info(url)
    client.execute(get)
  }
}
