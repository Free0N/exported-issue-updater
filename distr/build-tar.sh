#!/bin/bash

APP_VERSION=2.0.1

BUILD_ROOT=$(mktemp -d)
DIST_DIR="${BUILD_ROOT}/jira-exported-issue-updater-${APP_VERSION}"
mkdir -p "${DIST_DIR}"

echo -n "Try to build project... "
mvn -q clean package
[ $? -ne 0 ] && echo "fail" && exit 1
echo "done"

cp -R issueUpdateConfig.yaml.example README.md updaters/ "${DIST_DIR}"
cp target/jira-exported-issue-updater-java-${APP_VERSION}-jar-with-dependencies.jar "${DIST_DIR}/jira-exported-issue-updater-${APP_VERSION}.jar"
tar -czf ./jira-exported-issue-updater-${APP_VERSION}.tar.gz -C "${BUILD_ROOT}" "jira-exported-issue-updater-${APP_VERSION}/"