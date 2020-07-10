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

import org.eclipse.jgit.lib.Config;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class GerritCloudwatchReporterConfigTest {

  GerritCloudwatchReporterConfig reporterConfig;

  @Test
  public void shoudGetAllDefaultsWhenConfigurationIsEmpty() {
    reporterConfig = new GerritCloudwatchReporterConfig(new Config());

    assertThat(reporterConfig.getInitialDelay())
        .isEqualTo(GerritCloudwatchReporterConfig.DEFAULT_INITIAL_DELAY_SECS);
    assertThat(reporterConfig.getNamespace())
        .isEqualTo(GerritCloudwatchReporterConfig.DEFAULT_NAMESPACE);
    assertThat(reporterConfig.getRate())
        .isEqualTo(GerritCloudwatchReporterConfig.DEFAULT_RATE_SECS);
  }

  @Test
  public void shoudReadValuesFromConfiguration() {

    final Config pluginConfig = new Config();
    pluginConfig.setString(
        GerritCloudwatchReporterConfig.SECTION_CLOUDWATCH,
        null,
        GerritCloudwatchReporterConfig.KEY_NAMESPACE,
        "foobar");
    pluginConfig.setString(
        GerritCloudwatchReporterConfig.SECTION_CLOUDWATCH,
        null,
        GerritCloudwatchReporterConfig.KEY_RATE,
        "3m");
    pluginConfig.setString(
        GerritCloudwatchReporterConfig.SECTION_CLOUDWATCH,
        null,
        GerritCloudwatchReporterConfig.KEY_INITIAL_DELAY,
        "20s");

    reporterConfig = new GerritCloudwatchReporterConfig(pluginConfig);

    assertThat(reporterConfig.getInitialDelay())
        .isEqualTo(20);
    assertThat(reporterConfig.getNamespace())
        .isEqualTo("foobar");
    assertThat(reporterConfig.getRate())
        .isEqualTo(180);
  }
}
