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
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.time.Duration;
import java.util.Enumeration;
import java.util.function.Supplier;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Stopwatch;
import com.google.gerrit.acceptance.LightweightPluginDaemonTest;
import com.google.gerrit.acceptance.TestPlugin;
import com.google.gerrit.acceptance.UseLocalDisk;
import com.google.gerrit.acceptance.config.GerritConfig;
import com.google.gerrit.metrics.Description;
import com.google.gerrit.metrics.MetricMaker;
import com.google.inject.Inject;

@UseLocalDisk
@TestPlugin(
    name = "metrics-reporter-cloudwatch",
    sysModule = "com.googlesource.gerrit.plugins.metricsreportercloudwatch.GerritCloudwatchModule")
public class MetricsReporterCloudwatchIT extends LightweightPluginDaemonTest {
	private static final String TEST_METRIC_NAME = "test/metric/name";
	private static final String TEST_TIMEOUT = "10";
	private static final Integer TEST_METRIC_VALUE = 1000;
	private static final Duration TEST_TIMEOUT_DURATION = Duration.ofSeconds(Integer.valueOf(TEST_TIMEOUT));

	private GerritCloudwatchReporter cloudwatchReporter;
	
	@Inject private MetricMaker metricMaker;
	
	@Override
	public void setUpTestPlugin() throws Exception {
		metricMaker.newConstantMetric(TEST_METRIC_NAME, TEST_METRIC_VALUE, new Description("test metric"));

		
		super.setUpTestPlugin();
	}

  @Before
  public void setup() throws Exception {
	  cloudwatchReporter = plugin.getSysInjector().getInstance(GerritCloudwatchReporter.class);
  }

  @Test
  public void shouldCloudwatchReporterBeStarted() throws Exception {
	  assertThat(cloudwatchReporter.isStarted()).isTrue();
  }
  
  @Test
  @GerritConfig( name = "plugin.metrics-reporter-cloudwatch.dryrun", value = "true")
  @GerritConfig( name = "plugin.metrics-reporter-cloudwatch.rate", value = TEST_TIMEOUT)
  public void shouldReportMetricValueToCloudwatch() throws Exception {
	  InMemoryLoggerAppender dryRunMetricsOutput = new InMemoryLoggerAppender();
		for (Enumeration<?> logger = LogManager.getCurrentLoggers(); logger.hasMoreElements();) {
			Logger log = (Logger) logger.nextElement();
			if (log.getName().contains("CloudWatchReporter")) {
				log.addAppender(dryRunMetricsOutput);
				log.setLevel(Level.DEBUG);
			}
		}

		assertThat(cloudwatchReporter.isDryRun()).isTrue();
		Thread.sleep(5000L);
		waitUntil(() -> dryRunMetricsOutput.getResult().contains("MetricName=" + TEST_METRIC_NAME));
  }
  
  private static void waitUntil(Supplier<Boolean> waitCondition)
	      throws InterruptedException {
	    Stopwatch stopwatch = Stopwatch.createStarted();
	    while (!waitCondition.get()) {
	      if (stopwatch.elapsed().compareTo(TEST_TIMEOUT_DURATION) > 0) {
	        throw new InterruptedException();
	      }
	      MILLISECONDS.sleep(50);
	    }
	  }
  
  static class InMemoryLoggerAppender extends AppenderSkeleton {

	    private StringBuilder builder = new StringBuilder();

	    @Override
	    public void close() {
	        // nothing to close
	    }

	    @Override
	    public boolean requiresLayout() {
	        return false;
	    }

	    @Override
	    protected void append(LoggingEvent event) {
	    	String logMessage = event.getMessage().toString() + "\n";
	        builder.append(logMessage);
	    }

	    public String getResult() {
	        return builder.toString();
	    }
	}
}
