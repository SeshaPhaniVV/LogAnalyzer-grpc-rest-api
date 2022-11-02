#!/bin/bash
echo "<=====================starting script======================>"
echo " creating logs "
java -cp LogFileGenerator-assembly-1.jar:logback-classic-1.4.1.jar:logback-core-1.4.1.jar:slf4j-api-2.0.1.jar vvakic2.uic.cs441.GenerateLogData
echo "Done creating logs"
echo "<===================Uploading to S3=====================>"
aws s3 cp /home/ec2-user/LogFileGenerator/log/LogFileGenerator.log s3://emrcs441
echo "uploaded to S3"
echo "<========================Script Upload Done=====================>"
