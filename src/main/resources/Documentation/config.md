# Configuration

The configuration of the @PLUGIN@ plugin is done in the `[plugin "@PLUGIN@]`
section of the `gerrit.config` file.

## Authentication

To make requests to AWS, this plugin uses the default AWS credential provider
chain. This means that the java SDK will try to find the relevant AWS
credentials (and region) by looking, in order to environment variables
(`AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY`), then system properties
`aws.accessKeyId` and `aws.secretKey`, then `Web Identity Token`, your
`~/.aws/credentials`, and so on.

Find the all the details about the default provider chain
[here](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html) and
[here](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html)

## Metrics Reporter

* `plugin.@PLUGIN@.dryRun` (Optional): the reporter will log.DEBUG the metrics,
instead of doing a real POST to CloudWatch.
    * Type: Boolean
    * Default: false
    * Example: true

There will also be a log entry at WARN level to inform the plugin is running in
dry-run mode:

```** Reporter is running in 'DRY RUN' mode **```

To observe the metrics increase the log level, as such:

```bash
ssh -p <port> admin@<server> gerrit logging set-level debug io.github.azagniotov.metrics.reporter.cloudwatch.CloudWatchReporter
```

* `plugin.@PLUGIN@.namespace` (Optional): The CloudWatch namespace for Gerrit metrics.
    * Type: String
    * Default: "gerrit"
    * AWS Docs: [Namespaces](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/cloudwatch_concepts.html#Namespace)
    * Example: "my-gerrit-metrics"

* `plugin.@PLUGIN@.rate` (Optional): The rate at which metrics should be fired to AWS.
    * Type: Time
    * Default: "60s"
    * Example: 5m

* `plugin.@PLUGIN@.initialDelay` (Optional): The time to delay the first reporting
execution.
    * Type: Time
    * Default: "0"
    * Example: 60 seconds