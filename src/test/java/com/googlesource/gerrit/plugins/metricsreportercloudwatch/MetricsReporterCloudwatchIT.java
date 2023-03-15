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

import static com.google.gerrit.testing.GerritJUnit.assertThrows;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.gerrit.acceptance.LightweightPluginDaemonTest;
import com.google.gerrit.acceptance.TestPlugin;
import com.google.gerrit.acceptance.UseLocalDisk;
import com.google.gerrit.acceptance.config.GerritConfig;
import com.google.gerrit.metrics.Counter0;
import com.google.gerrit.metrics.Description;
import com.google.gerrit.metrics.MetricMaker;
import com.google.inject.Inject;
import java.time.Duration;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;

@UseLocalDisk
@TestPlugin(
    name = "metrics-reporter-cloudwatch",
    sysModule = "com.googlesource.gerrit.plugins.metricsreportercloudwatch.GerritCloudwatchModule")
public class MetricsReporterCloudwatchIT extends LightweightPluginDaemonTest {
  private static final String GERRIT_INSTANCE_ID = "testInstanceId";
  private static final String APPLICATION_NAME = "testApplicationName";
  private static final String TEST_METRIC_NAME = "test/metric/name";
  private static final long TEST_METRIC_INCREMENT = 1234567L;
  private static final String TEST_JVM_METRIC_NAME = "jvm.uptime";
  private static final String TEST_TIMEOUT = "10";
  private static final Duration TEST_TIMEOUT_DURATION =
      Duration.ofSeconds(Integer.valueOf(TEST_TIMEOUT));

  @Inject private MetricMaker metricMaker;
  private Counter0 testCounterMetric;

  @Override
  public void setUpTestPlugin() throws Exception {
    System.setProperty("aws.region", "us-west-1");

    testCounterMetric = metricMaker.newCounter(TEST_METRIC_NAME, new Description("test metric"));

    super.setUpTestPlugin();
  }

  @Test
  @GerritConfig(name = "plugin.metrics-reporter-cloudwatch.dryrun", value = "true")
  public void shouldCloudwatchReporterBeStartedInDryRun() throws Exception {
    InMemoryLoggerAppender dryRunMetricsOutput = newInMemoryLogger();

    waitUntil(() -> dryRunMetricsOutput.metricsStream().anyMatch(l -> l.contains("DRY RUN")));
  }

  @Test
  @GerritConfig(name = "plugin.metrics-reporter-cloudwatch.dryrun", value = "true")
  @GerritConfig(name = "plugin.metrics-reporter-cloudwatch.rate", value = TEST_TIMEOUT)
  public void shouldReportMetricValueToCloudwatch() throws Exception {
    InMemoryLoggerAppender dryRunMetricsOutput = newInMemoryLogger();

    testCounterMetric.incrementBy(TEST_METRIC_INCREMENT);

    waitUntil(
        () ->
            dryRunMetricsOutput
                .metricsStream()
                .filter(l -> l.contains("MetricName=" + TEST_METRIC_NAME))
                .anyMatch(l -> l.contains("Value=" + TEST_METRIC_INCREMENT)));
  }

  @Test
  @GerritConfig(name = "plugin.metrics-reporter-cloudwatch.dryrun", value = "true")
  @GerritConfig(
      name = "plugin.metrics-reporter-cloudwatch.excludeMetrics",
      value = TEST_METRIC_NAME)
  @GerritConfig(name = "plugin.metrics-reporter-cloudwatch.rate", value = TEST_TIMEOUT)
  public void shouldExcludeMetrics() {
    InMemoryLoggerAppender dryRunMetricsOutput = newInMemoryLogger();

    assertThrows(
        InterruptedException.class,
        () -> {
          waitUntil(
              () ->
                  dryRunMetricsOutput
                      .metricsStream()
                      .anyMatch(l -> l.contains("MetricName=" + TEST_METRIC_NAME)));
        });
  }

  @GerritConfig(name = "plugin.metrics-reporter-cloudwatch.dryrun", value = "true")
  @GerritConfig(name = "plugin.metrics-reporter-cloudwatch.jvmMetrics", value = "true")
  @GerritConfig(name = "plugin.metrics-reporter-cloudwatch.rate", value = TEST_TIMEOUT)
  public void shouldReportJVMMetricsToCloudwatch() throws Exception {
    InMemoryLoggerAppender dryRunMetricsOutput = newInMemoryLogger();

    waitUntil(
        () ->
            dryRunMetricsOutput
                .metricsStream()
                .anyMatch(l -> l.contains("MetricName=" + TEST_JVM_METRIC_NAME)));
  }

