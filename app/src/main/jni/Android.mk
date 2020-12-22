LOCAL_PATH := $(call my-dir)

BUILD_SHARED_EXECUTABLE := $(LOCAL_PATH)/build-shared-executable.mk

###############################################################################
## libancillary
###############################################################################

include $(CLEAR_VARS)

LOCAL_MODULE := libancillary
LOCAL_CFLAGS += -I$(LOCAL_PATH)/libancillary

LOCAL_SRC_FILES := \
    libancillary/fd_recv.c \
    libancillary/fd_send.c \

include $(BUILD_STATIC_LIBRARY)

###############################################################################
## tun2socks
###############################################################################

include $(CLEAR_VARS)

LOCAL_MODULE := tun2socks
LOCAL_CFLAGS := -std=gnu99
LOCAL_CFLAGS += -Wno-parentheses -Wno-unused-value
LOCAL_CFLAGS += -Wno-address-of-packed-member -Wno-pointer-sign
LOCAL_CFLAGS += -DNDEBUG -DANDROID -DBADVPN_LINUX -D_GNU_SOURCE
LOCAL_CFLAGS += -DBADVPN_THREADWORK_USE_PTHREAD -DBADVPN_THREAD_SAFE
LOCAL_CFLAGS += -DBADVPN_BREACTOR_BADVPN
LOCAL_CFLAGS += -DBADVPN_USE_SIGNALFD -DBADVPN_USE_EPOLL
LOCAL_CFLAGS += -DBADVPN_LITTLE_ENDIAN
LOCAL_LDLIBS := -ldl -llog
LOCAL_STATIC_LIBRARIES := libancillary

LOCAL_C_INCLUDES:= \
    $(LOCAL_PATH)/libancillary \
    $(LOCAL_PATH)/badvpn \
    $(LOCAL_PATH)/badvpn/lwip/src/include \
    $(LOCAL_PATH)/badvpn/lwip/custom \

LOCAL_SRC_FILES := \
    badvpn/base/BLog.c \
    badvpn/base/BLog_syslog.c \
    badvpn/base/BPending.c \
    badvpn/base/DebugObject.c \
    badvpn/flow/BufferWriter.c \
    badvpn/flow/LineBuffer.c \
    badvpn/flow/PacketBuffer.c \
    badvpn/flow/PacketCopier.c \
    badvpn/flow/PacketPassConnector.c \
    badvpn/flow/PacketPassFairQueue.c \
    badvpn/flow/PacketPassFifoQueue.c \
    badvpn/flow/PacketPassInterface.c \
    badvpn/flow/PacketPassNotifier.c \
    badvpn/flow/PacketPassPriorityQueue.c \
    badvpn/flow/PacketProtoDecoder.c \
    badvpn/flow/PacketProtoEncoder.c \
    badvpn/flow/PacketProtoFlow.c \
    badvpn/flow/PacketRecvBlocker.c \
    badvpn/flow/PacketRecvConnector.c \
    badvpn/flow/PacketRecvInterface.c \
    badvpn/flow/PacketRouter.c \
    badvpn/flow/PacketStreamSender.c \
    badvpn/flow/RouteBuffer.c \
    badvpn/flow/SinglePacketBuffer.c \
    badvpn/flow/SinglePacketSender.c \
    badvpn/flow/SingleStreamReceiver.c \
    badvpn/flow/SingleStreamSender.c \
    badvpn/flow/StreamPacketSender.c \
    badvpn/flow/StreamPassConnector.c \
    badvpn/flow/StreamPassInterface.c \
    badvpn/flow/StreamRecvConnector.c \
    badvpn/flow/StreamRecvInterface.c \
    badvpn/flowextra/KeepaliveIO.c \
    badvpn/flowextra/PacketPassInactivityMonitor.c \
    badvpn/lwip/custom/sys.c \
    badvpn/lwip/src/core/altcp.c \
    badvpn/lwip/src/core/altcp_tcp.c \
    badvpn/lwip/src/core/def.c \
    badvpn/lwip/src/core/dns.c \
    badvpn/lwip/src/core/inet_chksum.c \
    badvpn/lwip/src/core/init.c \
    badvpn/lwip/src/core/ip.c \
    badvpn/lwip/src/core/mem.c \
    badvpn/lwip/src/core/memp.c \
    badvpn/lwip/src/core/netif.c \
    badvpn/lwip/src/core/pbuf.c \
    badvpn/lwip/src/core/raw.c \
    badvpn/lwip/src/core/stats.c \
    badvpn/lwip/src/core/sys.c \
    badvpn/lwip/src/core/tcp.c \
    badvpn/lwip/src/core/tcp_in.c \
    badvpn/lwip/src/core/tcp_out.c \
    badvpn/lwip/src/core/timeouts.c \
    badvpn/lwip/src/core/udp.c \
    badvpn/lwip/src/core/ipv4/autoip.c \
    badvpn/lwip/src/core/ipv4/dhcp.c \
    badvpn/lwip/src/core/ipv4/etharp.c \
    badvpn/lwip/src/core/ipv4/icmp.c \
    badvpn/lwip/src/core/ipv4/igmp.c \
    badvpn/lwip/src/core/ipv4/ip4_addr.c \
    badvpn/lwip/src/core/ipv4/ip4.c \
    badvpn/lwip/src/core/ipv4/ip4_frag.c \
    badvpn/lwip/src/core/ipv6/dhcp6.c \
    badvpn/lwip/src/core/ipv6/ethip6.c \
    badvpn/lwip/src/core/ipv6/icmp6.c \
    badvpn/lwip/src/core/ipv6/inet6.c \
    badvpn/lwip/src/core/ipv6/ip6_addr.c \
    badvpn/lwip/src/core/ipv6/ip6.c \
    badvpn/lwip/src/core/ipv6/ip6_frag.c \
    badvpn/lwip/src/core/ipv6/mld6.c \
    badvpn/lwip/src/core/ipv6/nd6.c \
    badvpn/socksclient/BSocksClient.c \
    badvpn/socks_udp_client/SocksUdpClient.c \
    badvpn/system/BConnection_common.c \
    badvpn/system/BConnection_unix.c \
    badvpn/system/BDatagram_unix.c \
    badvpn/system/BInputProcess.c \
    badvpn/system/BLockReactor.c \
    badvpn/system/BNetwork.c \
    badvpn/system/BProcess.c \
    badvpn/system/BReactor_badvpn.c \
    badvpn/system/BSignal.c \
    badvpn/system/BThreadSignal.c \
    badvpn/system/BTime.c \
    badvpn/system/BUnixSignal.c \
    badvpn/tun2socks/tun2socks_android.c \
    badvpn/tun2socks/SocksUdpGwClient.c \
    badvpn/tuntap/BTap.c \
    badvpn/udpgw_client/UdpGwClient.c \

include $(BUILD_SHARED_EXECUTABLE)
