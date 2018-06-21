
rules_scala_version="63eab9f4d80612e918ba954211f377cc83d27a07" # update this as needed

http_archive(
    name = "io_bazel_rules_scala",
    url = "https://github.com/bazelbuild/rules_scala/archive/%s.zip" % rules_scala_version,
    type = "zip",
    strip_prefix= "rules_scala-%s" % rules_scala_version
)

load("@io_bazel_rules_scala//scala:scala.bzl", "scala_repositories")
scala_repositories()

load("@io_bazel_rules_scala//scala:toolchains.bzl", "scala_register_toolchains")
scala_register_toolchains()

maven_jar(
    name = "com_fasterxml_jackson_core_jackson_core",
    artifact = "com.fasterxml.jackson.core:jackson-core:2.9.6",
    server = "maven_uk_server",
)

maven_server(
    name = "maven_uk_server",
    url = "http://uk.maven.org/maven2",
)

maven_jar(
    name = "org_apache_kafka_connect_api",
    artifact = "org.apache.kafka:connect-api:1.1.0",
    server = "maven_uk_server",
)

maven_jar(
    name = "org_apache_kafka_connect_transforms",
    artifact = "org.apache.kafka:connect-transforms:1.1.0",
    server = "maven_uk_server",
)

maven_jar(
    name = "org_apache_kafka_kafka_clients",
    artifact = "org.apache.kafka:kafka-clients:1.1.0",
    server = "maven_uk_server",
)

maven_jar(
    name = "org_slf4j_slf4j_api",
    artifact = "org.slf4j:slf4j-api:1.7.25",
    server = "maven_uk_server",
)
