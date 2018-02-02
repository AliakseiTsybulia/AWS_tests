#!/usr/bin/env groovy
pipeline {
    agent any

    stages {
        stage ('Cleaning stage') {
            steps {
                    sh 'gradle clean'
            }
        }
        stage ('Testing stage') {
            steps {
                    sh 'gradle test'
            }
        }
    }
}