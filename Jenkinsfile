#!/usr/bin/env groovy
pipeline {
    agent any

    stages {
        stage ('Cleaning stage') {
            steps {
                withMaven(gradle : 'gradle-4.4.1') {
                    sh 'gradle clean'
                }
            }
        }
        stage ('Testing stage') {
            steps {
                withMaven(gradle : 'gradle-4.4.1') {
                    sh 'gradle test'
                }
            }
        }
    }
}