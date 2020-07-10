load(
    "//tools/bzl:plugin.bzl",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
    "gerrit_plugin",
)

load("//tools/bzl:junit.bzl", "junit_tests")

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
        "@awssdk_aws_core//jar",
        "@awssdk_regions//jar",
        "@aws_java_sdk_core//jar",
        "@awssdk_auth//jar",
        "@awssdk_sdk_core//jar",
        "@awssdk_utils//jar",
        "@awssdk_http_client_spi//jar",
        "@awssdk_profiles//jar",
        "@awssdk_query_protocol//jar",
        "@awssdk_protocol_core//jar",
        "@io_netty_all//jar",
        "@awssdk_netty_nio_client//jar",
        "@awssdk_metrics_spi//jar",
        "@reactivestreams//jar",
    ],
)

junit_tests(
    name = "metrics-reporter-cloudwatch_tests",
    srcs = glob(["src/test/java/**/*.java"]),
    resources = glob(["src/test/resources/**/*"]),
    deps = [
        ":metrics-reporter-cloudwatch__plugin_test_deps",
        "@awssdk_auth//jar",
    ],
)

java_library(
    name = "metrics-reporter-cloudwatch__plugin_test_deps",
    testonly = 1,
    visibility = ["//visibility:public"],
    exports = PLUGIN_DEPS + PLUGIN_TEST_DEPS + [
        ":metrics-reporter-cloudwatch__plugin",
    ],
)