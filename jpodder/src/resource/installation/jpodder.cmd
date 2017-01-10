echo off
echo jPODDER launched on %DATE% at %TIME%

rem ********************************************************************************
rem 1. get the application HOME directory. Set manually otherwise.
rem ********************************************************************************

if "%OS%"=="Windows_NT" @setlocal 
rem %~dp0 is expanded pathname of the current script under NT
set DEF_JPODDER_HOME=%~dp0..
if "%JPODDER_HOME%" == "" set JPODDER_HOME=%DEF_JPODDER_HOME%
set DEF_JPODDER_HOME=
echo JPODDER is located in: %JPODDER_HOME%

rem ********************************************************************************
rem 2. get java.exe from JAVA_HOME or set the default jvm.
rem ********************************************************************************

goto checkjava

:checkJava
set _JAVACMD=%JAVACMD%

if "%JAVA_HOME%" == "" goto noJavaHome

if exist "%JAVA_HOME%\javaw.exe" (
  if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\java.exe
  goto run 
)

if exist "%JAVA_HOME%\bin\javaw.exe" (
   if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java.exe
   goto run
)

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=javaw.exe

rem ********************************************************************************
rem 3. run the application with a classpath.
rem ********************************************************************************
:run
echo The following libraries are loaded:

set _JPODDER_CP=%JPodder_HOME%\bin\Main.jar
echo #JPodder library: %_JPODDER_CP%
set CLASSPATH=%CLASSPATH%;%JPODDER_HOME%\lib;%_JPODDER_CP%

"%_JAVACMD%" -Djpodder.home="%JPODDER_HOME%" com.jpodder.Main

:end
set _JAVACMD=
set _JPODDER_CLASSPATH=
