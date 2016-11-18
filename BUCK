include_defs('//bucklets/gerrit_plugin.bucklet')
include_defs('//lib/maven.defs')

gerrit_plugin(
  name = 'metrics-reporter-cloudwatch',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/resources/**/*']),
  deps = [
    '//lib/dropwizard:dropwizard-core',
    ':metrics-cloudwatch'
  ],
  manifest_entries = [
    'Gerrit-PluginName: metrics-reporter-cloudwatch',
  ],
)

maven_jar(
  name = 'metrics-cloudwatch',
  id = 'com.blacklocus:metrics-cloudwatch:0.4.0',
  sha1 = '6a447b695294d27cb85d2a60b9b2eae2f3d8c780',
  license = 'Apache2.0',
)

# this is required for bucklets/tools/eclipse/project.py to work
java_library(
  name = 'classpath',
  deps = [':metrics-reporter-cloudwatch__plugin'],
)

