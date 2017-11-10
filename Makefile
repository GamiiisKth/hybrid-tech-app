VERSION:=$(shell xmlstarlet sel -N x="http://www.w3.org/ns/widgets" -T -t -m "//x:widget" -v @version config.xml)
GIT_VERSION=$(shell ./git-version.sh)

NEXUS_PARAMS=-e NEXUS_CREDENTIALS_USR=$$NEXUS_CREDENTIALS_USR -e NEXUS_CREDENTIALS_PSW=$$NEXUS_CREDENTIALS_PSW 
HOCKEY_PARAMS=-e HOCKEY_API_TOKEN=$$HOCKEY_API_TOKEN
GRADLE_PARAMS=-v $$HOME/.gradle:/root/.gradle
SSH_PARAMS=-v $$HOME/.ssh:/root/.ssh:ro -v $$SSH_AUTH_SOCK:/ssh-agent -e SSH_AUTH_SOCK=/ssh-agent
WORKSPACE_PARAMS=-v $(PWD):/workspace -w /workspace
BUILDER=docker-registry.electrolux.io/edp/hybrid-android-builder:7.1.0-769f4cf

define DOCKER_RUN
docker run -i --rm ${NEXUS_PARAMS} ${HOCKEY_PARAMS} ${GRADLE_PARAMS} ${SSH_PARAMS} ${WORKSPACE_PARAMS} -e APP_VERSION=$$APP_VERSION ${BUILDER}
endef

NPM:=${DOCKER_RUN} npm
CORDOVA:=${DOCKER_RUN} cordova
RM:=${DOCKER_RUN} rm
FASTLANE:=${DOCKER_RUN} fastlane
ADD:=${DOCKER_RUN}
prepare:
	${NPM} install
	${CORDOVA} platform add android --verbose --nofetch

build-android:
ifndef BUILD_NUMBER
	$(error BUILD_NUMBER is not set)
endif
	${CORDOVA} build android -- --gradleArg=-PcdvVersionCode=$$BUILD_NUMBER --verbose

build-run-android:
	rm -rf platforms/android/build/outputs/apk/*
	cordova build android
	mv platforms/android/build/outputs/apk/debug/android-debug.apk platforms/android/build/outputs/apk/
	rm -rf platforms/android/build/outputs/apk/debug/
	cordova  run android --nobuild

publish-android: export APP_VERSION=${VERSION}-${GIT_VERSION} 
publish-android:
	${FASTLANE} android publish

beta-android: export APP_VERSION=${VERSION}-${GIT_VERSION} 
beta-android:
	${FASTLANE} android beta

build-ios:
	echo('hallo')
list-plugins:
	${CORDOVA} plugin ls

add-device:
	
	${ADD} --device=/dev/tty.SAMSUNG_MDM

clean:
	${RM} -rf plugins/
	${RM} -rf node_modules/
	${RM} -rf platforms/
