#!/usr/bin/env groovy
pipeline {
    agent any
    stages {
        stage ('Cleaning stage') {
            steps {
                    bat './gradlew clean'
            }
        }
        stage ('Testing stage') {
            steps {
                    bat './gradlew test'
            }
        }
    }
}