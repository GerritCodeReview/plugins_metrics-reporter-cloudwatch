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
import org.eclipse.jgit.lib.Config;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

class GerritCloudwatchReporterConfig {

    protected static final String SECTION_CLOUDWATCH = "cloudwatch";

    protected static final String KEY_NAMESPACE = "namespace";
    protected static final String KEY_RATE = "rate";
    protected static final String KEY_INITIAL_DELAY = "initialDelay";

    protected static final String DEFAULT_NAMESPACE = "gerrit";
    protected static final Integer DEFAULT_RATE_SECS = 60;
    protected static final Integer DEFAULT_INITIAL_DELAY_SECS = 0;

    private final int rate;
    private final String namespace;
    private final int initialDelay;

    public GerritCloudwatchReporterConfig(Config pluginConfig) {
        this.namespace =
                Optional.ofNullable(pluginConfig.getString(SECTION_CLOUDWATCH, null, KEY_NAMESPACE))
                        .orElse(DEFAULT_NAMESPACE);
        this.rate =
                (int)
                        ConfigUtil.getTimeUnit(
                                pluginConfig,
                                SECTION_CLOUDWATCH,
                                null,
                                KEY_RATE,
                                DEFAULT_RATE_SECS,
                                TimeUnit.SECONDS);

        this.initialDelay =
                (int)
                        ConfigUtil.getTimeUnit(
                                pluginConfig,
                                SECTION_CLOUDWATCH,
                                null,
                                KEY_INITIAL_DELAY,
                                DEFAULT_INITIAL_DELAY_SECS,
                                TimeUnit.SECONDS);
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
}
