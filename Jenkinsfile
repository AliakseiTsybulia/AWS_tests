#!/usr/bin/env groovy

def GRADLE_HOME = tool name: 'gradle-4.4.1', type: 'gradle'

def gradle(command) {
        bat "${GRADLE_HOME}/bin/gradle ${command}"
}

void test() {
        try {
            gradle 'clean test'
        } finally {
            step $class: 'JUnitResultArchiver', allowEmptyResults: true, testResults: '**/build/test-results/TEST-*.xml'
        }
}

node {
    stage('Test') {
        test()
}





