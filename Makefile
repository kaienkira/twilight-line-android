.PHONY: build debug release clean

build:
	@gradle build

debug:
	@gradle assemble check 

release:
	@gradle assembleRelease check 

clean:
	@gradle clean
