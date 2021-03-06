#include <sys/un.h>
#include <ancillary.h>
#include <android/log.h>

#define main main2
#include "badvpn/tun2socks/tun2socks.c"
#undef main

struct {
    char *sock_path;
    int tun_mtu;
} options_android;

static void android_log(int channel, int level, const char *msg)
{
    static char *level_names[] = { NULL, "ERROR", "WARNING", "NOTICE", "INFO", "DEBUG" };

    __android_log_print(ANDROID_LOG_DEBUG, "tun2socks",
        "%s(%s): %s\n", level_names[level],
        blog_global.channels[channel].name, msg);

    fprintf(stderr, "%s(%s): %s\n", level_names[level],
        blog_global.channels[channel].name, msg);
}

static void android_log_free(void)
{
}

static int android_parse_arguments(int argc, char *argv[])
{
    if (argc <= 0) {
        return 0;
    }

    // init options
    options.help = 0;
    options.version = 0;
    options.logger = LOGGER_STDOUT;
    options.loglevel = -1;
    for (int i = 0; i < BLOG_NUM_CHANNELS; i++) {
        options.loglevels[i] = -1;
    }
    options.tundev = NULL;
    options.netif_ipaddr = NULL;
    options.netif_netmask = NULL;
    options.netif_ip6addr = NULL;
    options.socks_server_addr = NULL;
    options.username = NULL;
    options.password = NULL;
    options.password_file = NULL;
    options.append_source_to_username = 0;
    options.udpgw_remote_server_addr = NULL;
    options.udpgw_max_connections = DEFAULT_UDPGW_MAX_CONNECTIONS;
    options.udpgw_connection_buffer_size = DEFAULT_UDPGW_CONNECTION_BUFFER_SIZE;
    options.udpgw_transparent_dns = 0;
    options.socks5_udp = 0;
    // init options_android
    options_android.sock_path = NULL;
    options_android.tun_mtu = 1500;

    for (int i = 1; i < argc; i++) {
        char *arg = argv[i];

        // --loglevel
        if (strcmp(arg, "--loglevel") == 0) {
            if (1 >= argc - i) {
                fprintf(stderr, "%s: requires an argument\n", arg);
                return 0;
            }
            if ((options.loglevel = parse_loglevel(argv[i + 1])) < 0) {
                fprintf(stderr, "%s: wrong argument\n", arg);
                return 0;
            }
            ++i;

        // --netif-ipaddr
        } else if (strcmp(arg, "--netif-ipaddr") == 0) {
            if (1 >= argc - i) {
                fprintf(stderr, "%s: requires an argument\n", arg);
                return 0;
            }
            options.netif_ipaddr = argv[i + 1];
            ++i;

        // --netif-netmask
        } else if (strcmp(arg, "--netif-netmask") == 0) {
            if (1 >= argc - i) {
                fprintf(stderr, "%s: requires an argument\n", arg);
                return 0;
            }
            options.netif_netmask = argv[i + 1];
            ++i;

        // --netif-ip6addr
        } else if (strcmp(arg, "--netif-ip6addr") == 0) {
            if (1 >= argc - i) {
                fprintf(stderr, "%s: requires an argument\n", arg);
                return 0;
            }
            options.netif_ip6addr = argv[i + 1];
            ++i;

        // --sock-path
        } else if (strcmp(arg, "--sock-path") == 0) {
            if (1 >= argc - i) {
                fprintf(stderr, "%s: requires an argument\n", arg);
                return 0;
            }
            options_android.sock_path = argv[i + 1];
            ++i;

        // --tun-mtu
        } else if (strcmp(arg, "--tun-mtu") == 0) {
            if (1 >= argc - i) {
                fprintf(stderr, "%s: requires an argument\n", arg);
                return 0;
            }
            if ((options_android.tun_mtu = atoi(argv[i + 1])) <= 0) {
                fprintf(stderr, "%s: wrong argument\n", arg);
                return 0;
            }
            ++i;

        // --socks-server-addr
        } else if (strcmp(arg, "--socks-server-addr") == 0) {
            if (1 >= argc - i) {
                fprintf(stderr, "%s: requires an argument\n", arg);
                return 0;
            }
            options.socks_server_addr = argv[i + 1];
            ++i;

        // --udpgw-remote-server-addr
        } else if (strcmp(arg, "--udpgw-remote-server-addr") == 0) {
            if (1 >= argc - i) {
                fprintf(stderr, "%s: requires an argument\n", arg);
                return 0;
            }
            options.udpgw_remote_server_addr = argv[i + 1];
            ++i;

        // unknown
        } else {
            fprintf(stderr, "unknown option: %s\n", arg);
            return 0;
        }
    }

    if (options.netif_ipaddr == NULL) {
        fprintf(stderr, "--netif-ipaddr is required\n");
        return 0;
    }
    if (options.netif_netmask == NULL) {
        fprintf(stderr, "--netif-netmask is required\n");
        return 0;
    }
    if (options_android.sock_path == NULL) {
        fprintf(stderr, "--sock-path is required\n");
        return 0;
    }
    if (options.socks_server_addr == NULL) {
        fprintf(stderr, "--socks-server-addr is required\n");
        return 0;
    }

    return 1;
}

