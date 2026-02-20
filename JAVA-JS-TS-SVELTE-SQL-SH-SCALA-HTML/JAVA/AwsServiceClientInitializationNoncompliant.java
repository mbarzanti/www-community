public class AwsServiceClientInitializationNoncompliant implements RequestHandler<String, Void> {

    private DataPipeline dataPipeline;

    public void AwsServiceClientInitializationNoncompliant() {
        // VIOLAZ: AWS region provider not specified.
        dataPipeline = DataPipelineAsyncClientBuilder.defaultClient();
    }

    @Override
    public Void handleRequest(String requestEvent, Context context) {
        // Handle the request here.
        return null;
    }
}