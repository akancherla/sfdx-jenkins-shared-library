#!/usr/bin/env groovy

def call(script) {
    
    echo "Script ${script}"
    
  //  def json = sh returnStdout: true, script: script
  //  def object = readJSON text: json
//def json = ''
//def object = ''
  if (isUnix()) {
       def rc1 =  sh returnStdout: true, script: script
        json1 = rc1.readLines().drop(1).join(" ")
        def object1 = readJSON text: json1
        if (object1.status != 0) {
            error "Script ${script} failed: status ${object1.status} message: ${object1.message} json: ${json1}"
         }
        return object1.result
    } else {
        def rc =  bat returnStdout: true, script: script
        json = rc.readLines().drop(1).join(" ")
      //  json = rs.readLines()
        echo "json---- ${json}"
        def object = readJSON text: json
        echo "output---- ${object}"
        if (object.status != 0) {
            error "Script ${script} failed: status ${object.status} message: ${object.message} json: ${json}"
         }
        return object.result
    }
   // object = readJSON text: json
    
    
    
}