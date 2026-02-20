
public class AwsDynamodbMapperBatchOutputIgnoredNoncompliant extends DynamoBatchWriteOutputNoncompliant {
		//aws-dynamodb-mapper-batch-output-ignored@v1.0
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		DynamoDBMapper myDynamoDBMapper = new DynamoDBMapper(client);
		@Override
		public void mapperNoncompliant(DynamoDBMapperCollection<String> batch) {
			// VIOLAZ: does not have checks to handle errors returned by batch operation.
			List<FailedBatch> failures = myDynamoDBMapper.batchSave(batch);
			System.out.println("Completed Dynamo Batch Write Operation");
			batch.clear();
		}
		
		public void flushNoncompliant(final SqsClient amazonSqs,
                              final String sqsEndPoint,
                              final List<SendMessageBatchRequestEntry> batch)
        throws CloneNotSupportedException {
			if (batch.isEmpty()) {
				return;
			}
			SendMessageBatchResult sendResult =
					amazonSqs.sendMessageBatch(sqsEndPoint, batch);
			// VIOLAZ: no checks to handle errors returned by batch operations.
			batch.clear();
		}

	public void flushCompliant(final SqsClient amazonSqs,
							   final String sqsEndPoint,
							   final List<SendMessageBatchRequestEntry> batch)
			throws SQSUpdateException, CloneNotSupportedException {
		if (batch.isEmpty() || sqsEndPoint == null) {
			return;
		}
		SendMessageBatchResult sendResult =
				amazonSqs.sendMessageBatch(sqsEndPoint, batch);
		if (sendResult == null) {
			return;
		} else {
			final List<BatchResultErrorEntry> failed = sendResult.getFailed();
			// OK: has checks to handle errors returned by batch operations.
			if (!failed.isEmpty()) {
				final String failedMessage = failed.stream()
						.map(batchResultErrorEntry -> String.format("messageId:%s failedReason:%s",
								batchResultErrorEntry.getId(), batchResultErrorEntry.getMessage()))
						.collect(Collectors.joining(","));
				throw new SQSUpdateException("Error occurred while sending messages to SQS::" + failedMessage);
			}
		}
	}

	}
