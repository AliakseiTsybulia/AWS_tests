#!groovy

node {
   stage('Test') {
    def name = env.BRANCH_NAME
        if (isUnix()) {
            sh "'${GRADLE_HOME}/bin/mvn' clean"
        } else {
            gradle('clean test')
        }
    }
}

def gradle(command) {
    GRADLE_HOME = tool 'gradle-4.4.1'
    bat "${GRADLE_HOME}/bin/gradle ${command}"
}

