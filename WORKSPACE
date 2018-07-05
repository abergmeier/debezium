
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

new_http_archive(
    name ="com_exasol_jdbc",
    urls = ["https://www.exasol.com/support/secure/attachment/60963/EXASOL_JDBC-6.0.8.tar.gz"],
    strip_prefix = "EXASOL_JDBC-6.0.8",
    build_file_content = """
java_import(
    name = "jar",
    jars = [
        "exajdbc.jar",
    ],
    visibility = ["//visibility:public"],
)
"""
)

maven_jar(
    name = "com_fasterxml_jackson_core_jackson_core",
    artifact = "com.fasterxml.jackson.core:jackson-core:2.9.6",
    server = "maven_uk_server",
)

maven_jar(
    name = "org_everit_osgi_bundles_javax_sql",
    artifact = "org.everit.osgi.bundles:org.everit.osgi.bundles.javax.sql:4.1.0",
    server = "maven_uk_server",
)

maven_jar(
    name = "com_typesafe_akka_akka_actor",
    artifact = "com.typesafe.akka:akka-actor_2.11:2.5.13",
    server = "maven_uk_server",
)

maven_jar(
    name = "com_typesafe_config",
    artifact = "com.typesafe:config:1.3.3",
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
    name = "org_easytesting_fest_assert",
    artifact = "org.easytesting:fest-assert:1.4",
    server = "maven_uk_server",
)

maven_jar(
    name = "org_easytesting_fest_util",
    artifact = "org.easytesting:fest-util:1.2.5",
    server = "maven_uk_server",
)

maven_jar(
    name = "org_slf4j_slf4j_api",
    artifact = "org.slf4j:slf4j-api:1.7.25",
    server = "maven_uk_server",
)
