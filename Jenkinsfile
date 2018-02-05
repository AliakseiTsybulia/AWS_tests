#!/usr/bin/env groovy

GRADLE_HOME = tool name: 'gradle-4.4.1', type: 'gradle'

def gradle(command) {
    bat "${GRADLE_HOME}/bin/gradle ${command}"
}

void test() {
    stage name: 'test', concurrency: 1
    try {
        gradle 'clean test'
    } finally {
        step $class: 'JUnitResultArchiver', allowEmptyResults: true, testResults: '**/build/test-results/TEST-*.xml'
    }
}

node {
    name = env.BRANCH_NAME
    if (name.startsWith('master')) {
        test()
    } else {
        error "Don't know what to do with this branch: ${name}"
    }
}

