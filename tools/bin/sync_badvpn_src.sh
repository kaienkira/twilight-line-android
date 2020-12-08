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
patch_dir=$proj_home_dir/tools/patch

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
if [ ! -d "$patch_dir" ]
then
    echo "dir $patch_dir not found"
    exit 1
fi

# copy files
# -- base
mkdir -p "$output_dir"/base
if [ $? -ne 0 ]; then exit 1; fi
cp "$badvpn_src_dir"/base/*.{c,h} "$output_dir"/base
if [ $? -ne 0 ]; then exit 1; fi
patch "$output_dir"/base/BLog.c "$patch_dir"/badvpn_base_BLog_c.patch
if [ $? -ne 0 ]; then exit 1; fi
# -- flow
mkdir -p "$output_dir"/flow
if [ $? -ne 0 ]; then exit 1; fi
cp "$badvpn_src_dir"/flow/*.{c,h} "$output_dir"/flow
if [ $? -ne 0 ]; then exit 1; fi
# -- lwip
mkdir -p "$output_dir"/lwip

exit 0
