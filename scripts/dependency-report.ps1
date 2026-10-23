<#
.SYNOPSIS
    Writes docs/dependencies.md: the libraries packaged in the Spring Boot jar (with versions)
    and the JDK modules the application needs, as reported by jdeps.

.DESCRIPTION
    1. Builds the executable jar with Maven (skip with -SkipBuild).
    2. Extracts BOOT-INF/classes and BOOT-INF/lib from the jar.
    3. Reads each library's groupId/artifactId/version from its META-INF/maven pom.properties
       (falling back to the jar file name).
    4. Runs jdeps to find
         - the JDK modules the whole application needs (the module list for jlink), and
         - the libraries the application's own classes reference directly.
    5. Writes a Markdown report with one sorted entry per line, so the file diffs cleanly
       between branches.

    Requires a JDK (jdeps) on PATH or in JAVA_HOME, and Maven unless -SkipBuild is used.
    Works with Windows PowerShell 5.1 and PowerShell 7.

.EXAMPLE
    .\scripts\dependency-report.ps1

.EXAMPLE
    .\scripts\dependency-report.ps1 -SkipBuild -Output docs\dependencies.md
#>
[CmdletBinding()]
param(
    # Use the jar already in target/ instead of running Maven.
    [switch] $SkipBuild,

    # Report file, relative to the project root.
    [string] $Output = 'docs/dependencies.md',

    # Java release used to resolve multi-release jars. Defaults to java.version from the POM.
    [string] $Release
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem

$projectRoot = Split-Path -Parent $PSScriptRoot
Push-Location $projectRoot
try {
    [xml] $pom = Get-Content -Raw -Path 'pom.xml'
    if (-not $Release) {
        $Release = $pom.project.properties.'java.version'
    }
    $bootVersion = $pom.project.parent.version

    $jdeps = 'jdeps'
    if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME 'bin\jdeps*'))) {
        $jdeps = Join-Path $env:JAVA_HOME 'bin\jdeps'
    }
    $jdkVersion = (& $jdeps --version).Trim()

    if (-not $SkipBuild) {
        Write-Host 'Building the application jar...'
        & mvn -B -q package -DskipTests
        if ($LASTEXITCODE -ne 0) { throw "Maven build failed with exit code $LASTEXITCODE" }
    }
    $jar = Get-ChildItem -Path 'target' -Filter '*.jar' |
        Where-Object { $_.Name -notlike '*-sources.jar' -and $_.Name -notlike '*-javadoc.jar' } |
        Select-Object -First 1
    if (-not $jar) { throw 'No jar found in target/. Run without -SkipBuild.' }

    # Extract the application classes and the nested library jars
    $work = Join-Path ([System.IO.Path]::GetTempPath()) ("dependency-report-" + [guid]::NewGuid())
    $classesDir = Join-Path $work 'classes'
    $libDir = Join-Path $work 'lib'
    New-Item -ItemType Directory -Path $classesDir, $libDir | Out-Null
    $zip = [System.IO.Compression.ZipFile]::OpenRead($jar.FullName)
    try {
        foreach ($entry in $zip.Entries) {
            if ($entry.FullName.EndsWith('/')) { continue }
            if ($entry.FullName.StartsWith('BOOT-INF/classes/')) {
                $target = Join-Path $classesDir $entry.FullName.Substring('BOOT-INF/classes/'.Length)
            }
            elseif ($entry.FullName.StartsWith('BOOT-INF/lib/')) {
                $target = Join-Path $libDir $entry.Name
            }
            else { continue }
            New-Item -ItemType Directory -Force -Path (Split-Path -Parent $target) | Out-Null
            [System.IO.Compression.ZipFileExtensions]::ExtractToFile($entry, $target, $true)
        }
    }
    finally { $zip.Dispose() }

    # Library coordinates from Maven, keyed by jar file name. Many jars (Spring, Tomcat, ...)
    # carry no META-INF/maven metadata, so the dependency list is the authoritative source.
    $listFile = Join-Path $work 'dependency-list.txt'
    & mvn -B -q dependency:list -DincludeScope=runtime -DoutputAbsoluteArtifactFilename=true "-DoutputFile=$listFile"
    if ($LASTEXITCODE -ne 0) { throw "mvn dependency:list failed with exit code $LASTEXITCODE" }
    $mavenCoordinates = @{}
    foreach ($line in Get-Content -Path $listFile) {
        # group:artifact:type[:classifier]:version:scope:path[ -- module ...]
        if ($line -match '^\s*([^:\s]+):([^:\s]+):[^:\s]+:(?:[^:\s]+:)?([^:\s]+):(?:compile|runtime|provided|system):(.+?\.jar)') {
            $mavenCoordinates[(Split-Path -Leaf $Matches[4])] = [pscustomobject] @{ groupId = $Matches[1]; artifactId = $Matches[2]; version = $Matches[3] }
        }
    }

    $libraries = foreach ($libJar in Get-ChildItem -Path $libDir -Filter '*.jar') {
        $coordinates = $mavenCoordinates[$libJar.Name]
        if ($coordinates) {
            [pscustomobject] @{ Group = $coordinates.groupId; Artifact = $coordinates.artifactId; Version = $coordinates.version; Jar = $libJar.Name }
            continue
        }
        # Fallback: META-INF/maven/<group>/<artifact>/pom.properties inside the jar
        $libZip = [System.IO.Compression.ZipFile]::OpenRead($libJar.FullName)
        try {
            $candidates = foreach ($entry in $libZip.Entries | Where-Object { $_.FullName -like 'META-INF/maven/*/pom.properties' }) {
                $reader = New-Object System.IO.StreamReader($entry.Open())
                try { $properties = $reader.ReadToEnd() } finally { $reader.Dispose() }
                $values = @{}
                foreach ($line in $properties -split "`r?`n") {
                    if ($line -match '^\s*(groupId|artifactId|version)\s*=\s*(.+?)\s*$') { $values[$Matches[1]] = $Matches[2] }
                }
                if ($values.Count -eq 3) { [pscustomobject] $values }
            }
            # Shaded jars carry several pom.properties; prefer the one matching the file name
            $coordinates = $candidates | Where-Object { $libJar.Name -eq "$($_.artifactId)-$($_.version).jar" } | Select-Object -First 1
            if (-not $coordinates) { $coordinates = $candidates | Select-Object -First 1 }
        }
        finally { $libZip.Dispose() }
        if (-not $coordinates) {
            $name = [System.IO.Path]::GetFileNameWithoutExtension($libJar.Name)
            if ($name -match '^(.+?)-(\d[\w.\-]*)$') {
                # spring-boot-jarmode-* is added by spring-boot-maven-plugin, not declared as a dependency
                $group = if ($Matches[1] -like 'spring-boot-jarmode-*') { 'org.springframework.boot' } else { '?' }
                $coordinates = [pscustomobject] @{ groupId = $group; artifactId = $Matches[1]; version = $Matches[2] }
            }
            else {
                $coordinates = [pscustomobject] @{ groupId = '?'; artifactId = $name; version = '?' }
            }
        }
        [pscustomobject] @{
            Group    = $coordinates.groupId
            Artifact = $coordinates.artifactId
            Version  = $coordinates.version
            Jar      = $libJar.Name
        }
    }
    $libraries = @($libraries | Sort-Object Group, Artifact)

    # jdeps does not expand classpath wildcards on Windows, so list every jar
    $libClassPath = (Get-ChildItem -Path $libDir -Filter '*.jar' | ForEach-Object { $_.FullName }) -join [System.IO.Path]::PathSeparator
    $commonArgs = @('--multi-release', $Release, '--ignore-missing-deps', '--class-path', $libClassPath)

    # JDK modules needed at runtime: --print-module-deps follows the application classes
    # transitively through the libraries, giving the module list for jlink --add-modules
    $moduleOutput = & $jdeps @commonArgs --print-module-deps $classesDir
    if ($LASTEXITCODE -ne 0) { throw "jdeps --print-module-deps failed (exit code $LASTEXITCODE)" }
    $runtimeModules = @((($moduleOutput | Select-Object -Last 1) -split ',') | ForEach-Object { $_.Trim() } | Where-Object { $_ } | Sort-Object)

    # Direct dependencies of the application's own classes: lines like "classes -> <jar or module>"
    $summary = & $jdeps @commonArgs -summary $classesDir
    if ($LASTEXITCODE -ne 0) { throw "jdeps -summary failed (exit code $LASTEXITCODE)" }
    $targets = @($summary | ForEach-Object { if ("$_" -match '->\s*(.+?)\s*$') { Split-Path -Leaf $Matches[1] } } | Sort-Object -Unique)
    $directJars = @($targets | Where-Object { $_ -like '*.jar' })
    $appModules = @($targets | Where-Object { $_ -notlike '*.jar' })
    $direct = @($libraries | Where-Object { $directJars -contains $_.Jar })

    # Write the report
    $lines = New-Object System.Collections.Generic.List[string]
    $lines.Add('# Runtime dependencies')
    $lines.Add('')
    $lines.Add('<!-- Generated by scripts/dependency-report.ps1. Do not edit by hand. -->')
    $lines.Add('')
    $lines.Add("Generated from ``$($jar.Name)`` with jdeps $jdkVersion (``--multi-release $Release``).")
    $lines.Add('')
    $lines.Add('| | |')
    $lines.Add('|---|---|')
    $lines.Add("| Spring Boot | $bootVersion |")
    $lines.Add("| Java release | $Release |")
    $lines.Add("| Libraries in the jar | $($libraries.Count) |")
    $lines.Add("| Libraries used directly by application code | $($direct.Count) |")
    $lines.Add("| JDK modules required | $($runtimeModules.Count) |")
    $lines.Add('')
    $lines.Add('## JDK modules')
    $lines.Add('')
    $lines.Add('Modules the application and its libraries need at runtime, from `jdeps --print-module-deps` (followed transitively from the application classes). The list is reduced: jlink adds the modules these require, for example `java.sql` for `java.sql.rowset`. Use it to build a custom runtime:')
    $lines.Add('')
    $lines.Add('```')
    $lines.Add("jlink --add-modules $($runtimeModules -join ',') --output runtime")
    $lines.Add('```')
    $lines.Add('')
    $lines.Add('| Module | Used by application code |')
    $lines.Add('|---|---|')
    foreach ($module in $runtimeModules) {
        $used = if ($appModules -contains $module) { 'yes' } else { '' }
        $lines.Add("| $module | $used |")
    }
    $lines.Add('')
    $lines.Add('## Libraries used directly by application code')
    $lines.Add('')
    $lines.Add('Jars that classes in `com.example.catalog` reference, from `jdeps -summary`.')
    $lines.Add('')
    $lines.Add('| Group | Artifact | Version |')
    $lines.Add('|---|---|---|')
    foreach ($library in $direct) { $lines.Add("| $($library.Group) | $($library.Artifact) | $($library.Version) |") }
    $lines.Add('')
    $lines.Add('## All libraries in the jar')
    $lines.Add('')
    $lines.Add('Everything in `BOOT-INF/lib`, with coordinates from `mvn dependency:list` (runtime scope). `spring-boot-jarmode-*` is added by the Spring Boot Maven plugin.')
    $lines.Add('')
    $lines.Add('| Group | Artifact | Version |')
    $lines.Add('|---|---|---|')
    foreach ($library in $libraries) { $lines.Add("| $($library.Group) | $($library.Artifact) | $($library.Version) |") }

    $outputPath = Join-Path $projectRoot $Output
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $outputPath) | Out-Null
    [System.IO.File]::WriteAllText($outputPath, (($lines -join "`n") + "`n"), (New-Object System.Text.UTF8Encoding $false))
    Write-Host "Wrote $Output ($($libraries.Count) libraries, $($runtimeModules.Count) JDK modules)"
}
finally {
    if ($work -and (Test-Path $work)) { Remove-Item -Recurse -Force $work }
    Pop-Location
}
