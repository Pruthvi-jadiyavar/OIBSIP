@echo off
if not exist out mkdir out
javac -d out src\*.java
if %errorlevel% equ 0 echo Build successful. Output in out\
