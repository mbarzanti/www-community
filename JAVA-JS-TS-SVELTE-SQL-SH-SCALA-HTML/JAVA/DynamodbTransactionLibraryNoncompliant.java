

public class DynamodbTransactionLibraryNoncompliant {
    public void createTransactionNoncompliant() throws Exception {
        // VIOLAZ: uses AWS Lab Transactions Library over DynamoDB native transactional APIs.
        TransactionManager.verifyOrCreateTransactionTable("client", "Transactions", 1, 1, null);
    }
	
	public InvokeResult invokeLambdaNoncompliant() {
		AWSLambda awsLambdaClient = AWSLambdaClientBuilder.standard().build();
		final InvokeRequest request = new InvokeRequest();
		// VIOLAZ: manual retry if a service exception is thrown.
		for(int i=0; i <= 5; i++) {
			try {
				return awsLambdaClient.invoke(request);
			} catch (AmazonServiceException e) {
				log.error("Exception: " + e);
			}
		}
		return null;
	}

	public void changeSqsMessageVisibilityNoncompliant(AmazonSQS amazonSqsClient, ChangeMessageVisibilityRequest request) {
		// VIOLAZ: MessageNotInFlight exception is not checked when changing message visibility.
		amazonSqsClient.changeMessageVisibility(request);
	}

	public void changeSqsMessageVisibilityCompliant(AmazonSQS amazonSqsClient, ChangeMessageVisibilityRequest request) {
		// OK: MessageNotInFlight exception is checked when changing message visibility.
		try {
			amazonSqsClient.changeMessageVisibility(request);
		} catch (MessageNotInflightException ex1) {
			log.info(format("Message with receipt handle %s already visible. Too late to abandon", request.getReceiptHandle()));
		} catch (Exception ex2) {
			log.error(format("Caught unknown exception %s", request.getReceiptHandle()), ex);
		}
		
		try {
		  int[] myNumbers = {1, 2, 3};
		  System.out.println(myNumbers[10]);
			try {
				
				System.out.println(myNumbers[10]);
				
				try {
					System.out.println(myNumbers[10]);
				}
				
				catch (Exception ex3) {}
			}
			catch (Exception ex4) {}
			catch (Exception ex5) {}
		} 
		catch (Exception ex6) {
			System.out.println("Something went wrong.");
		}
		finally {
			System.out.println("The 'try catch' is finished.");
		}
		
	}

	public void transferManagerNoncompliant(PutObjectRequest putRequest) {
		// VIOLAZ: transferManager is not shutdown.
		TransferManager transferManager = TransferManagerBuilder.defaultTransferManager();
		try {
			final Upload upload = transferManager.upload(putRequest);
			upload.waitForCompletion();
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public void transferManagerCompliant(PutObjectRequest putRequest) {
		TransferManager transferManager = TransferManagerBuilder.defaultTransferManager();
		try {
			final Upload upload = transferManager.upload(putRequest);
			upload.waitForCompletion();
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally
		{
			// OK: transferManager is shutdown.
			transferManager.shutdownNow();
		}
	}
	
	public void createStepConfigNoncompliant() {
		// VIOLAZ: ActionOnFailure.TERMINATE_JOB_FLOW is outdated.
		new StepConfig().withName("sampleStepName").withActionOnFailure(ActionOnFailure.TERMINATE_JOB_FLOW);
	}

	public void s3PutObjectNoncompliant(String bucket, String key, InputStream content,
										ObjectMetadata metadata, AmazonS3 s3Client, String owner) {
		log.info("Putting content into bucket {} and key {}", bucket, key);
		// VIOLAZ: readLimit not set.
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, content, metadata);
		putObjectRequest.setExpectedBucketOwner(owner);
		s3Client.putObject(putObjectRequest);
	}

}
