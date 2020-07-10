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

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.lifecycle.LifecycleModule;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.Inject;
import com.google.inject.Scopes;
import org.eclipse.jgit.lib.Config;

public class GerritCloudwatchModule extends LifecycleModule {

  final GerritCloudwatchReporterConfig config;

  @Inject
  GerritCloudwatchModule(PluginConfigFactory configFactory, @PluginName String pluginName) {
    Config globalPluginConfig = configFactory.getGlobalPluginConfig(pluginName);

    this.config = new GerritCloudwatchReporterConfig(globalPluginConfig);
  }

  @Override
  protected void configure() {
    bind(GerritCloudwatchReporterConfig.class).toInstance(config);
    listener().to(GerritCloudwatchReporter.class).in(Scopes.SINGLETON);
  }
}
