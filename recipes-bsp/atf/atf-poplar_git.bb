DESCRIPTION = "ARM Trusted Firmware Poplar"

LICENSE="BSD"
LIC_FILES_CHKSUM = "file://license.md;md5=829bdeb34c1d9044f393d5a16c068371"

COMPATIBLE_MACHINE = "poplar"

DEPENDS = " u-boot-poplar openssl-native"

SRCREV = "e5a382576c556670070caf8a66574a29e18c1a93"

SRC_URI = "git://github.com/linaro/poplar-arm-trusted-firmware.git;name=atf;branch=latest;"

S = "${WORKDIR}/git"

# /usr/lib/edk2/bl1.bin not shipped files. [installed-vs-shipped]
#INSANE_SKIP_${PN} += "installed-vs-shipped"

export LDFLAGS=""

do_compile() {

# if optee exists copy across tee.bin
#    install -D -p -m0644 \
#      ${STAGING_DIR_HOST}/lib/firmware/tee.bin \
#      ${EDK2_DIR}/optee_os/out/arm-plat-hikey/core/tee.bin

    oe_runmake CROSS_COMPILE=${TARGET_PREFIX} all fip DEBUG=1 PLAT=${COMPATIBLE_MACHINE} SPD=none \
		       BL33=${DEPLOY_DIR_IMAGE}/u-boot.bin
}

do_install() {
    install -D -p -m0644 ${S}/build/${COMPATIBLE_MACHINE}/debug/bl1.bin ${D}${libdir}/atf/bl1.bin
    install -D -p -m0644 ${S}/build/${COMPATIBLE_MACHINE}/debug/fip.bin ${D}${libdir}/atf/fip.bin
}

do_deploy() {
    install -D -p -m0644 fip.bin ${DEPLOYDIR}/fip.bin
}
