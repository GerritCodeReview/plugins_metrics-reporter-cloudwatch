# Configuration

The configuration of the @PLUGIN@ plugin is done in the `[plugin "@PLUGIN@"]`
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

## InstanceId Dimension

Gerrit can be optionally configured to have a unique identifier, the
[instanceId](https://gerrit-review.googlesource.com/Documentation/config-gerrit.html#gerrit.instanceId),
which represents a specific instance within a group of Gerrit instances.

When the instanceId is set this plugin will hydrate all the metrics sent to CloudWatch with
an additional dimension named `InstanceId`, populated with the value of the `gerrit.instanceId`
configuration.

This is useful as it allows to correlate cloudwatch metrics to specific instances
they originated from.

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

* `plugin.@PLUGIN@.jvmMetrics` (Optional): Add JVM metrics to the registry

   * Type: Boolean
   * Default: false
   * Example: true

  Supported metrics include:

  _jvm.uptime.*_, _jvm.current_time.*_, _jvm.classes.*_, _jvm.fd_usage.*_, _jvm.buffers.*_,
  _jvm.gc.*_, _jvm.memory.*_, _jvm.thread-states.*_

  Refer to the [codahale-aggregated-metrics-cloudwatch-reporter](https://github.com/azagniotov/codahale-aggregated-metrics-cloudwatch-reporter/)
documentation for more information on this.


* `plugin.@PLUGIN@.excludeMetrics` (Optional): Regex pattern used to exclude
metrics from the report. It can be specified multiple times.
Note that pattern matching is done on the whole metric name, not only on a part of it.
    * Type: String
    * Example: "plugins.*"

In case of invalid pattern, the plugin will fail to load and the relevant error will
be logged in the _error_log_ file.

* `plugin.@PLUGIN@.reportRawCountValue` (Optional): Will report the raw value of count
metrics instead of reporting only the count difference since the last report.

   * Type: Boolean
   * Default: false
   * Example: true