  @Test
  @GerritConfig(name = "plugin.metrics-reporter-cloudwatch.dryrun", value = "true")
  @GerritConfig(name = "plugin.metrics-reporter-cloudwatch.rate", value = TEST_TIMEOUT)
  public void shouldNotReportJVMMetricsToCloudwatchByDefault() throws Exception {
    InMemoryLoggerAppender dryRunMetricsOutput = newInMemoryLogger();

    assertThrows(
        InterruptedException.class,
        () -> {
          waitUntil(
              () ->
                  dryRunMetricsOutput
                      .metricsStream()
                      .anyMatch(l -> l.contains("MetricName=" + TEST_JVM_METRIC_NAME)));
        });
  }

  @Test
  @GerritConfig(name = "plugin.metrics-reporter-cloudwatch.dryrun", value = "true")
  @GerritConfig(name = "plugin.metrics-reporter-cloudwatch.rate", value = TEST_TIMEOUT)
  @GerritConfig(name = "gerrit.instanceId", value = GERRIT_INSTANCE_ID)
  public void shouldAddInstanceIdAsApplicationNameDimensionWhenAvailable() throws Exception {
    InMemoryLoggerAppender dryRunMetricsOutput = newInMemoryLogger();

    waitUntil(
        () ->
            dryRunMetricsOutput
                .metricsStream()
                .anyMatch(
                    l ->
                        l.contains(
                            String.format("Name=ApplicationName, Value=%s", GERRIT_INSTANCE_ID))));
  }

  @Test
  @GerritConfig(name = "plugin.metrics-reporter-cloudwatch.dryrun", value = "true")
  @GerritConfig(
      name = "plugin.metrics-reporter-cloudwatch.applicationName",
      value = APPLICATION_NAME)
  @GerritConfig(name = "plugin.metrics-reporter-cloudwatch.rate", value = TEST_TIMEOUT)
  @GerritConfig(name = "gerrit.instanceId", value = GERRIT_INSTANCE_ID)
  public void shouldAddApplicationNameConfigValueAsDimensionWhenAvailable() throws Exception {
    InMemoryLoggerAppender dryRunMetricsOutput = newInMemoryLogger();

    waitUntil(
        () ->
            dryRunMetricsOutput
                .metricsStream()
                .anyMatch(
                    l ->
                        l.contains(
                            String.format("Name=ApplicationName, Value=%s", APPLICATION_NAME))));
  }

  private static InMemoryLoggerAppender newInMemoryLogger() {
    InMemoryLoggerAppender dryRunMetricsOutput = new InMemoryLoggerAppender();
    for (Enumeration<?> logger = LogManager.getCurrentLoggers(); logger.hasMoreElements(); ) {
      Logger log = (Logger) logger.nextElement();
      if (log.getName().contains("CloudWatchReporter")) {
        log.addAppender(dryRunMetricsOutput);
        log.setLevel(Level.DEBUG);
      }
    }
    return dryRunMetricsOutput;
  }

  private static void waitUntil(Supplier<Boolean> waitCondition) throws InterruptedException {
    Stopwatch stopwatch = Stopwatch.createStarted();
    while (!waitCondition.get()) {
      if (stopwatch.elapsed().compareTo(TEST_TIMEOUT_DURATION) > 0) {
        throw new InterruptedException();
      }
      MILLISECONDS.sleep(50);
    }
  }

  static class InMemoryLoggerAppender extends AppenderSkeleton {
    private final Splitter metricsDatumSplitter = Splitter.on("MetricDatum");

    private CopyOnWriteArrayList<String> logLines = new CopyOnWriteArrayList<>();

    @Override
    public void close() {}

    @Override
    public boolean requiresLayout() {
      return false;
    }

    @Override
    protected void append(LoggingEvent event) {
      String logMessage = event.getMessage().toString();
      logLines.add(logMessage);
    }

    public Stream<String> metricsStream() {
      return logLines.stream().flatMap(metricsDatumSplitter::splitToStream);
    }
  }
}
