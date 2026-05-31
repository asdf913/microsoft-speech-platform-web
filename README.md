# Microsoft Speech Platform Web

## Build
`mvn clean package`

## Run
`java -jar microsoft-speech-platform-web-0.0.1-SNAPSHOT.jar`

## Runtime Required
<table>
  <tbody>
    <tr>
      <td>Microsoft Speech Platform - Runtime (Version 11)</td>
      <td>https://www.microsoft.com/en-us/download/details.aspx?id=27225</td>
    </tr>
    <tr>
      <td>Microsoft Speech Platform - Runtime Languages (Version 11)</td>
      <td>https://www.microsoft.com/en-us/download/details.aspx?id=27224</td>
    </tr>
    <tr>
      <td>Download .NET Framework 4.5.1</td>
      <td>https://dotnet.microsoft.com/en-us/download/dotnet-framework/net451</td>
    </tr>
    <tr>
      <td>Java SE 8 Archive Downloads (JDK 8u202 and earlier)</td>
      <td>https://www.oracle.com/jp/java/technologies/javase/javase8-archive-downloads.html</td>
    </tr>
  </tbody>
</table>

# Development Tool Required

<table>
  <tbody>
    <tr>
      <td>Eclipse&nbsp;Oxygen</td>
      <td>https://www.eclipse.org/downloads/packages/release/oxygen/r</td>
    </tr>
    <tr>
      <td>TestNG&nbsp;for&nbsp;Eclipse </td>
      <td>https://testng.org/testng-eclipse-update-site/zipped/6.14.3.201902250526/org.testng.eclipse.updatesite.zip</td>
    </tr>
  </tbody>
</table>
## URL
<table>
  <tbody>
    <tr>
      <td>Text to Speech</td>
      <td>http://127.0.0.:8080/日本語.wav</td>
    </tr>
  </tbody>
</table>
<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>URL</th>
      <th>Result</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>IsInstalled</td>
      <td>http://127.0.0.1:8080/isInstalled</td>
      <td>true</td>
    </tr>
    <tr>
      <td>Provider&nbsp;Name</td>
      <td>http://127.0.0.1:8080/getProviderName</td>
      <td>Microsoft Speech Object Library</td>
    </tr>
    <tr>
      <td>Provider&nbsp;Version</td>
      <td>http://127.0.0.1:8080/getProviderVersion</td>
      <td>11.0.14405.00</td>
    </tr>
    <tr>
      <td>Provider&nbsp;Platform</td>
      <td>http://127.0.0.1:8080/getProviderPlatform</td>
      <td>win64</td>
    </tr>
    <tr>
      <td>Voice&nbsp;IDs</td>
      <td>http://127.0.0.1:8080/getVoiceIds</td>
      <td></td>
    </tr>
    <tr>
      <td>Voice&nbsp;Attributes&nbsp;by&nbsp;ID</td>
      <td>http://127.0.0.1:8080/HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Speech%20Server\v11.0\Voices\Tokens\TTS_MS_ja-JP_Haruka_11.0</td>
      <td>
        <pre>{
  "": "",
  "Age": "Adult",
  "AudioFormats": "18",
  "Gender": "Female",
  "Language": "411",
  "Name": "Microsoft Server Speech Text to Speech Voice (ja-JP, Haruka)",
  "Vendor": "Microsoft",
  "Version": "11.0"
}</pre>
      </td>
    </tr>
  </tbody>
</table>
