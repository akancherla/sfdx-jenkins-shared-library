#!/usr/bin/env groovy

def call(script) {
    
    echo "Script ${script}"
    
  //  def json = sh returnStdout: true, script: script
  //  def object = readJSON text: json
    def json = ''
    def object = ''
    def rc = ''
  if (isUnix()) {
        rc =  sh returnStdout: true, script: script
         json = rc1.readLines().drop(1).join(" ")
         object = readJSON text: json1
        if (object.status != 0) {
            error "Script ${script} failed: status ${object.status} message: ${object.message} json: ${json}"
         }
        return object.result
    } else {
         rc =  bat(returnStdout: true, script: script).trim()
         json = rc.readLines().drop(1).join(" ")
      //  json = rs.readLines()
        echo "json---- ${json}"
         object = readJSON text: json
        echo "output---- ${object}"
        if (object.status != 0) {
            error "Script ${script} failed: status ${object.status} message: ${object.message} json: ${json}"
         }
        return object.result
    }
   // object = readJSON text: json
    
    
    
}