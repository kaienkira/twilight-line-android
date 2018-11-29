#==============================================================================
#-*- CONFIGS -*- 
#==============================================================================
CFG_ANDROID_SDK_BUILD_TOOL = 28.0.3
CFG_ANDROID_SDK_PLATFORM = android-28
CFG_APK_NAME = twilight-line-android
CFG_PACKAGE_NAME = com.brickredstudio.twilightline

#==============================================================================
#-*- FUNCTIONS -*-
#==============================================================================
define ECHO
	@printf "\033[;32m"; printf $1; printf "\033[0m\n"
endef

#==============================================================================
#-*- MAIN -*- 
#==============================================================================
# define tools
ANDROID_SDK_BUILD_TOOL_DIR = \
	$(ANDROID_SDK_HOME)/build-tools/$(CFG_ANDROID_SDK_BUILD_TOOL)
ANDROID_SDK_PLATFORM_DIR = \
	$(ANDROID_SDK_HOME)/platforms/$(CFG_ANDROID_SDK_PLATFORM)
AAPT = $(ANDROID_SDK_BUILD_TOOL_DIR)/aapt
DX = $(ANDROID_SDK_BUILD_TOOL_DIR)/dx
ZIPALIGN = $(ANDROID_SDK_BUILD_TOOL_DIR)/zipalign
APKSIGNER = $(ANDROID_SDK_BUILD_TOOL_DIR)/apksigner
ANDROID_JAR = $(ANDROID_SDK_PLATFORM_DIR)/android.jar

# define dirs
SRC_DIR = src
LIB_DIR = libs
RES_DIR = res
KEY_DIR = key
BUILD_DIR = build
BUILD_BIN_DIR = build/bin
BUILD_BIN_CLASSES_DIR = build/bin/classes
BUILD_GEN_DIR = build/gen
BUILD_GEN_SRC_DIR = $(BUILD_DIR)/gen/$(subst .,/,$(CFG_PACKAGE_NAME))

# define files
ANDROID_MANIFEST_XML = AndroidManifest.xml
R_JAVA = $(BUILD_GEN_SRC_DIR)/R.java
SOURCES = \
	$(shell \
		find $(SRC_DIR) -not -path '*/\.*' \
			-type f -name '*.java')
LIBRARIES = \
	$(shell \
		test -d $(LIB_DIR) && \
		find $(LIB_DIR) -not -path '*/\.*' \
			-type f -name '*.jar')
RESOURCES = \
	$(shell \
		find $(RES_DIR) -not -path '*/\.*' \
			-type f)
CLASSES = \
	$(foreach SRC, $(SOURCES), \
		$(patsubst \
			$(SRC_DIR)%.java, \
			$(BUILD_BIN_CLASSES_DIR)%.class, \
			$(SRC)))
CLASSES_DEX = $(BUILD_BIN_DIR)/classes.dex
UNALIGNED_APK = $(BUILD_BIN_DIR)/$(CFG_APK_NAME)-unaligned.apk
UNSIGNED_APK = $(BUILD_BIN_DIR)/$(CFG_APK_NAME)-unsigned.apk
FINAL_APK = $(BUILD_BIN_DIR)/$(CFG_APK_NAME).apk

# define rules
.PHONY: build check-config create-dir clean

build: check-config create-dir $(FINAL_APK)

check-config:
ifndef ANDROID_SDK_HOME
	$(error ANDROID_SDK_HOME is undefined)
endif

create-dir:
	@mkdir -p $(BUILD_BIN_DIR)
	@mkdir -p $(BUILD_BIN_CLASSES_DIR)
	@mkdir -p $(BUILD_GEN_DIR)
	@mkdir -p $(BUILD_GEN_SRC_DIR)

$(FINAL_APK): $(UNSIGNED_APK)
	@$(call ECHO, "[build final apk ...]")
	@$(APKSIGNER) sign \
		--v1-signing-enabled true \
		--v2-signing-enabled true \
		--ks $(KEY_DIR)/android.keystore \
		--ks-pass file:$(KEY_DIR)/keystore.password \
		--key-pass file:$(KEY_DIR)/key.password \
		--out $(FINAL_APK) $(UNSIGNED_APK)

$(UNSIGNED_APK): $(UNALIGNED_APK)
	@$(call ECHO, "[build unsigned apk ...]")
	@$(ZIPALIGN) -v -f -p 4 $(UNALIGNED_APK) $(UNSIGNED_APK)

$(UNALIGNED_APK): $(ANDROID_MANIFEST_XML) $(CLASSES_DEX) $(RESOURCES)
	@$(call ECHO, "[build unaligned apk ...]")
	@$(AAPT) package -M $(ANDROID_MANIFEST_XML) \
		-I $(ANDROID_JAR) -S $(RES_DIR) \
		-F $(UNALIGNED_APK) -f
	@cd $(BUILD_BIN_DIR) && zip $(abspath $(UNALIGNED_APK)) classes.dex

$(CLASSES_DEX): $(CLASSES)
	@$(call ECHO, "[build classes.dex ...]")
	@$(DX) --dex --output=$@ $(BUILD_BIN_CLASSES_DIR) $(LIBRARIES)

$(CLASSES): $(SOURCES) $(R_JAVA) $(LIBRARIES)
	@$(call ECHO, "[compile java classes...]")
	@javac -bootclasspath $(ANDROID_JAR) \
		$(foreach LIB,$(LIBRARIES),-classpath $(LIB)) \
		-d $(BUILD_BIN_CLASSES_DIR) \
		$(SOURCES) $(R_JAVA)

$(R_JAVA): $(ANDROID_MANIFEST_XML) $(RESOURCES)
	@$(call ECHO, "[generate R.java ...]")
	@$(AAPT) package -M $(ANDROID_MANIFEST_XML) \
		-I $(ANDROID_JAR) -S $(RES_DIR) \
		-J $(BUILD_GEN_DIR) -m

clean:
	@$(call ECHO, "[clean build dir ...]")
	@rm -rf $(BUILD_GEN_DIR)
	@rm -rf $(BUILD_BIN_DIR)
	@rm -f $(BUILD_DIR)/*.apk
