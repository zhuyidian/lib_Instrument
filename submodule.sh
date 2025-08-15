#!/bin/bash

# 定义子模块数组（仓库URL 路径 分支）
submodules=(
#    "http://gitlab.skysri.com/sea/aihuman library/LibAiHumn/aihuman release_v1.2"
#    "http://gitlab.skysri.com/coopark/lib_smartcool_voice.git library/LibSmartCoolVoice/SmartCoolVoice v1.0_pad_web"
#    "http://gitlab.skysri.com/cooui/lib_eventtracking.git library/LibEventTracking/EventTracking cooui-v1.8-pad"
#    "http://gitlab.skysri.com/cooui/core_util library/LibUtil/Util cooui-v1.7"
#    "http://gitlab.skysri.com/cooui/lib_user library/LibUser/User cooui-v1.8"
#    "http://gitlab.skysri.com/CCSDK/server-verification library/LibDomainVerify/server-verification cooui-dev"
#    "http://gitlab.skysri.com/cooui/lib_network library/LibNetwork/Network cooui-v1.9-english_speak"
#    "http://gitlab.skysri.com/cooui/common library/LibCommon/Common cooui-v2.0-voice-pad"
#    "http://gitlab.skysri.com/cooui/lib_operateappui library/LibOperateAppUI/AppUI cooui-v1.9"
#    "http://gitlab.skysri.com/CCSDK/AF_Bolts core/CoreBolts/AF_Bolts cooui-dev"
#    "http://gitlab.skysri.com/CCSDK/CC_UIEngineSDK core/CoreUIEngine/CC_UIEngineSDK cooui-v2.0-Story1.0"
#    "http://gitlab.skysri.com/CCSDK/AF_ImageLoader core/CoreImageLoader/AF_ImageLoader cooui-v1.10"
#    "http://gitlab.skysri.com/cooui/core_cache core/CoreCache/Cache cooui-v1.1"
#    "http://gitlab.skysri.com/cooui/core_http core/CoreHttp/Http cooui-dev"
#    "http://gitlab.skysri.com/CCSDK/AppDownload library/LibApkDownload/AppDownload cooui-v1.8"
#    "https://github.com/zhuyidian/lib_Instrument_Promotion Instrument/library/LibPromotion/Promotion main"
#	"https://github.com/zhuyidian/lib_Instrument_Frameworks Instrument/library/LibFrameworks/Frameworks main"
	"https://github.com/zhuyidian/lib_Instrument_WebviewSenior Instrument/library/LibWebviewSenior/WebviewSenior main"
)

function addSubmodule(){
    # 循环添加子模块
    for module in "${submodules[@]}"; do
        read url path branch <<< "$module"
        echo "Adding submodule: $url => $path (branch: $branch)"
        #git submodule deinit -f $path
        git rm --cached $path
        rm -rf .git/modules/$path
        rm -rf $path
        git submodule add --branch $branch $url $path
    done
}

function removeSubmodule() {
    # 循环添加子模块
    for module in "${submodules[@]}"; do
        read url path branch <<< "$module"
        echo "Removeing submodule: $url => $path (branch: $branch)"
        git submodule deinit -f $path
        git rm --cached $path
        rm -rf .git/modules/$path
        rm -rf $path
    done
}

exe_cmd=$1
echo "exe_cmd: $exe_cmd"

if [[ $exe_cmd == "add" ]]
then
  addSubmodule
  # 初始化子模块
  git submodule update --init --recursive
elif [[ $exe_cmd == "remove" ]]
then
  removeSubmodule
else
  echo "exe cmd is null"
fi