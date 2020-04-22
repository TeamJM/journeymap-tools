[Setup]
AppName=JourneyMap Tools
AppVersion=1.0
DefaultDirName={autopf}\JourneyMap Tools
DefaultGroupName=JourneyMap
OutputBaseFilename=JMToolsSetup
OutputDir="..\..\..\build\innosetup\"

[Dirs]
Name: "{app}\lib"

[Files]
Source: "..\..\..\build\launch4j\JMTools.exe"; \
        DestDir: "{app}"

Source: "..\resources\jm.ico"; \
        DestDir: "{app}"

Source: "..\..\..\build\launch4j\lib\*"; \
        DestDir: "{app}/lib"

[Icons]
Name: "{userdesktop}\JourneyMap Tools"; \
      Filename: "{app}/JMTools.exe"; \
      WorkingDir: "{app}"; \
      Comment: "Launch JourneyMap Tools"; \
      IconFilename: "{app}\jm.ico"

Name: "{group}\JourneyMap Tools"; \
      Filename: "{app}/JMTools.exe"; \
      WorkingDir: "{app}"; Comment: \
      "Launch JourneyMap Tools"; \
      IconFilename: "{app}\jm.ico"

Name: "{group}\Uninstall JourneyMap Tools"; \
      Filename: "{uninstallexe}"; \
      WorkingDir: "{app}"; \
      Comment: "Uninstall JourneyMap Tools"

[Registry]
; https://stackoverflow.com/a/3431379
Root: HKLM; \
      Subkey: "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"; \
      ValueType: expandsz; \
      ValueName: "Path"; \
      ValueData: "{olddata};{app}"; \
      Check: NeedsAddPath('{app}')

[Code]
{ https://stackoverflow.com/a/3431379 }
function NeedsAddPath(Param: string): boolean;
var
    OrigPath: string;
begin
    if not RegQueryStringValue(HKEY_LOCAL_MACHINE,
        'SYSTEM\CurrentControlSet\Control\Session Manager\Environment',
        'Path', OrigPath)
    then begin
        Result := True;
        exit;
    end;
    { look for the path with leading and trailing semicolon }
    { Pos() returns 0 if not found }
    Result := Pos(';' + Param + ';', ';' + OrigPath + ';') = 0;
end;