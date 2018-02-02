#!/usr/bin/env groovy
pipeline {
    agent any
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