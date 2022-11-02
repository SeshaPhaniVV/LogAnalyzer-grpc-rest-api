import json

import boto3
from datetime import datetime
from datetime import timedelta
import hashlib
import re

s3 = boto3.client("s3")


def getTimeStamp(log_line):
    """Gets time stamp from the log line

    Args:
        log_line (text)

    Returns:
        dateTime
    """
    regex = "(20[0-9][0-9]-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3})"
    result = re.search(
        regex,
        log_line,
    )
    return datetime.strptime(result.group(), "%Y-%m-%d %H:%M:%S.%f")


def lowerIndex(logs, startTimeStamp):
    """Binary search to find the index of the lower bound time

    Args:
        logs (Array[Text])
        startTimeStamp (timeStamp)

    Returns:
        index: int
    """
    start = 0
    end = len(logs) - 1
    while start <= end:
        mid = (start + end) // 2
        currTimestamp = getTimeStamp(logs[mid])
        if currTimestamp >= startTimeStamp:
            end = mid - 1
        else:
            start = mid + 1
    return start


def getParameters(event):
    """Gets parameters needed for lambda function

    Args:
        event (s3 Event): _description_

    Returns:
        tuple(pattern, startTime, endTIme)
    """
    if event["httpMethod"] == "POST":
        body = json.loads(event["body"])
        inputDuration = body["time_duration"]
        input = body["time"]
        pattern = body["pattern"]
    elif event["httpMethod"] == "GET":
        inputDuration = float(event["queryStringParameters"]["time_duration"])
        inputtime = event["queryStringParameters"]["time"]
        inputdate = event["queryStringParameters"]["date"]
        input = inputdate + " " + inputtime
        pattern = event["queryStringParameters"]["pattern"]

    time = datetime.strptime(input, "%Y-%m-%d %H:%M:%S.%f")
    endTime = time + timedelta(minutes=inputDuration)
    startTime = time - timedelta(minutes=inputDuration)
    return (pattern, startTime, endTime)


def getResponseLists(fileContent, startTime, endTime, pattern, index):
    """response Lists are generated

    Args:
        fileContent - file
        startTime - start time given from config
        endTime - endTime given from config
        pattern - pattern to search for
        index - index to start

    Returns:
        tuple(patternList, hashList)
    """
    startSearchTime = re.search(
        "(20[0-9][0-9]-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3})",
        fileContent[index],
    )
    currTime = datetime.strptime(startSearchTime.group(), "%Y-%m-%d %H:%M:%S.%f")
    currIndex = index

    patternList = []
    hashList = []

    while (
        currIndex < len(fileContent) and currTime >= startTime and currTime <= endTime
    ):
        patternresult = re.search(pattern, fileContent[currIndex])
        hash = hashlib.md5(fileContent[currIndex].encode("utf-8")).hexdigest()
        if patternresult:
            patternList.append(patternresult.group())
            hashList.append(hash)
        currIndex = currIndex + 1
        currTime = getTimeStamp(fileContent[currIndex])

    return (patternList, hashList)


def lambda_handler(event, context):
    """Main Lambda Function executed and deployed in AWS

    Args:
        event
        context

    Returns:
        response: json
    """
    fileObj = s3.get_object(Bucket="emrcs441", Key="LogFileGenerator.log")
    fileContent = fileObj["Body"].read().decode("utf-8").split("\n")

    (pattern, startTime, endTime) = getParameters(event)
    index = lowerIndex(fileContent, startTime)

    if index == -1:
        return {
            "statusCode": 400,
            "body": "No logs are present in the given time frame",
        }

    (patternList, hashList) = getResponseLists(fileContent, startTime, endTime, pattern)
    if len(patternList) == 0:
        return {
            "statusCode": 400,
            "body": "Messages with the pattern are not present in the given time frame.",
        }

    result = "Patterns present are {} and MD5 for each of the messages are {}".format(
        patternList, hashList
    )
    return {"statusCode": 200, "body": result}
