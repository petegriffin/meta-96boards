# Enable lima for hisilicon
PACKAGECONFIG_append_hisilicon-gx = " gallium"
PACKAGECONFIG_remove_hisilicon-gx = " vulkan"
GALLIUMDRIVERS_hisilicon-gx = "hisilicon,meson,lima,freedreno,etnaviv,swrast,imx,rockchip,sun4i"
DRIDRIVERS_hisilicon-gx = ""
