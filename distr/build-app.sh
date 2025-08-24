### Общие параметры сборки
BUILD_ROOT=$(mktemp -d)

function clearBuildRoot() {
  [ -d "${BUILD_ROOT}" ] && rm -rf "${BUILD_ROOT}"
}
trap 'clearBuildRoot' SIGINT

### Сборка подходящего образа JVM
export JAVA_HOME="/opt/jdk-21"
export PATH="${JAVA_HOME}/bin:${PATH}"
BUILD_ROOT_JVM="${BUILD_ROOT}/jvm"
[ ! -d "${BUILD_ROOT_JVM}" ] && mkdir -p "${BUILD_ROOT_JVM}"
JVM_DIR="${BUILD_ROOT_JVM}/jvm"
echo -n "build JVM image... "
jlink \
	--add-modules java.base,java.logging,java.desktop,java.net.http \
	--strip-debug \
	--strip-java-debug-attributes \
	--vm client \
	--output "${JVM_DIR}"

if [ $? -ne 0 ]; then
  echo "fail"
  clearBuildRoot
  exit 1
fi
echo "done"


### Сборка приложения
BUILD_ROOT_APP="${BUILD_ROOT}/app"
[ ! -d "${BUILD_ROOT_APP}" ] && mkdir -p "${BUILD_ROOT_APP}"
# название собираемого приложения
APP_NAME="jira-exported-issue-updater"
# текущая версия jar-ника
APP_VERSION=1.1.1
# путь к jar-нику, который собирается maven-ом
APP_BUILDED_JAR_PATH="target/jira-exported-issue-updater-java-${APP_VERSION}-jar-with-dependencies.jar"

[ -d "${APP_NAME}" ] && rm -rf "${APP_NAME}"

echo -n "build application jar... "
mvn -q clean package
[ $? -ne 0 ] && echo "fail" && clearBuildRoot && exit 1
echo "done"
echo -n "build application package... "
mv "${APP_BUILDED_JAR_PATH}" "${BUILD_ROOT_APP}/jira-exported-issue-updater-${APP_VERSION}.jar" \
&& jpackage \
	--type app-image \
	-i "${BUILD_ROOT_APP}" \
	-n "${APP_NAME}" \
	--runtime-image "${JVM_DIR}" \
	--main-class org.samearch.jira.util.export.ExportedIssuesUpdater \
	--main-jar jira-exported-issue-updater-${APP_VERSION}.jar
[ $? -ne 0 ] && echo "fail" && clearBuildRoot && exit 1
echo "done"

clearBuildRoot