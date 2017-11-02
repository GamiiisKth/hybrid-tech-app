if [ -d "platforms/android" ]; then
  echo "OVERWRITING GENERATED build.gradle IN PROJECT, AS LONG AS WE DO NOT FIND A BETTER WAY"
  cp resources/build.gradle platforms/android/build.gradle
  cp resources/GradleBuilder.js platforms/android/cordova/lib/builders/GradleBuilder.js
fi
