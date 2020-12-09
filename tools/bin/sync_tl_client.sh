#!/bin/bash

set -o pipefail

script_name=`basename "$0"`
script_abs_name=`readlink -f "$0"`
script_path=`dirname "$script_abs_name"`

# check args
if [ $# -ne 1 ]
then
    echo "usage: $script_name <tl_src_dir>"
    exit 1
fi

# dirs
tl_src_dir=`readlink -f "$1"`
proj_home_dir=`readlink -f "$script_path/../.."`
config_output_dir=$proj_home_dir/app/src/main/assets/config
bin_output_dir=$proj_home_dir/app/src/main/jniLibs

# check exists
if [ ! -d "$tl_src_dir" ]
then
    echo "dir $tl_src_dir not found"
    exit 1
fi
if [ ! -d "$proj_home_dir" ]
then
    echo "dir $proj_home_dir not found"
    exit 1
fi
if [ ! -d "$config_output_dir" ]
then
    echo "dir $config_output_dir not found"
    exit 1
fi
if [ ! -d "$bin_output_dir" ]
then
    echo "dir $bin_output_dir not found"
    exit 1
fi

# copy files
# -- config
cp "$tl_src_dir"/etc/client_config.json \
   "$config_output_dir"/tl-client-config.json
if [ $? -ne 0 ]; then exit 1; fi
# -- binary
cp "$tl_src_dir"/bin/twilight-line-go-client-android-i686 \
   "$bin_output_dir"/x86/libtlclient.so
if [ $? -ne 0 ]; then exit 1; fi
cp "$tl_src_dir"/bin/twilight-line-go-client-android-x86_64 \
   "$bin_output_dir"/x86_64/libtlclient.so
if [ $? -ne 0 ]; then exit 1; fi
cp "$tl_src_dir"/bin/twilight-line-go-client-android-armv7a \
   "$bin_output_dir"/armeabi-v7a/libtlclient.so
if [ $? -ne 0 ]; then exit 1; fi
cp "$tl_src_dir"/bin/twilight-line-go-client-android-aarch64 \
   "$bin_output_dir"/arm64-v8a/libtlclient.so
if [ $? -ne 0 ]; then exit 1; fi

exit 0
