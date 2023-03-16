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

import com.codahale.metrics.MetricRegistry;
import com.google.common.flogger.FluentLogger;
import com.google.gerrit.extensions.events.LifecycleListener;
import com.google.inject.Inject;
import io.github.azagniotov.metrics.reporter.cloudwatch.CloudWatchReporter;
import java.util.concurrent.TimeUnit;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClientBuilder;

public class GerritCloudwatchReporter implements LifecycleListener {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private final CloudWatchReporter cloudWatchReporter;
  private final GerritCloudwatchReporterConfig config;

  @Inject
  public GerritCloudwatchReporter(GerritCloudwatchReporterConfig config, MetricRegistry registry)
      throws IllegalStateException {
    this.config = config;

    final CloudWatchAsyncClientBuilder cloudWatchAsyncClientBuilder =
        CloudWatchAsyncClient.builder();

    CloudWatchReporter.Builder cloudWatchReporterBuilder =
        CloudWatchReporter.forRegistry(
                registry, cloudWatchAsyncClientBuilder.build(), config.getNamespace())
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .filter(config.getExclusionFilter())
            .withZeroValuesSubmission()
            .withReportRawCountValue()
            .withHighResolution();

    config
        .getMaybeInstanceId()
        .ifPresent(
            instanceId ->
                cloudWatchReporterBuilder.withGlobalDimensions(
                    String.format("InstanceId=%s", instanceId)));

    if (config.getDryRun()) {
      cloudWatchReporterBuilder.withDryRun();
    }

    if (config.getJvmMetrics()) {
      cloudWatchReporterBuilder.withJvmMetrics();
    }

    cloudWatchReporter = cloudWatchReporterBuilder.build();
  }

  @Override
  public void start() {
    logger.atInfo().log(
        "Reporting to CloudWatch [namespace:'%s'] at rate of '%d' seconds, after initial delay of %d seconds",
        config.getNamespace(), config.getRate(), config.getInitialDelay());
    cloudWatchReporter.start(config.getInitialDelay(), config.getRate(), TimeUnit.SECONDS);
  }

  @Override
  public void stop() {
    cloudWatchReporter.stop();
  }
}
