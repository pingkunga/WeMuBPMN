1.cd to path D:\BPMN\99Web\WuMuBPMN\testData\04BatchProcess
2.json-server --watch db.json

Reference: https://naiwaen.debuggingsoft.com/tag/json-server/


def newFile = new File("C:/COUNT.txt")
newFile.write();


String readFileString(String filePath) {
    File file = new File(filePath)
    String fileContent = file.text
    return fileContent
}