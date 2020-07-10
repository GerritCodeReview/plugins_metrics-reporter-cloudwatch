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

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.flogger.FluentLogger;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.events.LifecycleListener;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.Inject;
import io.github.azagniotov.metrics.reporter.cloudwatch.CloudWatchReporter;
import io.github.azagniotov.metrics.reporter.cloudwatch.CloudWatchReporter.Percentile;
import org.eclipse.jgit.lib.Config;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;

import java.util.concurrent.TimeUnit;

public class GerritCloudwatchReporter implements LifecycleListener {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private final CloudWatchReporter cloudWatchReporter;

  @Inject
  public GerritCloudwatchReporter(
      PluginConfigFactory configFactory, @PluginName String pluginName, MetricRegistry registry) {

    Config config = configFactory.getGlobalPluginConfig(pluginName);

    SdkAsyncHttpClient httpClient =
        NettyNioAsyncHttpClient.builder()
            .maxConcurrency(100)
            .maxPendingConnectionAcquires(10_000)
            .build();

    final CloudWatchAsyncClient amazonCloudWatchAsync =
        CloudWatchAsyncClient.builder()
                .region(Region.US_EAST_1)
                .httpClient(httpClient)
                .build();

    cloudWatchReporter =
        CloudWatchReporter.forRegistry(registry, amazonCloudWatchAsync, "gerrit")
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .filter(MetricFilter.ALL)
            .withPercentiles(Percentile.P99)
            .withOneMinuteMeanRate()
            .withFiveMinuteMeanRate()
            .withFifteenMinuteMeanRate()
            .withMeanRate()
            .withArithmeticMean()
            .withStdDev()
            .withStatisticSet()
            .withZeroValuesSubmission()
            .withReportRawCountValue()
            .withHighResolution()
            .withMeterUnitSentToCW(StandardUnit.BYTES)
            .withJvmMetrics()
            .withGlobalDimensions("Info=testing")
            .withDryRun()
            .build();
  }

  @Override
  public void start() {
    if (cloudWatchReporter != null) {
      logger.atInfo().log("Starting CloudWatch reporter");
      cloudWatchReporter.start(0, 1, TimeUnit.MINUTES);
    } else {
      logger.atSevere().log("Attempt to start null reporter");
    }
  }

  @Override
  public void stop() {
    if (cloudWatchReporter != null) {
      logger.atInfo().log("Stopping CloudWatch reporter");
      cloudWatchReporter.stop();
    }
  }
}