static void android_print_help(const char *name)
{
    printf(
        "Usage:\n"
        "    %s\n"
        "        [--loglevel <0-5/none/error/warning/notice/info/debug>]\n"
        "        --netif-ipaddr <ipaddr>\n"
        "        --netif-netmask <ipnetmask>\n"
        "        [--netif-ip6addr <addr>]\n"
        "        --sock-path <path>\n"
        "        --tun-mtu <mtu>\n"
        "        --socks-server-addr <addr>\n"
        "        [--udpgw-remote-server-addr <addr>]\n",
        name
    );
}

static int android_wait_for_vpn_fd()
{
    // create socket
    int sock = socket(AF_UNIX, SOCK_STREAM, 0);
    if (-1 == sock) {
        BLog(BLOG_ERROR, "socket() failed: %s", strerror(errno));
        return -1;
    }

    // set socket nonblock
    {
        int flags = fcntl(sock, F_GETFL, 0);
        if (-1 == flags) {
            flags = 0;
        }
        fcntl(sock, F_SETFL, flags | O_NONBLOCK);
    }

    // delete sock file
    unlink(options_android.sock_path);

    // create addr
    struct sockaddr_un addr;
    memset(&addr, 0, sizeof(addr));
    addr.sun_family = AF_UNIX;
    strncpy(addr.sun_path, options_android.sock_path,
            sizeof(addr.sun_path) - 1);

    // bind
    if (bind(sock, (struct sockaddr *)&addr, sizeof(addr)) == -1) {
        BLog(BLOG_ERROR, "bind() failed: %s", strerror(errno));
        close(sock);
        return -1;
    }
    // listen
    if (listen(sock, 5) == -1) {
        BLog(BLOG_ERROR, "listen() failed: %s", strerror(errno));
        close(sock);
        return -1;
    }
    {
        fd_set set;
        FD_ZERO(&set);
        FD_SET(sock, &set);

        BLog(BLOG_INFO, "waiting for vpn fd");

        if (select(sock + 1, &set, NULL, NULL, NULL) < 0) {
            BLog(BLOG_ERROR, "select() failed: %s\n", strerror(errno));
            return -1;
        }

        struct sockaddr_un remote_addr;
        int remote_addr_size = sizeof(remote_addr);
        int remote_sock = accept(sock,
            (struct sockaddr *)&remote_addr, &remote_addr_size);
        if (remote_sock == -1) {
            BLog(BLOG_ERROR, "accept() failed: %s", strerror(errno));
            return -1;
        }

        int vpn_fd = -1;
        if (ancil_recv_fd(remote_sock, &vpn_fd)) {
            BLog(BLOG_ERROR, "ancil_recv_fd: %s", strerror(errno));
            close(remote_sock);
            close(sock);
            return -1;
        } else {
            close(remote_sock);
            close(sock);
            BLog(BLOG_INFO, "received vpn fd = %d", vpn_fd);
            return vpn_fd;
        }
    }
}

