#!/usr/bin/env groovy
import com.akan.sjsl.Org

def call(Map parameters = [:]) {

    echo("Jobname ${JOB_NAME}")
    echo("Env Jobname ${env.JOB_NAME}")

    def packagesToInstall = parameters.packages ?: []
    def authenticateToOrg = parameters.authenticate ?: []
    def authorizeDevHub = parameters.authorize ?: []
    def sfInstanceURL = parameters.sfInstanceURL ?: []
    def sfConsumerKey = parameters.sfConsumerKey ?: []
    def sfUserName = parameters.sfUserName ?: []
    def sfdxUrlCredentialId = parameters.sfdxUrlCredentialId
    def unlockedPackagesToInstall = parameters.unlockedPackages ?: []
    def unpackagedSourcePathToInstall = parameters.unpackagedSourcePath // comma-separated, at least for now

    def deploymentOrg = new Org()

    

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
                            stage("Authenticate to org") {

                                if (authenticateToOrg) {

                                //    withCredentials([file(credentialsId: sfdxUrlCredentialId, variable: 'SFDX_URL')]) {
                                        def authenticationResult = shWithResult('sfdx force:auth:sfdxurl:store --setalias="$JOB_NAME" --setdefaultusername --sfdxurlfile=$server_key_file --json')
                                        deploymentOrg.alias = "${env.JOB_NAME}"
                                        deploymentOrg.username = authenticationResult.username
                                        deploymentOrg.orgId = authenticationResult.orgId
                                        deploymentOrg.instanceUrl = authenticationResult.instanceUrl
                                        echo("Successfully authorized ${authenticationResult.username} with org ID ${authenticationResult.orgId}")
                                //    }

                                }
                                else {

                                    echo("No Authenticate to org")

                                }
                                

                            }

                            stage('Authorize DevHub') {

                                if (authorizeDevHub) {
                                //    withCredentials([file(credentialsId: sfdxUrlCredentialId, variable: 'server_key_file')]) {

                                    // def toolbelt = tool 'toolbelt'
                                def rc =  shWithStatus("sfdx auth:jwt:grant --instanceurl=${sfInstanceURL} --clientid=${sfConsumerKey} --username=${sfUserName} --jwtkeyfile=${server_key_file} --setdefaultdevhubusername --setalias=${authorizeDevHub}")
                                    //  echo "Script---- ${toolbelt}/sfdx force:auth:jwt:grant --instanceurl ${sfInstanceURL} --clientid ${sfConsumerKey} --username ${sfUserName} --jwtkeyfile ${server_key_file} --setdefaultdevhubusername --setalias HubOrg"
                                //  def rc = command "sfdx force:auth:jwt:grant --instanceurl ${sfInstanceURL} --clientid ${sfConsumerKey} --username ${sfUserName} --jwtkeyfile ${server_key_file} --setdefaultdevhubusername --setalias ${authorizeDevHub}"
                                 if (rc != 0) {
                                        error "Salesforce dev hub org authorization failed.--- ${rc}"
                                }
                                        deploymentOrg.devHubAlias = authorizeDevHub
                                        echo("Successfully authorized DevHub")
                                        
                                //    }

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