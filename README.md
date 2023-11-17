# Seaweed test
This repository provides an example showing how accessing a SeaweedFS S3 bucket using the Hadoop FileSystem.

## The issue
I'm unable to use the Hadoop `FileSystem` class to write to an instance of SeaweedFS that exposes an S3 API.

Creating buckets, listing, writing a file, etc. all works when using the AWS CLI.

I'm using the configuration options described in:
- https://github.com/seaweedfs/seaweedfs/wiki/HDFS-via-S3-connector

## Reproducing

### Start SeaweedFS
Run `docker-compose up` from the root to start an instance that exposes the S3 API.

### Create a test bucket
Use the AWS CLI to create a bucket, for example:
```
aws --endpoint-url http://localhost:8333 s3 ls
aws --endpoint-url http://localhost:8333 s3 mb s3://test-bucket
make_bucket: test-bucket
aws --endpoint-url http://localhost:8333 s3 ls
2023-11-17 17:55:53 test-bucket
```

### Run the test
Run the test in `./src/test/java/test/HadoopAccessTest.java` using your prefered method or execute `mvn test`

The test will fail with an error: `com.amazonaws.services.s3.model.AmazonS3Exception: Signed request requires setting up
SeaweedFS S3 authentication`

### Full stack-trace
```
org.apache.hadoop.fs.s3a.AWSBadRequestException: Writing Object on e4350e44-b7ed-43e8-816a-0005fd8f7bd6/file.tmp: com.amazonaws.services.s3.model.AmazonS3Exception: Signed request requires setting up SeaweedFS S3 authentication (Service: Amazon S3; Status Code: 400; Error Code: InvalidRequest; Request ID: 1700240676060853763; S3 Extended Request ID: null; Proxy: null), S3 Extended Request ID: null:InvalidRequest: Signed request requires setting up SeaweedFS S3 authentication (Service: Amazon S3; Status Code: 400; Error Code: InvalidRequest; Request ID: 1700240676060853763; S3 Extended Request ID: null; Proxy: null)

	at org.apache.hadoop.fs.s3a.S3AUtils.translateException(S3AUtils.java:247)
	at org.apache.hadoop.fs.s3a.Invoker.once(Invoker.java:124)
	at org.apache.hadoop.fs.s3a.Invoker.lambda$retry$4(Invoker.java:376)
	at org.apache.hadoop.fs.s3a.Invoker.retryUntranslated(Invoker.java:468)
	at org.apache.hadoop.fs.s3a.Invoker.retry(Invoker.java:372)
	at org.apache.hadoop.fs.s3a.Invoker.retry(Invoker.java:347)
	at org.apache.hadoop.fs.s3a.WriteOperationHelper.retry(WriteOperationHelper.java:213)
	at org.apache.hadoop.fs.s3a.WriteOperationHelper.putObject(WriteOperationHelper.java:577)
	at org.apache.hadoop.fs.s3a.S3ABlockOutputStream.lambda$putObject$0(S3ABlockOutputStream.java:617)
	at org.apache.hadoop.thirdparty.com.google.common.util.concurrent.TrustedListenableFutureTask$TrustedFutureInterruptibleTask.runInterruptibly(TrustedListenableFutureTask.java:125)
	at org.apache.hadoop.thirdparty.com.google.common.util.concurrent.InterruptibleTask.run(InterruptibleTask.java:69)
	at org.apache.hadoop.thirdparty.com.google.common.util.concurrent.TrustedListenableFutureTask.run(TrustedListenableFutureTask.java:78)
	at org.apache.hadoop.util.SemaphoredDelegatingExecutor$RunnableWithPermitRelease.run(SemaphoredDelegatingExecutor.java:225)
	at org.apache.hadoop.util.SemaphoredDelegatingExecutor$RunnableWithPermitRelease.run(SemaphoredDelegatingExecutor.java:225)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
	at java.base/java.lang.Thread.run(Thread.java:840)
Caused by: com.amazonaws.services.s3.model.AmazonS3Exception: Signed request requires setting up SeaweedFS S3 authentication (Service: Amazon S3; Status Code: 400; Error Code: InvalidRequest; Request ID: 1700240676060853763; S3 Extended Request ID: null; Proxy: null), S3 Extended Request ID: null
	at com.amazonaws.http.AmazonHttpClient$RequestExecutor.handleErrorResponse(AmazonHttpClient.java:1879)
	at com.amazonaws.http.AmazonHttpClient$RequestExecutor.handleServiceErrorResponse(AmazonHttpClient.java:1418)
	at com.amazonaws.http.AmazonHttpClient$RequestExecutor.executeOneRequest(AmazonHttpClient.java:1387)
	at com.amazonaws.http.AmazonHttpClient$RequestExecutor.executeHelper(AmazonHttpClient.java:1157)
	at com.amazonaws.http.AmazonHttpClient$RequestExecutor.doExecute(AmazonHttpClient.java:814)
	at com.amazonaws.http.AmazonHttpClient$RequestExecutor.executeWithTimer(AmazonHttpClient.java:781)
	at com.amazonaws.http.AmazonHttpClient$RequestExecutor.execute(AmazonHttpClient.java:755)
	at com.amazonaws.http.AmazonHttpClient$RequestExecutor.access$500(AmazonHttpClient.java:715)
	at com.amazonaws.http.AmazonHttpClient$RequestExecutionBuilderImpl.execute(AmazonHttpClient.java:697)
	at com.amazonaws.http.AmazonHttpClient.execute(AmazonHttpClient.java:561)
	at com.amazonaws.http.AmazonHttpClient.execute(AmazonHttpClient.java:541)
	at com.amazonaws.services.s3.AmazonS3Client.invoke(AmazonS3Client.java:5456)
	at com.amazonaws.services.s3.AmazonS3Client.invoke(AmazonS3Client.java:5403)
	at com.amazonaws.services.s3.AmazonS3Client.access$300(AmazonS3Client.java:421)
	at com.amazonaws.services.s3.AmazonS3Client$PutObjectStrategy.invokeServiceCall(AmazonS3Client.java:6532)
	at com.amazonaws.services.s3.AmazonS3Client.uploadObject(AmazonS3Client.java:1861)
	at com.amazonaws.services.s3.AmazonS3Client.putObject(AmazonS3Client.java:1821)
	at org.apache.hadoop.fs.s3a.S3AFileSystem.lambda$putObjectDirect$18(S3AFileSystem.java:2993)
	at org.apache.hadoop.fs.statistics.impl.IOStatisticsBinding.trackDurationOfSupplier(IOStatisticsBinding.java:651)
	at org.apache.hadoop.fs.s3a.S3AFileSystem.putObjectDirect(S3AFileSystem.java:2990)
	at org.apache.hadoop.fs.s3a.WriteOperationHelper.lambda$putObject$7(WriteOperationHelper.java:580)
	at org.apache.hadoop.fs.store.audit.AuditingFunctions.lambda$withinAuditSpan$0(AuditingFunctions.java:62)
	at org.apache.hadoop.fs.s3a.Invoker.once(Invoker.java:122)
	... 15 more
```