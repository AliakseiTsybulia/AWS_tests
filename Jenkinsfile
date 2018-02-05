#!groovy

node {
   stage('Test') {
    def name = env.BRANCH_NAME
        if (isUnix()) {
            sh "'${GRADLE_HOME}/bin/mvn' clean"
        } else {
            try {
                gradle('clean test')
            } catch (error) {
               println 'Could not start tests. Proceeding anyways...'
               println err
            }
        }
    }
}

def gradle(command) {
    GRADLE_HOME = tool 'gradle-4.4.1'
    bat "${GRADLE_HOME}/bin/gradle ${command}"
}

