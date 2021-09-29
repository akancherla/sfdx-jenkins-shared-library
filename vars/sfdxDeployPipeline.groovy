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

    pipeline {
        node {
            
            // stage("Checkout") {
                     //   checkout(scm: scm)
                   // }
                    stage("Authenticate to org") {

                        if (authenticateToOrg) {

                            withCredentials([file(credentialsId: sfdxUrlCredentialId, variable: 'SFDX_URL')]) {
                                def authenticationResult = shWithResult('sfdx force:auth:sfdxurl:store --setalias="$JOB_NAME" --setdefaultusername --sfdxurlfile=$SFDX_URL --json')
                                deploymentOrg.alias = "${env.JOB_NAME}"
                                deploymentOrg.username = authenticationResult.username
                                deploymentOrg.orgId = authenticationResult.orgId
                                deploymentOrg.instanceUrl = authenticationResult.instanceUrl
                                echo("Successfully authorized ${authenticationResult.username} with org ID ${authenticationResult.orgId}")
                            }

                        }
                        else {

                            echo("No Authenticate to org")

                        }
                        

                    }

                    stage('Authorize DevHub') {

                        if (authorizeDevHub) {
                            withCredentials([file(credentialsId: sfdxUrlCredentialId, variable: 'server_key_file')]) {
                                shWithStatus("sfdx auth:jwt:grant --instanceurl=${sfInstanceURL} --clientid=${sfConsumerKey} --username=${sfUserName} --jwtkeyfile=${server_key_file} --setdefaultdevhubusername --setalias=${authorizeDevHub}")
                                deploymentOrg.devHubAlias = authorizeDevHub
                                echo("Successfully authorized DevHub")
                                
                            }

                        }
                        else {

                            echo("No Authorize to org")

                        }
                
                    }
        }
    }
}