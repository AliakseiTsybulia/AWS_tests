#!/usr/bin/env groovy
pipeline {
    agent any

    stages {
        stage ('Cleaning stage') {
            steps {
                    bat 'gradlew clean build'
            }
        }
        stage ('Testing stage') {
            steps {
                    bat 'gradlew test'
            }
        }
    }
}