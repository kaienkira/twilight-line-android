LOCAL_BUILD_SCRIPT := BUILD_EXECUTABLE
LOCAL_MAKEFILE     := $(local-makefile)

$(call check-defined-LOCAL_MODULE,$(LOCAL_BUILD_SCRIPT))
$(call check-LOCAL_MODULE,$(LOCAL_MAKEFILE))
$(call check-LOCAL_MODULE_FILENAME)

# we are building target objects
my := TARGET_

$(call handle-module-filename,lib,$(TARGET_SONAME_EXTENSION))
$(call handle-module-built)

LOCAL_MODULE_CLASS := EXECUTABLE
include $(BUILD_SYSTEM)/build-module.mk
