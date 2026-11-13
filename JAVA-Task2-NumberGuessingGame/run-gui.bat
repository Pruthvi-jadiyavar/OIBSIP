@echo off
if not exist out\NumberGuessingGameGUI.class (
    call build.bat
)
java -cp out NumberGuessingGameGUI
