package com.amazonaws.services.dynamodbv2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

import com.amazonaws.*;
import com.amazonaws.regions.*;
 
import com.amazonaws.services.dynamodbv2.*; 


import java.math.BigDecimal;

public class AWS_CLOUD {

	@Beta
	@BetaApi
	@InternalApi
	
	
	public void GOOGLE_04(org.joda.time.LocalDate Test)
	
	{
	}

	public com.google.common.io.OutputSupplier GOOGLE_04(org.joda.time.LocalDate Test)
	
	{
	}

	
	public void AWS()
	
	{
		AmazonDynamoDBClient dynamoDB = new AmazonDynamoDBClient(credentialsProvider.getCredentials());
		dynamoDB.setEndpoint("dynamodb.cn-north-1.amazonaws.com.cn");  //VIOLATION

		AmazonDynamoDBClient ddb = new AmazonDynamoDBClient(credentials);
		Region region = Region.getRegion(Regions.fromName(regionName));
		ddb.setRegion(region); //VIOLATION

		
	}
	
	@InternalExtensionOnly
	public void howToDeleteTable() throws InterruptedException {
        String TABLE_NAME = "myTableForMidLevelApi";
        Table table = dynamo.getTable(TABLE_NAME);
        // Wait for the table to become active or deleted
        TableDescription desc = table.waitForActiveOrDelete();
        if (desc == null) {
            System.out.println("Table " + table.getTableName() + " does not exist.");
        } else {
            table.delete();
            // No need to wait, but you could
            table.waitForDelete();
            System.out.println("Table " + table.getTableName() + " has been deleted");
        }
    }

	private String inefficientApiCallsNoncompliant(final String bucketName, final String key) throws IOException {
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		// VIOLAZ: uses inefficient chain of API calls over an efficient single API call.
		S3Object s3object = s3Client.getObject(bucketName, key);
		try {
			return s3object.getObjectMetadata().getVersionId();
		} finally {
			s3object.close();
		}
	}

	@ExperimentalApi
	public void dynamoDBGetItemNoncompliant(Map<String, AttributeValue> key, String tableName) {
		AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.standard().build();
		GetItemRequest request = new GetItemRequest()
				.withTableName(tableName)
				.withKey(key);
		try {
			GetItemResult result = dynamoDBClient.getItem(request);
			// VIOLAZ: result is not null-checked.
			System.out.println(result.getItem().get("key"));
		} catch (ResourceNotFoundException e) {
			log.error(e.getMessage());
		}
	}

	public void dynamoDBGetItemCompliant(Map<String, AttributeValue> key, String tableName) {
		AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.standard().build();
		GetItemRequest request = new GetItemRequest()
				.withTableName(tableName)
				.withKey(key);
		try {
			GetItemResult result = dynamoDBClient.getItem(request);
			// OK: result is null-checked.
			if (result.getItem() != null) {
				System.out.println(result.getItem().get("key"));
			}
		} catch (ResourceNotFoundException e) {
			log.error(e.getMessage());
		}
	}

	public KinesisClientLibConfiguration configureKCLNoncompliant() {
		// VIOLAZ: doesn't set withCallProcessRecordsEvenForEmptyRecordList to true during Kinesis Client Library (KCL) initialization.
		KinesisClientLibConfiguration kclConfig = new KinesisClientLibConfiguration(applicationName,
				streamARN, ddbStreamCredentials, workerID)
				.withMaxRecords(maxRecords)
				.withIdleTimeBetweenReadsInMillis(idleTimeBetweenReadsInMillis)
				.withFailoverTimeMillis(leaseFailOverTimeInMillis)
				.withInitialPositionInStream(InitialPositionInStream.TRIM_HORIZON);
		return kclConfig;
	}

