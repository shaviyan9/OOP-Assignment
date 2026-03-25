@echo off
REM ─────────────────────────────────────────────────────────────────────────────
REM build.bat  –  Compile and run the Placement System on Windows
REM ─────────────────────────────────────────────────────────────────────────────

set MYSQL_JAR=

for %%f in (lib\mysql-connector-j-*.jar) do set MYSQL_JAR=%%f

if "%MYSQL_JAR%"=="" (
    echo ERROR: MySQL Connector/J JAR not found in lib\
    echo Download from: https://dev.mysql.com/downloads/connector/j/
    echo Place the JAR in the lib\ directory and re-run.
    pause
    exit /b 1
)

echo Using: %MYSQL_JAR%

if not exist out mkdir out

echo Collecting Java source files...
dir /s /b src\*.java > src_files.txt

echo Compiling...
javac -cp "%MYSQL_JAR%" -d out @src_files.txt

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful. Starting...
    java -cp "out;%MYSQL_JAR%" Main
) else (
    echo Compilation FAILED.
    pause
    exit /b 1
)

del src_files.txt
pause
