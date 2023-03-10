@echo off

SET GP_HOME=.

SET JAVA_COMMAND=%JAVA_HOME%\bin\java.exe
IF "%JAVA_HOME%"=="" SET JAVA_COMMAND=java

SET LOCAL_CLASSPATH=%GP_HOME%\eclipsito.jar

SET CONFIGURATION_FILE=ganttproject-eclipsito-config.xml
SET BOOT_CLASS=org.bardsoftware.eclipsito.Boot

"%JAVA_COMMAND%" -Xmx256m -classpath "%CLASSPATH%;%LOCAL_CLASSPATH%" %BOOT_CLASS% "%CONFIGURATION_FILE%" -log -open %1 %2 %3 %4 %5 %6 
if %ERRORLEVEL% EQU 9009 goto ERROR_NO_JAVA
goto END

:ERROR_NO_JAVA
echo "Please set up JAVA_HOME variable"

:END

