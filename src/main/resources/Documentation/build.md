# Build

This plugin can be built with Bazel.

Clone (or link) this plugin to the `plugins` directory of Gerrit's source tree.
Put the external dependency Bazel build file into the Gerrit /plugins directory,
replacing the existing empty one.

```
  cd gerrit/plugins
  ln -fs metrics-reporter-cloudwatch/external_plugin_deps.bzl .
```

Then run:

```bash
  bazelisk build plugins/metrics-reporter-cloudwatch
```

in the root of Gerrit's source tree to build
The output is created in:

```
  bazel-bin/plugins/metrics-reporter-cloudwatch/metrics-reporter-cloudwatch.jar
```

# Test


You can run tests with bazelisk, as such:

```bash
bazelisk plugins/metrics-reporter-cloudwatch:metrics-reporter-cloudwatch_tests
```

