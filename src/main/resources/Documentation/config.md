# Configuration

The configuration of the @PLUGIN@ plugin is done in `gerrit.config` and in the `@PLUGIN@.config` file.


## Authentication

To make requests to AWS, this plugin uses the default AWS credential provider chain.
This means that the java SDK will try to find the relevant AWS credentials (and region) by looking, in order to
environment variables (`AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY`), then system properties
`aws.accessKeyId` and `aws.secretKey`, then `Web Identity Token`, your `~/.aws/credentials`, and so on

You can find the all the details about the default provider chain
[here](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html) and
[here](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html)

If the default credential chain doesn’t work for your deployment strategy, you can set credentials explicitly by defining
them in the `gerrit.config` file.
Note that your `secretKeyId` should be stored in a secure way, so you should set it in the `secure.config`.
your configuration will look like this:

`etc/gerrit.config`
```
[plugin "metrics-reporter-cloudwatch"]
  region = us-east-1
  accessKeyId = "myAccessKey"
```

`etc/secure.config`
```
[plugin "metrics-reporter-cloudwatch"]
  secretKeyId = "mySecretKey"
```

## Metrics Reporter

* `cloudwatch.dryRun` (Optional): the reporter will log.DEBUG the metrics, instead of doing a real POST to CloudWatch.  
    * Type: Boolean
    * Default: false
    * Example: true

You can observe the metrics by increasing the log level, as such:

```bash
ssh -p <port> admin@<server> gerrit logging set-level debug io.github.azagniotov.metrics.reporter.cloudwatch.CloudWatchReporter
```

* `cloudwatch.namespace` (Optional): The CloudWatch namespace for Gerrit metrics.
    * Type: String
    * Default: "gerrit"
    * AWS Docs: [Namespaces](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/cloudwatch_concepts.html#Namespace)
    * Example: "my-gerrit-metrics"

* `cloudwatch.rate` (Optional): The rate at which metrics should be fired to AWS.
    * Type: Time
    * Default: "60s"
    * Example: 5 minutes

* `cloudwatch.initialDelay` (Optional): The time to delay the first reporting execution.
    * Type: Time
    * Default: "0"
    * Example: 60 seconds