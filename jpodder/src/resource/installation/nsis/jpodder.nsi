; JPODDER Install script
; Version 1.1
; christophe@kualasoft.com
!define BASE_DIR "..\..\..\.."

; HM NIS Edit Wizard helper defines
!define PRODUCT_NAME "jPodder"
!define PRODUCT_VERSION "1.1"
!define PRODUCT "${PRODUCT_NAME}-${PRODUCT_VERSION}"
!define PRODUCT_PUBLISHER "Kualasoft"
!define PRODUCT_WEB_SITE "http://www.jpodder.com"
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"

; MUI 1.67 compatible ------
!include "MUI.nsh"

; MUI Settings
!define MUI_ABORTWARNING
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install.ico"
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall.ico"
;!define MUI_WELCOMEFINISHPAGE_BITMAP "${BASE_DIR}\src\resource\image\installation\jPodder-install.png"

; Welcome page
!insertmacro MUI_PAGE_WELCOME
; License page
!insertmacro MUI_PAGE_LICENSE "${BASE_DIR}\doc\license.txt"
; Directory page
!insertmacro MUI_PAGE_DIRECTORY
; Instfiles page
!insertmacro MUI_PAGE_INSTFILES
; Finish page
!insertmacro MUI_PAGE_FINISH


; Uninstaller pages
!insertmacro MUI_UNPAGE_INSTFILES

; Language files
!insertmacro MUI_LANGUAGE "English"

; MUI end ------

Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
OutFile "Setup.exe"
InstallDir "$PROGRAMFILES\${PRODUCT_NAME}-${PRODUCT_VERSION}"
ShowInstDetails show
ShowUnInstDetails show
;AddBrandingImage left 100

Section "MainSection" SEC01
  SetOverwrite try
  SetOutPath "$INSTDIR"
  ;File "..\..\..\..\doc\README.html";
  File "${BASE_DIR}\doc\*.html"
  File "${BASE_DIR}\doc\*.txt"
  SetOutPath "$INSTDIR\bin"
  File "${BASE_DIR}\src\scripts\directory\directory.*"
  File "${BASE_DIR}\src\resource\native\*.*"
  File "${BASE_DIR}\src\resource\installation\jpodder.cmd"
  File "${BASE_DIR}\src\resource\config\log4j.xml"
  File "${BASE_DIR}\src\resource\config\jpodder.properties"
  File "${BASE_DIR}\src\resource\image\ui\themes\the-error\32\jpodder.png"
  File "${BASE_DIR}\src\resource\image\installation\win32\jpodder.ico"
  File "${BASE_DIR}\target\main.jar"
  File "${BASE_DIR}\target\jPodder.one.click.jar"  
  SetOutPath "$INSTDIR\lib"
  File "${BASE_DIR}\src\resource\config\id3.xml"
  File "${BASE_DIR}\src\resource\config\style.css"
  File "${BASE_DIR}\target\tmpLibDir\*.jar"
; JAVA SCRIPT LIBRARIES.
  SetOutPath "$INSTDIR\lib\scripts"
  File "${BASE_DIR}\src\scripts\lib\xmlrpc\*.*"
  File "${BASE_DIR}\src\scripts\lib\ajaxslt-0.4\*.*"
  SetOutPath "$INSTDIR\default"
  File "${BASE_DIR}\src\resource\default\*.xml"
  File "${BASE_DIR}\src\resource\default\*.css"
SetOutPath "$INSTDIR\plugin"
  File "${BASE_DIR}\target\plugin\*.jar"
  SetOutPath "$INSTDIR\imaging"
  File /r    "${BASE_DIR}\src\resource\image\ui\*.png"
  ; Zip the source code.
  ;SetOutPath "$INSTDIR\src"
  ;File /r    "${BASE_DIR}\src\java\*.*"
  
SectionEnd

Section -AdditionalIcons
  SetOutPath $INSTDIR
  WriteIniStr "$INSTDIR\${PRODUCT_NAME}.url" "InternetShortcut" "URL" "${PRODUCT_WEB_SITE}"
  CreateDirectory "$SMPROGRAMS\${PRODUCT}"

  SetOutPath "$INSTDIR\bin"
  CreateShortCut "$SMPROGRAMS\${PRODUCT}\jPodder.lnk" "$SYSDIR\javaw.exe" "-jar $\"$INSTDIR\bin\main.jar$\"" "$INSTDIR\bin\jpodder.ico"
  CreateShortCut "$SMPROGRAMS\${PRODUCT}\jPodder site.lnk" "$INSTDIR\${PRODUCT_NAME}.url"
  CreateShortCut "$SMPROGRAMS\${PRODUCT}\Uninstall.lnk" "$INSTDIR\uninst.exe"
SectionEnd

Section -Post
  WriteUninstaller "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "URLInfoAbout" "${PRODUCT_WEB_SITE}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
SectionEnd


Function un.onUninstSuccess
  HideWindow
  MessageBox MB_ICONINFORMATION|MB_OK "$(^Name) was successfully removed from your computer."
FunctionEnd

Function un.onInit
  MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "Are you sure you want to completely remove $(^Name) and all of its components?" IDYES +2
  Abort
FunctionEnd

Section Uninstall
  Delete "$INSTDIR\${PRODUCT_NAME}.url"
  Delete "$INSTDIR\uninst.exe"
  
  Delete "$SMPROGRAMS\${PRODUCT}\jPodder.lnk"
  Delete "$SMPROGRAMS\${PRODUCT}\jPodder site.lnk"
  Delete "$SMPROGRAMS\${PRODUCT}\Uninstall.lnk"
  Delete "$SMPROGRAMS\${PRODUCT}\"

  RMDir "$SMPROGRAMS\jPodder"
  
  RMDir /r "$INSTDIR"
  ;RMDir /r "$INSTDIR\doc"
  ;RMDir /r "$INSTDIR\lib"
  ;RMDir /r "$INSTDIR\default"
  ;RMDir /r "$INSTDIR\src\"
  ;RMDir /r "$INSTDIR\imaging\"
  ;RMDir /r "$INSTDIR\plugin\"

  DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}"
  SetAutoClose true
SectionEnd