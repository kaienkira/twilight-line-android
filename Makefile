.PHONY: build debug release clean

build:
	@gradle build

debug:
	@gradle assemble check 

release:
	@gradle assembleRelease check 

clean:
	@gradle clean

install:
	@adb install -r app/build/outputs/apk/release/app-release.apk

transfer:
	sz -bey app/build/outputs/apk/release/app-release.apk
