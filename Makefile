.PHONY: default debug release check bundle clean install transfer

default: release

debug:
	@gradle assemble

release:
	@gradle assembleRelease

check:
	@gradle check

bundle:
	@gradle bundleRelease

clean:
	@gradle clean
	@rm -rf .gradle
	@rm -rf build

install:
	@adb install -r app/build/outputs/apk/release/app-release.apk

transfer:
	tsz -y app/build/outputs/apk/release/app-release.apk
