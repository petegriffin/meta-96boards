require linux.inc

DESCRIPTION = "96boards-hikey kernel"

PV = "5.3+git${SRCPV}"
SRCREV_kernel = "7e517c4200191eac0c76e8a11a8e6e99f3d2231c"
SRCREV_FORMAT = "kernel"

SRC_URI = "git://git.linaro.org/people/john.stultz/android-dev.git;protocol=https;branch=dev/hikey-5.3;name=kernel \
"

SRC_URI_append_hikey = " \
    file://0001-Revert-drm-kirin-Remove-HISI_KIRIN_DW_DSI-config-opt.patch \
    file://0001-Kbuild-update-to-srctree-src-so-the-build-succedes.patch \
    file://mali-450.conf;subdir=git/kernel/configs \
    file://defconfig;subdir=git/kernel/configs \
    file://0001-Revert-arm64-dts-hikey-Enable-SDIO-high-speed-mode-o.patch \
    file://0001-hi6220-hikey-disabled-wifi-sdio-dt-node.patch \
"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "hikey"
KERNEL_IMAGETYPE ?= "Image"
KERNEL_CONFIG_FRAGMENTS_hikey += "${S}/kernel/configs/mali-450.conf"

# make[3]: *** [scripts/extract-cert] Error 1
DEPENDS += "openssl-native"
HOST_EXTRACFLAGS += "-I${STAGING_INCDIR_NATIVE}"

do_configure() {
    # Make sure to disable debug info and enable ext4fs built-in
    sed -e '/CONFIG_EXT4_FS=/d' \
        -e '/CONFIG_DEBUG_INFO=/d' \
        < ${S}/kernel/configs/defconfig \
        > ${B}/.config

    echo 'CONFIG_EXT4_FS=y' >> ${B}/.config
    echo '# CONFIG_DEBUG_INFO is not set' >> ${B}/.config

    # Check for kernel config fragments. The assumption is that the config
    # fragment will be specified with the absolute path. For example:
    #   * ${WORKDIR}/config1.cfg
    #   * ${S}/config2.cfg
    # Iterate through the list of configs and make sure that you can find
    # each one. If not then error out.
    # NOTE: If you want to override a configuration that is kept in the kernel
    #       with one from the OE meta data then you should make sure that the
    #       OE meta data version (i.e. ${WORKDIR}/config1.cfg) is listed
    #       after the in-kernel configuration fragment.
    # Check if any config fragments are specified.
    if [ ! -z "${KERNEL_CONFIG_FRAGMENTS}" ]; then
        for f in ${KERNEL_CONFIG_FRAGMENTS}; do
            # Check if the config fragment was copied into the WORKDIR from
            # the OE meta data
            if [ ! -e "$f" ]; then
                echo "Could not find kernel config fragment $f"
                exit 1
            fi
        done

        # Now that all the fragments are located merge them.
        ( cd ${WORKDIR} && ${S}/scripts/kconfig/merge_config.sh -m -r -O ${B} ${B}/.config ${KERNEL_CONFIG_FRAGMENTS} 1>&2 )
    fi

    yes '' | oe_runmake -C ${S} O=${B} oldconfig

    bbplain "Saving defconfig to:\n${B}/defconfig"
    oe_runmake -C ${B} savedefconfig
}

do_deploy_append() {
    cp -a ${B}/defconfig ${DEPLOYDIR}
    cp -a ${B}/.config ${DEPLOYDIR}/config
}
