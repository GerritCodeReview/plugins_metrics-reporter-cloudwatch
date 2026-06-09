load(
    "@com_googlesource_gerrit_bazlets//:gerrit_plugin.bzl",
    "gerrit_plugin",
    "gerrit_plugin_dependency_tests",
    "gerrit_plugin_tests",
)

PLUGIN = "metrics-reporter-cloudwatch"

EXT_DEPS = [
    "io.github.azagniotov:dropwizard-metrics-cloudwatch",
    "io.dropwizard.metrics:metrics-jvm",
    "software.amazon.awssdk:cloudwatch",
]

gerrit_plugin(
    srcs = glob(["src/main/java/**/*.java"]),
    ext_deps = EXT_DEPS,
    manifest_entries = [
        "Gerrit-PluginName: metrics-reporter-cloudwatch",
        "Gerrit-Module: com.googlesource.gerrit.plugins.metricsreportercloudwatch.GerritCloudwatchModule",
    ],
    plugin = PLUGIN,
    resources = glob(["src/main/resources/**/*"]),
)

gerrit_plugin_tests(
    srcs = glob(["src/test/java/**/*.java"]),
    ext_deps = EXT_DEPS,
    plugin = PLUGIN,
    resources = glob(["src/test/resources/**/*"]),
)
