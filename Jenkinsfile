#!/usr/bin/env groovy
pipeline {
    agent any
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