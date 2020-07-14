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

import com.google.gerrit.server.config.PluginConfig;
import org.eclipse.jgit.lib.Config;
import org.junit.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;

import java.util.Arrays;
import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;
import static com.google.gerrit.testing.GerritJUnit.assertThrows;

public class GerritCloudwatchReporterConfigTest {
  private final String pluginName = "metrics-reporter-cloudwatch";
  private final Config emptyGlobalPluginConfig = new Config();
  private final PluginConfig emptyGerritConfig =
      new PluginConfig(pluginName, emptyGlobalPluginConfig);

  GerritCloudwatchReporterConfig reporterConfig;

  @Test
  public void shouldGetAllDefaultsWhenConfigurationIsEmpty() {
    reporterConfig = new GerritCloudwatchReporterConfig(emptyGlobalPluginConfig, emptyGerritConfig);

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
    Config globalPluginConfig = emptyGlobalPluginConfig;
    globalPluginConfig.setString(
        GerritCloudwatchReporterConfig.SECTION_CLOUDWATCH,
        null,
        GerritCloudwatchReporterConfig.KEY_NAMESPACE,
        "foobar");
    globalPluginConfig.setString(
        GerritCloudwatchReporterConfig.SECTION_CLOUDWATCH,
        null,
        GerritCloudwatchReporterConfig.KEY_RATE,
        "3m");
    globalPluginConfig.setString(
        GerritCloudwatchReporterConfig.SECTION_CLOUDWATCH,
        null,
        GerritCloudwatchReporterConfig.KEY_INITIAL_DELAY,
        "20s");
    globalPluginConfig.setBoolean(
        GerritCloudwatchReporterConfig.SECTION_CLOUDWATCH,
        null,
        GerritCloudwatchReporterConfig.KEY_DRYRUN,
        true);

    reporterConfig = new GerritCloudwatchReporterConfig(globalPluginConfig, emptyGerritConfig);

    assertThat(reporterConfig.getInitialDelay()).isEqualTo(20);
    assertThat(reporterConfig.getNamespace()).isEqualTo("foobar");
    assertThat(reporterConfig.getRate()).isEqualTo(180);
    assertThat(reporterConfig.getDryRun()).isTrue();
  }

  @Test
  public void shouldReadCredentialsWhenTheyAreDefined() {
    PluginConfig gerritConfig = emptyGerritConfig;

    gerritConfig.setString(GerritCloudwatchReporterConfig.KEY_SECRET_KEY_ID, "mySecretId");
    gerritConfig.setString(GerritCloudwatchReporterConfig.KEY_ACCESS_KEY_ID, "myAccessId");

    reporterConfig = new GerritCloudwatchReporterConfig(emptyGlobalPluginConfig, gerritConfig);

    assertThat(reporterConfig.getMaybeAWSCredentials())
        .isEqualTo(Optional.of(AwsBasicCredentials.create("myAccessId", "mySecretId")));
  }

  @Test
  public void shouldThrowWhenCredentialsArePartiallyDefined() {
    PluginConfig gerritConfig = emptyGerritConfig;

    gerritConfig.setString(GerritCloudwatchReporterConfig.KEY_SECRET_KEY_ID, "mySecretId");
    gerritConfig.setString(GerritCloudwatchReporterConfig.KEY_ACCESS_KEY_ID, null);

    assertThrows(
        IllegalStateException.class,
        () -> new GerritCloudwatchReporterConfig(emptyGlobalPluginConfig, gerritConfig));
  }

  @Test
  public void shouldReadRegionFromConfiguration() {
    PluginConfig gerritConfig = emptyGerritConfig;

    gerritConfig.setString(GerritCloudwatchReporterConfig.KEY_REGION, "eu-west-1");

    reporterConfig = new GerritCloudwatchReporterConfig(emptyGlobalPluginConfig, gerritConfig);

    assertThat(reporterConfig.getMaybeAWSRegion()).isEqualTo(Optional.of("eu-west-1"));
  }

  @Test
  public void shouldReadCorrectExclusionFilter() {
    Config globalPluginConfig = emptyGlobalPluginConfig;
    globalPluginConfig.setStringList(
        GerritCloudwatchReporterConfig.SECTION_CLOUDWATCH,
        null,
        GerritCloudwatchReporterConfig.KEY_EXCLUDE_METRICS,
        Arrays.asList("foo.*", ".*bar"));

    reporterConfig = new GerritCloudwatchReporterConfig(globalPluginConfig, emptyGerritConfig);

    assertThat(reporterConfig.getExclusionFilter().test("foo/metrics/for/testing")).isTrue();
    assertThat(reporterConfig.getExclusionFilter().test("foo/metrics/for/bar")).isTrue();
    assertThat(reporterConfig.getExclusionFilter().test("any/other/metric")).isFalse();
  }
}
