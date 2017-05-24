require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot:"

SUMMARY = "u-boot bootloader for HiSilicon Poplar"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

BRANCH ?= "latest"
SRC_URI = "git://github.com/linaro/poplar-u-boot;protocol=git;branch=${BRANCH}"
SRCREV = "1de155d0a7fe1ba5ca949dd1e8a5f1b2d276b228"

PV = "v2017.05+git${SRCPV}"

# u-boot needs devtree compiler to parse dts files
DEPENDS += "dtc-native bc-native"

PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/git"

