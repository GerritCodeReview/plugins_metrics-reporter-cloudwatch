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

import static com.google.common.truth.Truth.assertThat;
import static com.google.gerrit.testing.GerritJUnit.assertThrows;
import static org.mockito.Mockito.when;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricFilter;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import org.eclipse.jgit.lib.Config;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Arrays;
import java.util.regex.PatternSyntaxException;

@RunWith(MockitoJUnitRunner.class)
public class GerritCloudwatchReporterConfigTest {
  private static final String PLUGIN_NAME = "foo";
  private final PluginConfig emptyGlobalPluginConfig = new PluginConfig(PLUGIN_NAME, new Config());

  GerritCloudwatchReporterConfig reporterConfig;

  @Mock PluginConfigFactory configFactory;

  @Test
  public void shouldGetAllDefaultsWhenConfigurationIsEmpty() {
    when(configFactory.getFromGerritConfig(PLUGIN_NAME)).thenReturn(emptyGlobalPluginConfig);
    reporterConfig = new GerritCloudwatchReporterConfig(configFactory, PLUGIN_NAME);

    assertThat(reporterConfig.getInitialDelay())
        .isEqualTo(GerritCloudwatchReporterConfig.DEFAULT_INITIAL_DELAY_SECS);
    assertThat(reporterConfig.getNamespace())
        .isEqualTo(GerritCloudwatchReporterConfig.DEFAULT_NAMESPACE);
    assertThat(reporterConfig.getRate())
        .isEqualTo(GerritCloudwatchReporterConfig.DEFAULT_RATE_SECS);
    assertThat(reporterConfig.getDryRun())
        .isEqualTo(GerritCloudwatchReporterConfig.DEFAULT_DRY_RUN);
  }

  @Test
  public void shouldReadMetricValuesFromConfiguration() {
    PluginConfig globalPluginConfig = emptyGlobalPluginConfig;
    globalPluginConfig.setString(GerritCloudwatchReporterConfig.KEY_NAMESPACE, "foobar");
    globalPluginConfig.setString(GerritCloudwatchReporterConfig.KEY_RATE, "3m");
    globalPluginConfig.setString(GerritCloudwatchReporterConfig.KEY_INITIAL_DELAY, "20s");
    globalPluginConfig.setBoolean(GerritCloudwatchReporterConfig.KEY_DRYRUN, true);

    when(configFactory.getFromGerritConfig(PLUGIN_NAME)).thenReturn(globalPluginConfig);
    reporterConfig = new GerritCloudwatchReporterConfig(configFactory, PLUGIN_NAME);

    assertThat(reporterConfig.getInitialDelay()).isEqualTo(20);
    assertThat(reporterConfig.getNamespace()).isEqualTo("foobar");
    assertThat(reporterConfig.getRate()).isEqualTo(180);
    assertThat(reporterConfig.getDryRun()).isTrue();
  }

  @Test
  public void shouldReadCorrectExclusionFilter() {
    PluginConfig globalPluginConfig = emptyGlobalPluginConfig;
    globalPluginConfig.setStringList(
        GerritCloudwatchReporterConfig.KEY_EXCLUDE_METRICS, Arrays.asList("foo.*", ".*bar"));

    when(configFactory.getFromGerritConfig(PLUGIN_NAME)).thenReturn(globalPluginConfig);
    reporterConfig = new GerritCloudwatchReporterConfig(configFactory, PLUGIN_NAME);

    MetricFilter exclusionFilter = reporterConfig.getExclusionFilter();
    assertThat(exclusionFilter.matches("foo/metrics/for/testing", new Counter())).isFalse();
    assertThat(exclusionFilter.matches("some/metrics/for/bar", new Counter())).isFalse();
    assertThat(exclusionFilter.matches("any/other/metric", new Counter())).isTrue();
  }

  @Test
  public void shouldThrowAnExceptionWhenExcludeMetricsRegexIsNotValid() {
    final String INVALID_REGEXP = "[[?";
    PluginConfig globalPluginConfig = emptyGlobalPluginConfig;
    globalPluginConfig.setString(
            GerritCloudwatchReporterConfig.KEY_EXCLUDE_METRICS, INVALID_REGEXP);

    when(configFactory.getFromGerritConfig(PLUGIN_NAME)).thenReturn(globalPluginConfig);

    assertThrows(
            PatternSyntaxException.class,
            () -> {
              new GerritCloudwatchReporterConfig(configFactory, PLUGIN_NAME);
            });
  }
}
