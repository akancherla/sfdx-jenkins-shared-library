#!/usr/bin/env groovy

def call(script) {
    
    echo "Script ${script}"
    
  //  def json = sh returnStdout: true, script: script
  //  def object = readJSON text: json
//def json = ''
//def object = ''
  if (isUnix()) {
        def json1 =  sh returnStatus: true, script: script
        def object1 = readJSON text: json1
        if (object1.status != 0) {
            error "Script ${script} failed: status ${object1.status} message: ${object1.message} json: ${json1}"
         }
        return object1.result
    } else {
        def json =  bat returnStatus: true, script: script
        def object = readJSON text: json
        if (object.status != 0) {
            error "Script ${script} failed: status ${object.status} message: ${object.message} json: ${json}"
         }
        return object.result
    }
   // object = readJSON text: json
    
    
    
}