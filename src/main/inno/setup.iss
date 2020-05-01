[Setup]
AppName=JourneyMap Tools
AppVersion=1.1
DefaultDirName={autopf}\JourneyMap Tools
DefaultGroupName=JourneyMap
LicenseFile=LICENSE.rtf
OutputBaseFilename=JMToolsSetup
OutputDir="..\..\..\build\innosetup\"

[Dirs]
Name: "{app}\lib"
Name: "{app}\jdk-11.0.7+10-jre"

[Files]
Source: "..\..\..\build\launch4j\JMTools.exe"; \
        DestDir: "{app}"

Source: "..\resources\jm.ico"; \
        DestDir: "{app}"

Source: "..\..\..\build\launch4j\lib\*"; \
        DestDir: "{app}/lib"; \
        Excludes: "*linux*,*mac*"

Source: "..\..\..\jdk-11.0.7+10-jre\*"; \
        DestDir: "{app}\jdk-11.0.7+10-jre"; \
        Flags: createallsubdirs recursesubdirs

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
