package vvakic2.uic.cs441
package HelperUtils

/** Template to create Request Body for Log analyze
  * @param time
  *   \- time
  * @param time_duration
  *   \- delta
  * @param pattern
  *   \- regex pattern
  */
case class Analyze(time: String, time_duration: Int, pattern: String)
