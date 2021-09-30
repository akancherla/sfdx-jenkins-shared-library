#!/usr/bin/env groovy
import com.akan.sjsl.Org

def call(Map parameters = [:]) {

    echo("Jobname ${JOB_NAME}")
    echo("Env Jobname ${env.JOB_NAME}")

    def packagesToInstall = parameters.packages ?: []
    def scratchOrgCreate = parameters.scratchOrgCreate ?: []
    def authorizeDevHub = parameters.authorize ?: []
    def sfInstanceURL = parameters.sfInstanceURL ?: []
    def sfConsumerKey = parameters.sfConsumerKey ?: []
    def sfUserName = parameters.sfUserName ?: []
    def sfdxUrlCredentialId = parameters.sfdxUrlCredentialId
    def unlockedPackagesToInstall = parameters.unlockedPackages ?: []
    def unpackagedSourcePathToInstall = parameters.unpackagedSourcePath // comma-separated, at least for now

    def deploymentOrg = new Org()
    def deploymentScratchOrg = new Org()

    

    if (!sfdxUrlCredentialId?.trim()) {
        error('Please specify a credential id on sfdxUrlCredentialId')
    }

  //  pipeline {
        node {

            throttle([]) {
                timeout(time: 4, unit: 'HOURS') {
            
            // stage("Checkout") {
                     //   checkout(scm: scm)
                   // }

                   withEnv(["HOME=${env.WORKSPACE}"]) {
        
                        withCredentials([file(credentialsId: sfdxUrlCredentialId, variable: 'server_key_file')]) {

                            echo("server_key_file --- ${server_key_file} --- ${sfdxUrlCredentialId}")
                       //     cat $server_key_file > server_key_file.key

                           
                            stage('Authorize DevHub') {

                                if (authorizeDevHub) {
                                
                                    // def toolbelt = tool 'toolbelt'
                                    //def rc =  shWithStatus("sfdx auth:jwt:grant --instanceurl=${sfInstanceURL} --clientid=${sfConsumerKey} --username=${sfUserName} --jwtkeyfile=${server_key_file} --setdefaultdevhubusername --setalias=${authorizeDevHub}")
                                    def authorizeResult =  shWithResult("sfdx auth:jwt:grant --instanceurl=${sfInstanceURL} --clientid=${sfConsumerKey} --username=${sfUserName} --jwtkeyfile=${server_key_file} --setdefaultdevhubusername --setalias=${authorizeDevHub} --json")
                                    deploymentOrg.alias = "${env.JOB_NAME}"
                                    deploymentOrg.devHubAlias = authorizeDevHub
                                    deploymentOrg.orgId = authorizeResult.orgId
                                    deploymentOrg.username = authorizeResult.username
                                    deploymentOrg.instanceUrl = authorizeResult.instanceUrl
                                    echo("Successfully authorized DevHub --- --- ${authorizeResult}")
                                        
                                }
                                else {

                                    echo("No Authorize to org")

                                }
                                    
                            }

                            stage('Scratch Org Creation') {

                                if (scratchOrgCreate) {
                                
                                    createScratchOrg deploymentOrg
                                    echo("Successfully authorized DevHub --- --- ${deploymentOrg}")
                                        
                                }
                                else {

                                    echo("No Authorize to org")

                                }
                                    
                            }
                        }
                   }
                }
            
            }

        }
   // }
}