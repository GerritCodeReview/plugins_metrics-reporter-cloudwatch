load(
    "@com_googlesource_gerrit_bazlets//:gerrit_plugin.bzl",
    "gerrit_plugin",
    "gerrit_plugin_dependency_tests",
    "gerrit_plugin_tests",
)

PLUGIN = "metrics-reporter-cloudwatch"

PLUGIN_DEPS = [
    "@metrics-reporter-cloudwatch_plugin_deps//:io_github_azagniotov_dropwizard_metrics_cloudwatch",
    "@metrics-reporter-cloudwatch_plugin_deps//:io_dropwizard_metrics_metrics_jvm",
    "@metrics-reporter-cloudwatch_plugin_deps//:software_amazon_awssdk_cloudwatch",
]

gerrit_plugin(
    name = PLUGIN,
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: metrics-reporter-cloudwatch",
        "Gerrit-Module: com.googlesource.gerrit.plugins.metricsreportercloudwatch.GerritCloudwatchModule",
    ],
    resources = glob(["src/main/resources/**/*"]),
    deps = PLUGIN_DEPS,
)

gerrit_plugin_tests(
    name = "metrics-reporter-cloudwatch_tests",
    srcs = glob(["src/test/java/**/*.java"]),
    plugin = PLUGIN,
    resources = glob(["src/test/resources/**/*"]),
    deps = PLUGIN_DEPS,
)

gerrit_plugin_dependency_tests(plugin = PLUGIN)
