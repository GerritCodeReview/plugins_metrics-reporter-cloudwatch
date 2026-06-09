# Build

This plugin can be built with Bazel.

Clone (or link) this plugin to the `plugins` directory of Gerrit's source tree.
From Gerrit's source tree, link Gerrit's Bazel version file into the plugin
repository.

```
  ln -sf `pwd`/.bazelversion plugins/metrics-reporter-cloudwatch
```

Put the external dependency Bazel module fragment into the Gerrit `/plugins`
directory, replacing the existing empty one.

```
  cd gerrit/plugins
  ln -fs metrics-reporter-cloudwatch/external_plugin_deps.MODULE.bazel .
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
bazelisk test //plugins/metrics-reporter-cloudwatch/...
```

### Updating Bazel modules

When the plugin's Bazel module dependencies change, regenerate the Bazel
module lockfile to ensure all module versions are recorded and reproducible.

Example:

```bash
  ln -sf `pwd`/.bazelversion plugins/metrics-reporter-cloudwatch
  cd plugins/metrics-reporter-cloudwatch
  bazelisk mod deps --lockfile_mode=update
```

This updates `MODULE.bazel.lock` with the currently resolved module versions.

### Pinning external dependencies

When the plugin's external dependencies are updated, regenerate the dependency
lockfile to pin the new versions.

Example:

```bash
  ln -sf `pwd`/.bazelversion plugins/metrics-reporter-cloudwatch
  cd plugins/metrics-reporter-cloudwatch
  REPIN=1 bazelisk run @metrics-reporter-cloudwatch_plugin_deps//:pin
```

This updates `metrics-reporter-cloudwatch_plugin_deps.lock.json` with the
latest pinned dependency versions.
