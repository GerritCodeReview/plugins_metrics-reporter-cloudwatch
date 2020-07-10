load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
  maven_jar(
    name = 'dropwizard_metrics_cloudwatch',
    artifact = 'io.github.azagniotov:dropwizard-metrics-cloudwatch:2.0.5',
    sha1 = '7fed805d8fb31e54d75597a2b4f5b958eecad0ab',
  )

  maven_jar(
    name = 'dropwizard_metrics_jvm',
    artifact = 'io.dropwizard.metrics:metrics-jvm:4.1.10.1',
    sha1 = '88d9e476c2944a9f0158474dc2b7064c96e26317',
  )

  maven_jar(
    name = 'reactivestreams',
    artifact = 'org.reactivestreams:reactive-streams:1.0.3',
    sha1 = 'd9fb7a7926ffa635b3dcaa5049fb2bfa25b3e7d0',
  )

  maven_jar(
    name = 'awssdk_cloudwatch',
    artifact = 'software.amazon.awssdk:cloudwatch:2.13.54',
    sha1 = '46e4f3dd21f1b6a61f08ae7195f0f025e207af5f',
  )

  maven_jar(
    name = 'awssdk_auth',
    artifact = 'software.amazon.awssdk:auth:2.13.54',
    sha1 = '68b522302874b580ecd5563fe58e492136a24c81',
  )

  maven_jar(
    name = 'awssdk_sdk_core',
    artifact = 'software.amazon.awssdk:sdk-core:2.13.54',
    sha1 = '10163b0cbe76600891e74516b958ee7628e70e2a',
  )

  maven_jar(
    name = 'awssdk_aws_core',
    artifact = 'software.amazon.awssdk:aws-core:2.13.54',
    sha1 = '356c0c26afa7fb2a1edb921fc16e9de6a533f559',
  )

  maven_jar(
    name = 'awssdk_profiles',
    artifact = 'software.amazon.awssdk:profiles:2.13.54',
    sha1 = '705146ffaa1aab5442791d57238b4ba304787e16',
  )

  maven_jar(
    name = 'awssdk_regions',
    artifact = 'software.amazon.awssdk:regions:2.13.54',
    sha1 = '0de5b12c51d5332720fcb32581af3bd3ff88b21c',
  )

  maven_jar(
    name = 'awssdk_metrics_spi',
    artifact = 'software.amazon.awssdk:metrics-spi:2.13.54',
    sha1 = '0610a43ba773be1a05fdf05416bb671d0eaa9916',
  )

  maven_jar(
    name = 'awssdk_utils',
    artifact = 'software.amazon.awssdk:utils:2.13.54',
    sha1 = 'dae698acd98027117a13550f106b1ea6539d076a',
  )

  maven_jar(
    name = 'awssdk_http_client_spi',
    artifact = 'software.amazon.awssdk:http-client-spi:2.13.54',
    sha1 = 'bdecd06aa2793f1366b1eeaa385314f22336fddb',
  )

  maven_jar(
    name = 'awssdk_query_protocol',
    artifact = 'software.amazon.awssdk:aws-query-protocol:2.13.54',
    sha1 = '2145252d0352b89ee59d3a4fab6b6ea03b032eb1',
  )

  maven_jar(
    name = 'awssdk_protocol_core',
    artifact = 'software.amazon.awssdk:protocol-core:2.13.54',
    sha1 = '28bc71e1450dd1515a251d74511652ccf0f2af01',
  )


  maven_jar(
    name = 'awssdk_netty_nio_client',
    artifact = 'software.amazon.awssdk:netty-nio-client:2.13.54',
    sha1 = '03de0e583e6b244743915640fa9d00d68c5fd077',
  )

  maven_jar(
    name = 'io_netty_all',
    artifact = 'io.netty:netty-all:4.1.51.Final',
    sha1 = '5e5f741acc4c211ac4572c31c7e5277ec465e4e4',
  )

  maven_jar(
      name = 'aws_java_sdk_core',
      artifact = 'com.amazonaws:aws-java-sdk-core:1.11.820',
      sha1 = '8902eefbbcd087a89e57a3e88c8e383ed0d7bab9',
    )

  maven_jar(
      name = 'jackson_core',
      artifact = 'com.fasterxml.jackson.core:jackson-core:2.11.1',
      sha1 = '8b02908d53183fdf9758e7e20f2fdee87613a962',
    )

  maven_jar(
      name = 'jackson_annotations',
      artifact = 'com.fasterxml.jackson.core:jackson-annotations:2.11.1',
      sha1 = 'f083c4ac0fb8b3c6b8d5b62cd54122228ef62cee',
    )

  maven_jar(
      name = 'jackson_databind',
      artifact = 'com.fasterxml.jackson.core:jackson-databind:2.11.1',
      sha1 = 'f5d24a1dcf46000316d40c8c61196c48dd5677c5',
    )
