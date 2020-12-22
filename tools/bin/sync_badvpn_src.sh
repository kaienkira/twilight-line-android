#!/bin/bash

set -o pipefail

script_name=`basename "$0"`
script_abs_name=`readlink -f "$0"`
script_path=`dirname "$script_abs_name"`

# check args
if [ $# -ne 1 ]
then
    echo "usage: $script_name <badvpn_src_dir>"
    exit 1
fi

# dirs
badvpn_src_dir=`readlink -f "$1"`
proj_home_dir=`readlink -f "$script_path/../.."`
output_dir=$proj_home_dir/app/src/main/jni/badvpn

# check exists
if [ ! -d "$badvpn_src_dir" ]
then
    echo "dir $badvpn_src_dir not found"
    exit 1
fi
if [ ! -d "$proj_home_dir" ]
then
    echo "dir $proj_home_dir not found"
    exit 1
fi
if [ ! -d "$output_dir" ]
then
    echo "dir $output_dir not found"
    exit 1
fi

# copy files
# -- COPYING
cp "$badvpn_src_dir"/COPYING "$output_dir"
if [ $? -ne 0 ]; then exit 1; fi
# -- base
mkdir -p "$output_dir"/base
if [ $? -ne 0 ]; then exit 1; fi
cp "$badvpn_src_dir"/base/*.{c,h} "$output_dir"/base
if [ $? -ne 0 ]; then exit 1; fi
# -- flow
mkdir -p "$output_dir"/flow
if [ $? -ne 0 ]; then exit 1; fi
cp "$badvpn_src_dir"/flow/*.{c,h} "$output_dir"/flow
if [ $? -ne 0 ]; then exit 1; fi
# -- flowextra
mkdir -p "$output_dir"/flowextra
if [ $? -ne 0 ]; then exit 1; fi
cp "$badvpn_src_dir"/flowextra/*.{c,h} "$output_dir"/flowextra
if [ $? -ne 0 ]; then exit 1; fi
# -- generated
mkdir -p "$output_dir"/generated
if [ $? -ne 0 ]; then exit 1; fi
cp "$badvpn_src_dir"/generated/blog_channel_*.h "$output_dir"/generated
if [ $? -ne 0 ]; then exit 1; fi
cp "$badvpn_src_dir"/generated/bproto_*.h "$output_dir"/generated
if [ $? -ne 0 ]; then exit 1; fi
# -- lwip
cp "$badvpn_src_dir"/lwip/COPYING "$output_dir"/lwip
if [ $? -ne 0 ]; then exit 1; fi
mkdir -p "$output_dir"/lwip/src
if [ $? -ne 0 ]; then exit 1; fi
cp -r "$badvpn_src_dir"/lwip/src/core "$output_dir"/lwip/src
if [ $? -ne 0 ]; then exit 1; fi
cp -r "$badvpn_src_dir"/lwip/src/include "$output_dir"/lwip/src
if [ $? -ne 0 ]; then exit 1; fi
cp -r "$badvpn_src_dir"/lwip/custom "$output_dir"/lwip
if [ $? -ne 0 ]; then exit 1; fi
# -- misc
mkdir -p "$output_dir"/misc
cp "$badvpn_src_dir"/misc/*.h "$output_dir"/misc
if [ $? -ne 0 ]; then exit 1; fi
# -- protocol
mkdir -p "$output_dir"/protocol
cp "$badvpn_src_dir"/protocol/*.h "$output_dir"/protocol
if [ $? -ne 0 ]; then exit 1; fi
# -- socksclient
mkdir -p "$output_dir"/socksclient
cp "$badvpn_src_dir"/socksclient/*.{c,h} "$output_dir"/socksclient
if [ $? -ne 0 ]; then exit 1; fi
# -- socks_udp_client
mkdir -p "$output_dir"/socks_udp_client
cp "$badvpn_src_dir"/socks_udp_client/*.{c,h} "$output_dir"/socks_udp_client
if [ $? -ne 0 ]; then exit 1; fi
# -- structure
mkdir -p "$output_dir"/structure
cp "$badvpn_src_dir"/structure/*.h "$output_dir"/structure
if [ $? -ne 0 ]; then exit 1; fi
# -- system
mkdir -p "$output_dir"/system
cp "$badvpn_src_dir"/system/*.{c,h} "$output_dir"/system
if [ $? -ne 0 ]; then exit 1; fi
# -- tun2socks
mkdir -p "$output_dir"/tun2socks
cp "$badvpn_src_dir"/tun2socks/*.{c,h} "$output_dir"/tun2socks
if [ $? -ne 0 ]; then exit 1; fi
# -- tuntap
mkdir -p "$output_dir"/tuntap
cp "$badvpn_src_dir"/tuntap/*.{c,h} "$output_dir"/tuntap
# -- udpgw_client
mkdir -p "$output_dir"/udpgw_client
cp "$badvpn_src_dir"/udpgw_client/*.{c,h} "$output_dir"/udpgw_client

exit 0
