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
            
            // We don't want the same deployment to run multiple times at same time
            // We also want to make sure we don't starve the job queue (limiting job to run up to a certain time)
            throttle([]) {
                timeout(time: 4, unit: 'HOURS') {


                
            }
        }   
    }    


    
}          