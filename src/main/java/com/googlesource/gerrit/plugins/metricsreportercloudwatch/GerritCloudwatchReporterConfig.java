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

import com.google.gerrit.server.config.ConfigUtil;
import com.google.gerrit.server.config.PluginConfig;
import org.eclipse.jgit.lib.Config;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

class GerritCloudwatchReporterConfig {

  protected static final String SECTION_CLOUDWATCH = "cloudwatch";

  protected static final String KEY_NAMESPACE = "namespace";
  protected static final String KEY_RATE = "rate";
  protected static final String KEY_DRYRUN = "dryRun";
  protected static final String KEY_INITIAL_DELAY = "initialDelay";
  protected static final String KEY_ACCESS_KEY_ID = "accessKeyId";
  protected static final String KEY_SECRET_KEY_ID = "secretKeyId";
  protected static final String KEY_REGION = "region";

  protected static final String DEFAULT_NAMESPACE = "gerrit";
  protected static final Boolean DEFAULT_DRY_RUN = false;
  protected static final Integer DEFAULT_RATE_SECS = 60;
  protected static final Integer DEFAULT_INITIAL_DELAY_SECS = 0;

  private final int rate;
  private final String namespace;
  private final int initialDelay;
  private final Boolean dryRun;
  private final Optional<String> maybeAWSRegion;
  private final Optional<AwsBasicCredentials> maybeAWSCredentials;

  public GerritCloudwatchReporterConfig(Config globalPluginConfig, PluginConfig gerritConfig)
      throws IllegalStateException {

    this.maybeAWSRegion = Optional.ofNullable(gerritConfig.getString(KEY_REGION));
    this.maybeAWSCredentials = maybeAWSCredentials(gerritConfig);

    this.namespace =
        Optional.ofNullable(globalPluginConfig.getString(SECTION_CLOUDWATCH, null, KEY_NAMESPACE))
            .orElse(DEFAULT_NAMESPACE);

    this.dryRun =
            globalPluginConfig.getBoolean(SECTION_CLOUDWATCH, null, KEY_DRYRUN, DEFAULT_DRY_RUN);

    this.rate =
        (int)
            ConfigUtil.getTimeUnit(
                globalPluginConfig,
                SECTION_CLOUDWATCH,
                null,
                KEY_RATE,
                DEFAULT_RATE_SECS,
                TimeUnit.SECONDS);

    this.initialDelay =
        (int)
            ConfigUtil.getTimeUnit(
                globalPluginConfig,
                SECTION_CLOUDWATCH,
                null,
                KEY_INITIAL_DELAY,
                DEFAULT_INITIAL_DELAY_SECS,
                TimeUnit.SECONDS);
  }

  private Optional<AwsBasicCredentials> maybeAWSCredentials(PluginConfig gerritConfig)
      throws IllegalStateException {
    String accessId = gerritConfig.getString(KEY_ACCESS_KEY_ID);
    String secretId = gerritConfig.getString(KEY_SECRET_KEY_ID);

    if (accessId != null && secretId != null) {
      return Optional.of(AwsBasicCredentials.create(accessId, secretId));
    } else if (accessId != null || secretId != null) {
      throw new IllegalStateException(
          String.format(
              "Either none or both '%s' and '%s' must be defined, but only one was defined.",
              KEY_ACCESS_KEY_ID, KEY_SECRET_KEY_ID));
    }
    return Optional.empty();
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

  public Optional<AwsBasicCredentials> getMaybeAWSCredentials() {
    return maybeAWSCredentials;
  }

  public Optional<String> getMaybeAWSRegion() {
    return maybeAWSRegion;
  }

  public Boolean getDryRun() {
    return dryRun;
  }
}
