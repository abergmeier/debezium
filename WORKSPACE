
rules_scala_version="a89d44f7ef67d93dedfc9888630f48d7723516f7" # update this as needed

http_archive(
    name = "io_bazel_rules_scala",
    url = "https://github.com/bazelbuild/rules_scala/archive/%s.zip"%rules_scala_version,
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
    name = "com_fasterxml_jackson_core_jackson_annotations",
    artifact = "com.fasterxml.jackson.core:jackson-annotations:2.9.6",
    server = "maven_uk_server",
)

maven_jar(
    name = "com_fasterxml_jackson_core_jackson_core",
    artifact = "com.fasterxml.jackson.core:jackson-core:2.9.6",
    server = "maven_uk_server",
)

maven_jar(
    name = "com_fasterxml_jackson_core_jackson_databind",
    artifact = "com.fasterxml.jackson.core:jackson-databind:2.9.6",
    server = "maven_uk_server",
)

maven_jar(
    name = "org_everit_osgi_bundles_javax_sql",
    artifact = "org.everit.osgi.bundles:org.everit.osgi.bundles.javax.sql:4.1.0",
    server = "maven_uk_server",
)

maven_jar(
    name = "com_thoughtworks_paranamer_paranamer",
    artifact = "com.thoughtworks.paranamer:paranamer:2.8",
    server = "maven_uk_server",
)

maven_jar(
    name = "com_typesafe_akka_akka_actor",
    artifact = "com.typesafe.akka:akka-actor_2.11:2.5.13",
    server = "maven_uk_server",
)

maven_jar(
    name = "com_typesafe_akka_akka_http_core",
    artifact = "com.typesafe.akka:akka-http-core_2.11:10.1.4",
    server = "maven_uk_server",
)

maven_jar(
    name = "com_typesafe_scala_logging",
    artifact = "com.typesafe.scala-logging:scala-logging_2.11:3.9.0",
    server = "maven_uk_server",
)

maven_jar(
    name = "com_typesafe_akka_parsing",
    artifact = "com.typesafe.akka:akka-parsing_2.11:10.1.4",
    server = "maven_uk_server",
)

maven_jar(
    name = "com_typesafe_akka_akka_stream",
    artifact = "com.typesafe.akka:akka-stream_2.11:2.5.13",
    server = "maven_uk_server",
)

maven_jar(
    name = "com_typesafe_akka_akka_stream_testkit",
    artifact = "com.typesafe.akka:akka-stream-testkit_2.11:2.5.16",
    server = "maven_uk_server",
)

maven_jar(
    name = "com_typesafe_akka_akka_testkit",
    artifact = "com.typesafe.akka:akka-testkit_2.11:2.5.9",
    server = "maven_uk_server",
)

maven_jar(
    name = "com_typesafe_config",
    artifact = "com.typesafe:config:1.3.3",
    server = "maven_uk_server",
)

maven_jar(
    name = "com_typesafe_ssl_config_core",
    artifact = "com.typesafe:ssl-config-core_2.11:0.2.4",
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
    name = "org_json4s_json4s_ast",
    artifact = "org.json4s:json4s-ast_2.11:3.6.1",
    server = "maven_uk_server",
)

maven_jar(
    name = "org_json4s_json4s_core",
    artifact = "org.json4s:json4s-core_2.11:3.6.1",
    server = "maven_uk_server",
)

maven_jar(
    name = "org_json4s_json4s_jackson",
    artifact = "org.json4s:json4s-jackson_2.11:3.6.1",
    server = "maven_uk_server",
)

maven_jar(
    name = "org_json4s_json4s_scalap",
    artifact = "org.json4s:json4s-scalap_2.11:3.6.1",
    server = "maven_uk_server",
)

maven_jar(
    name = "org_reactivestreams_reactive_streams",
    artifact = "org.reactivestreams:reactive-streams:1.0.2",
    server = "maven_uk_server",
)

maven_jar(
    name = "org_scala_lang_modules_scala_parser_combinators",
    artifact = "org.scala-lang.modules:scala-parser-combinators_2.11:1.1.1",
    server = "maven_uk_server",
)

maven_jar(
    name = "org_slf4j_slf4j_api",
    artifact = "org.slf4j:slf4j-api:1.7.25",
    server = "maven_uk_server",
)