	public KinesisClientLibConfiguration configureKCLCompliant() {
		// OK: sets withCallProcessRecordsEvenForEmptyRecordList to true during Kinesis Client Library (KCL) initialization.
		KinesisClientLibConfiguration kclConfig = new KinesisClientLibConfiguration(applicationName,
				streamARN, ddbStreamCredentials, workerID)
				.withMaxRecords(maxRecords)
				.withCallProcessRecordsEvenForEmptyRecordList(true)
				.withIdleTimeBetweenReadsInMillis(idleTimeBetweenReadsInMillis)
				.withFailoverTimeMillis(leaseFailOverTimeInMillis)
				.withInitialPositionInStream(InitialPositionInStream.TRIM_HORIZON);
		return kclConfig;
		
		KinesisClientLibConfiguration kclConfig1;
		kclConfig1.withCallProcessRecordsEvenForEmptyRecordList (true);
	}

	public KinesisClientLibConfiguration configureKCLCompliant() {
		// VIOLAZ: sets withCallProcessRecordsEvenForEmptyRecordList to true during Kinesis Client Library (KCL) initialization.
		KinesisClientLibConfiguration kclConfig = new KinesisClientLibConfiguration(applicationName,
				streamARN, ddbStreamCredentials, workerID)
				.withMaxRecords(maxRecords)
				.withCallProcessRecordsEvenForEmptyRecordList(false) //VIOLAZ settato a false
				.withIdleTimeBetweenReadsInMillis(idleTimeBetweenReadsInMillis)
				.withFailoverTimeMillis(leaseFailOverTimeInMillis)
				.withInitialPositionInStream(InitialPositionInStream.TRIM_HORIZON);
		return kclConfig;
	}
	
	public Void handleRequest(ScheduledEvent scheduledEvent, Context context) {
			final long startTime = System.currentTimeMillis();
			doSomething(scheduledEvent, context);
			final long endTime = System.currentTimeMillis();
			final long timeElapsed = endTime - startTime;
			PutMetricDataRequest putMetricDataRequest = new PutMetricDataRequest();
			MetricDatum metricDatum = new MetricDatum().withMetricName("TIME_ELAPSED")
					.withUnit(StandardUnit.Milliseconds).withValue((double) timeElapsed);
			putMetricDataRequest.withNamespace("EXAMPLE_NAMESPACE").withMetricData(metricDatum);
			// VIOLAZ: uses CloudWatch to synchronically publish metrics from inside a Lambda.
			cloudwatch.putMetricData(putMetricDataRequest);
			return null;
		}
	
	public Void handleRequest(ScheduledEvent scheduledEvent, Context context) {
			LambdaLogger logger = context.getLogger();
			final long startTime = System.currentTimeMillis();
			doSomething(scheduledEvent, context);
			final long endTime = System.currentTimeMillis();
			final long timeElapsed = endTime - startTime;
			MetricDatum metricDatum = new MetricDatum().withMetricName("TIME_ELAPSED")
					.withUnit(StandardUnit.Milliseconds).withValue((double) timeElapsed);
			// OK: logs the metrics for further postprocessing outside the Lambda.
			logger.log("Metrics: " + metricDatum);
			return null;
		}
		
		public void describeImagesNoncompliant(AmazonEC2 client) {
			final String imageName = "sample_image_name";
			final Filter filter = new Filter("name").withValues(imageName);
			// VIOLAZ: images are filtered using name only.
			DescribeImagesResult result =
					client.describeImages(new DescribeImagesRequest().withFilters(filter));
		}

		public void describeImagesCompliant(AmazonEC2 client) {
			final String imageName = "sample_image_name";
			final String imageOwner = "sample_image_owner";
			final Filter nameFilter = new Filter("name").withValues(imageName);
			final Filter ownerFilter = new Filter("owner-alias").withValues(imageOwner);
			// OK: images are filtered using name and owner.
			DescribeImagesResult result =
					client.describeImages(new DescribeImagesRequest().withFilters(Arrays.asList(nameFilter, ownerFilter)));
		}

		public void s3MultiPartUploadNoncompliant() {
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
					.withRegion(Regions.US_EAST_1)
					.build();
			// VIOLAZ: uses an API that we don't recommend, and a better alternative exists.
			s3Client.initiateMultipartUpload(new InitiateMultipartUploadRequest(bucketName,key));
		}

	public void getUserMetaDataNoncompliant(ObjectMetadata objectMetadata) {
		// VIOLAZ: the metadata key contains an uppercase letter.
		objectMetadata.getUserMetaDataOf("Key");
	}

}

