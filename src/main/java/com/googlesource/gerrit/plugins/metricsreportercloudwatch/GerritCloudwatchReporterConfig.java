// Copyright (C) 2020 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.googlesource.gerrit.plugins.metricsreportercloudwatch;

import static java.util.stream.Collectors.toList;

import com.codahale.metrics.MetricFilter;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.ConfigUtil;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Pattern;

class GerritCloudwatchReporterConfig {
  protected static final String KEY_NAMESPACE = "namespace";
  protected static final String KEY_RATE = "rate";
  protected static final String KEY_DRYRUN = "dryRun";
  protected static final String KEY_INITIAL_DELAY = "initialDelay";
  protected static final String KEY_EXCLUDE_METRICS = "excludeMetrics";

  protected static final String DEFAULT_NAMESPACE = "gerrit";
  protected static final String DEFAULT_EMPTY_STRING = "";
  protected static final Boolean DEFAULT_DRY_RUN = false;
  protected static final Long DEFAULT_RATE_SECS = 60L;
  protected static final Integer DEFAULT_INITIAL_DELAY_SECS = 0;

  private final int rate;
  private final String namespace;
  private final int initialDelay;
  private final Boolean dryRun;
  private final MetricFilter exclusionFilter;

  @Inject
  public GerritCloudwatchReporterConfig(
      PluginConfigFactory configFactory, @PluginName String pluginName) {
    PluginConfig pluginConfig = configFactory.getFromGerritConfig(pluginName);

    this.namespace = pluginConfig.getString(KEY_NAMESPACE, DEFAULT_NAMESPACE);

    this.dryRun = pluginConfig.getBoolean(KEY_DRYRUN, DEFAULT_DRY_RUN);

    this.rate =
        (int)
            ConfigUtil.getTimeUnit(
                pluginConfig.getString(KEY_RATE, DEFAULT_EMPTY_STRING),
                DEFAULT_RATE_SECS,
                TimeUnit.SECONDS);

    this.initialDelay =
        (int)
            ConfigUtil.getTimeUnit(
                pluginConfig.getString(KEY_INITIAL_DELAY, DEFAULT_EMPTY_STRING),
                DEFAULT_INITIAL_DELAY_SECS,
                TimeUnit.SECONDS);

    this.exclusionFilter = buildExclusionFilter(pluginConfig.getStringList(KEY_EXCLUDE_METRICS));
  }

  public int getRate() {
    return rate;
  }

  public String getNamespace() {
    return namespace;
  }

  public int getInitialDelay() {
    return initialDelay;
  }

  public Boolean getDryRun() {
    return dryRun;
  }

  public MetricFilter getExclusionFilter() {
    return exclusionFilter;
  }

  private MetricFilter buildExclusionFilter(String[] exclusionList) {
    final List<Pattern> excludedMetricPatterns =
        Arrays.stream(exclusionList).map(Pattern::compile).collect(toList());

    Predicate<String> filter =
        s -> excludedMetricPatterns.stream().anyMatch(e -> e.matcher(s).matches());
    return (s, metric) -> !filter.test(s);
  }
}