static int android_init_tun_device(int vpn_fd)
{
    struct BTap_init_data init_data;
    init_data.dev_type = BTAP_DEV_TUN;
    init_data.init_type = BTAP_INIT_FD;
    init_data.init.fd.fd = vpn_fd;
    init_data.init.fd.mtu = options_android.tun_mtu;

    if (!BTap_Init2(&device, &ss, init_data, device_error_handler, NULL)) {
        BLog(BLOG_ERROR, "BTap_Init2 failed");
        return 0;
    }

    return 1;
}

int main(int argc, char *argv[])
{
    if (argc <= 0) {
        return 1;
    }

    // open standard streams
    open_standard_streams();

    // parse command-line arguments
    if (!android_parse_arguments(argc, argv)) {
        android_print_help(argv[0]);
        goto fail0;
    }

    // initialize logger
    BLog_Init(android_log, android_log_free);
    // configure logger channels
    for (int i = 0; i < BLOG_NUM_CHANNELS; i++) {
        BLog_SetChannelLoglevel(i, options.loglevel);
    }

    BLog(BLOG_NOTICE, "initializing "GLOBAL_PRODUCT_NAME" "PROGRAM_NAME" "GLOBAL_VERSION);

    // clear password contents pointer
    password_file_contents = NULL;

    // initialize network
    if (!BNetwork_GlobalInit()) {
        BLog(BLOG_ERROR, "BNetwork_GlobalInit failed");
        goto fail1;
    }

    // process arguments
    if (!process_arguments()) {
        BLog(BLOG_ERROR, "Failed to process arguments");
        goto fail1;
    }

    // get vpn fd
    int vpn_fd = android_wait_for_vpn_fd();
    if (-1 == vpn_fd) {
        BLog(BLOG_ERROR, "Failed to get vpn fd");
        goto fail1;
    }

    // init time
    BTime_Init();

    // init reactor
    if (!BReactor_Init(&ss)) {
        BLog(BLOG_ERROR, "BReactor_Init failed");
        goto fail1;
    }

    // set not quitting
    quitting = 0;

    // setup signal handler
    if (!BSignal_Init(&ss, signal_handler, NULL)) {
        BLog(BLOG_ERROR, "BSignal_Init failed");
        goto fail2;
    }

    // init TUN device
    if (!android_init_tun_device(vpn_fd)) {
        goto fail3;
    }

    ///////////////////////////////////////////////////////////////////////////

    // NOTE: the order of the following is important:
    // first device writing must evaluate,
    // then lwip (so it can send packets to the device),
    // then device reading (so it can pass received packets to lwip).

    // init device reading
    PacketPassInterface_Init(&device_read_interface, BTap_GetMTU(&device), device_read_handler_send, NULL, BReactor_PendingGroup(&ss));
    if (!SinglePacketBuffer_Init(&device_read_buffer, BTap_GetOutput(&device), &device_read_interface, BReactor_PendingGroup(&ss))) {
        BLog(BLOG_ERROR, "SinglePacketBuffer_Init failed");
        goto fail4;
    }

    // Compute the largest possible UDP payload that we can receive from or send to the
    // TUN device.
    udp_mtu = BTap_GetMTU(&device) - (int)(sizeof(struct ipv4_header) + sizeof(struct udp_header));
    if (options.netif_ip6addr) {
        int udp_ip6_mtu = BTap_GetMTU(&device) - (int)(sizeof(struct ipv6_header) + sizeof(struct udp_header));
        if (udp_mtu < udp_ip6_mtu) {
            udp_mtu = udp_ip6_mtu;
        }
    }
    if (udp_mtu < 0) {
        udp_mtu = 0;
    }

    if (options.udpgw_remote_server_addr) {
        udp_mode = UdpModeUdpgw;

        // make sure our UDP payloads aren't too large for udpgw
        int udpgw_mtu = udpgw_compute_mtu(udp_mtu);
        if (udpgw_mtu < 0 || udpgw_mtu > PACKETPROTO_MAXPAYLOAD) {
            BLog(BLOG_ERROR, "device MTU is too large for UDP");
            goto fail4a;
        }

        // init udpgw client
        if (!SocksUdpGwClient_Init(&udpgw_client, udp_mtu, DEFAULT_UDPGW_MAX_CONNECTIONS,
            options.udpgw_connection_buffer_size, UDPGW_KEEPALIVE_TIME, socks_server_addr,
            socks_auth_info, socks_num_auth_info, udpgw_remote_server_addr,
            UDPGW_RECONNECT_TIME, &ss, NULL, udp_send_packet_to_device))
        {
            BLog(BLOG_ERROR, "SocksUdpGwClient_Init failed");
            goto fail4a;
        }
    } else if (options.socks5_udp) {
        udp_mode = UdpModeSocks;

        // init SOCKS UDP client
        SocksUdpClient_Init(&socks_udp_client, udp_mtu, DEFAULT_UDPGW_MAX_CONNECTIONS,
            SOCKS_UDP_SEND_BUFFER_PACKETS, UDPGW_KEEPALIVE_TIME, socks_server_addr,
            socks_auth_info, socks_num_auth_info, &ss, NULL, udp_send_packet_to_device);
    } else {
        udp_mode = UdpModeNone;
    }

    // init lwip init job
    BPending_Init(&lwip_init_job, BReactor_PendingGroup(&ss), lwip_init_job_hadler, NULL);
    BPending_Set(&lwip_init_job);

    // init device write buffer
    if (!(device_write_buf = (uint8_t *)BAlloc(BTap_GetMTU(&device)))) {
        BLog(BLOG_ERROR, "BAlloc failed");
        goto fail5;
    }

    // init TCP timer
    // it won't trigger before lwip is initialized, becuase the lwip init is a job
    BTimer_Init(&tcp_timer, TCP_TMR_INTERVAL, tcp_timer_handler, NULL);
    BReactor_SetTimer(&ss, &tcp_timer);
    tcp_timer_mod4 = 0;

    // set no netif
    have_netif = 0;

    // set no listener
    listener = NULL;
    listener_ip6 = NULL;

    // init clients list
    LinkedList1_Init(&tcp_clients);

    // init number of clients
    num_clients = 0;

    // enter event loop
    BLog(BLOG_NOTICE, "entering event loop");
    BReactor_Exec(&ss);

    // free clients
    LinkedList1Node *node;
    while (node = LinkedList1_GetFirst(&tcp_clients)) {
        struct tcp_client *client = UPPER_OBJECT(node, struct tcp_client, list_node);
        client_murder(client);
    }

    // free listener
    if (listener_ip6) {
        tcp_close(listener_ip6);
    }
    if (listener) {
        tcp_close(listener);
    }

    // free netif
    if (have_netif) {
        netif_remove(&the_netif);
    }

    BReactor_RemoveTimer(&ss, &tcp_timer);
    BFree(device_write_buf);
fail5:
    BPending_Free(&lwip_init_job);
    if (udp_mode == UdpModeUdpgw) {
        SocksUdpGwClient_Free(&udpgw_client);
    } else if (udp_mode == UdpModeSocks) {
        SocksUdpClient_Free(&socks_udp_client);
    }
fail4a:
    SinglePacketBuffer_Free(&device_read_buffer);
fail4:
    PacketPassInterface_Free(&device_read_interface);
    BTap_Free(&device);
fail3:
    BSignal_Finish();
fail2:
    BReactor_Free(&ss);
fail1:
    BFree(password_file_contents);
    BLog(BLOG_NOTICE, "exiting");
    BLog_Free();
fail0:
    DebugObjectGlobal_Finish();

    return 1;
}
