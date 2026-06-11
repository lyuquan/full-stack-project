# Quick start script for the backend Spring Boot app.
# Usage: open PowerShell in the backend folder, then run: .\run.ps1

# $PSScriptRoot is the folder where this script lives: backend.
$BackendDir = $PSScriptRoot

# Find the local Maven executable inside this project.
$Maven = Resolve-Path "$BackendDir\..\.tools\apache-maven-3.9.6\bin\mvn.cmd"

# Find the project Maven settings file.
$Settings = Resolve-Path "$BackendDir\.mvn\settings.xml"

# Switch to backend so Maven can find pom.xml.
Set-Location $BackendDir

# Start the Spring Boot backend.
& $Maven spring-boot:run -s $Settings
