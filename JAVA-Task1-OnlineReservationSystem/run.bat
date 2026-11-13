@echo off
setlocal
cd /d "%~dp0"
chcp 65001 >nul

if not exist "lib\mysql-connector-j-9.4.0.jar" (
  echo MySQL JDBC driver not found at lib\mysql-connector-j-9.4.0.jar
  echo Download it from https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.4.0/mysql-connector-j-9.4.0.jar
  pause
  exit /b 1
)

if not exist "out" mkdir out
dir /s /b src\*.java > sources.txt
echo Compiling...
javac -encoding UTF-8 -cp "lib\mysql-connector-j-9.4.0.jar" -d out @sources.txt
if errorlevel 1 (
  echo.
  echo Build failed.
  del sources.txt
  pause
  exit /b 1
)
del sources.txt
echo Starting RailConnect...
java -cp "out;lib\mysql-connector-j-9.4.0.jar" com.reservation.Main %*
