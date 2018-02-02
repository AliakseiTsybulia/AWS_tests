#!/usr/bin/env groovy
pipeline {
    agent {
        node {
          label 'my-defined-label'
          customWorkspace 'C:/projects/workspace'
        }
    }
    stages {
        stage ('Cleaning stage') {
            steps {
                    sh './gradlew clean'
            }
        }
        stage ('Testing stage') {
            steps {
                    sh './gradlew test'
            }
        }
    }
}