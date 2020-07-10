Configuration
=============

The configuration of the @PLUGIN@ plugin is done in the `@PLUGIN@.config`
file.

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