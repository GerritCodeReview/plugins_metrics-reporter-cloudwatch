load("//tools/bzl:plugin.bzl", "gerrit_plugin")

gerrit_plugin(
    name = "metrics-reporter-cloudwatch",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: metrics-reporter-cloudwatch",
        "Gerrit-Module: com.googlesource.gerrit.plugins.metricsreportercloudwatch.GerritCloudwatchModule",
    ],
    resources = glob(["src/main/resources/**/*"]),
    deps = [
        "@dropwizard_metrics_cloudwatch//jar",
        "@dropwizard_metrics_jvm//jar",
        "@awssdk_cloudwatch//jar",
        "@awssdk_annotations//jar",
        "@awssdk_arns//jar",
        "@awssdk_auth//jar",
        "@awssdk_sdk_core//jar",
        "@awssdk_aws_core//jar",
        "@awssdk_profiles//jar",
        "@awssdk_regions//jar",
        "@awssdk_metrics_spi//jar",
        "@awssdk_utils//jar",
        "@awssdk_http_client_spi//jar",
        "@awssdk_query_protocol//jar",
        "@awssdk_protocol_core//jar",
        "@awssdk_netty_nio_client//jar",
        "@io_netty_all//jar",
    ],
)
