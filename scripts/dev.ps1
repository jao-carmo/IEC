param([ValidateSet('run', 'test', 'package')][string]$Task = 'run')
$ErrorActionPreference = 'Stop'
$projectDirectory = Split-Path -Parent $PSScriptRoot
$localJdk = Get-ChildItem (Join-Path $projectDirectory '.tools/java17') -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
if ($localJdk) { $env:JAVA_HOME = $localJdk.FullName }
if (!$env:JAVA_HOME -or !(Test-Path (Join-Path $env:JAVA_HOME 'bin/java.exe'))) {
    throw 'Set JAVA_HOME to a JDK 17 or 21 installation before running this script.'
}
$env:DEBUG = 'false'
Push-Location $projectDirectory
try {
    if ($Task -eq 'run') { & .\mvnw.cmd spring-boot:run }
    else { & .\mvnw.cmd $Task }
    if ($LASTEXITCODE -ne 0) { throw "Maven failed with exit code $LASTEXITCODE." }
} finally { Pop-Location }
