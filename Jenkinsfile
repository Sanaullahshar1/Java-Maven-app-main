def gv

pipeline {
    agent any
    tools {
        maven 'maven 3.9.6'
    }
    environment {
        ACR_LOGIN_SERVER = 'azrrepo.azurecr.io'
        APP_NAME = 'java-app'
        ACR_REPO = "${ACR_LOGIN_SERVER}/${APP_NAME}"
    }
    stages {
        stage("init") {
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }
        stage("build version") {
            steps {
                script {
                    gv.buildVersion()
                }
            }
        }
        stage("build jar") {
            steps {
                script {
                    echo "building jar"
                    gv.buildJar()
                }
            }
        }
        stage("build image") {
            steps {
                script {
                    echo "building image"
                    gv.buildImage()
                }
            }
        }
        stage("deploy") {
            steps {
                script {
                    gv.deployApp()
                }
            }
        }
        stage("commit version") {
            steps {
                script {
                    gv.commitVersion()
                }
            }
        }
    }   
}
