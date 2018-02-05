#!/usr/bin/env groovy
pipeline {
    agent any
    tools {
            gradle 'gradle-4.4.1'
        }
    stages {
        stage ('Cleaning stage') {
            steps {
                    bat 'gradle clean'
            }
        }
        stage ('Testing stage') {
            steps {
                    bat 'gradle test'
            }
        }
    }
}