.PHONY: default debug release bundle clean install transfer

default: release

debug:
	@gradle assemble check 

release:
	@gradle assembleRelease check 

bundle:
	@gradle bundleRelease

clean:
	@gradle clean

install:
	@adb install -r app/build/outputs/apk/release/app-release.apk

transfer:
	sz -bey app/build/outputs/apk/release/app-release.apk
