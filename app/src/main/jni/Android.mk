LOCAL_PATH := $(call my-dir)

###############################################################################
## libancillary
###############################################################################

include $(CLEAR_VARS)

LOCAL_MODULE := libancillary
LOCAL_CFLAGS += -I$(LOCAL_PATH)/libancillary
LOCAL_SRC_FILES := \
libancillary/fd_recv.c \
libancillary/fd_send.c

include $(BUILD_STATIC_LIBRARY)
