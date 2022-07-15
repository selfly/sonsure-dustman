pipeline {
    agent any
    tools {
       maven 'maven'
    }
    triggers {
            cron('10 23 * * *')
    }
    stages {
        stage('Build') {
            steps {
                script {
                    if (env.GIT_PREVIOUS_SUCCESSFUL_COMMIT == env.GIT_COMMIT) {
                        echo 'no commit changes，skip build'
                    } else {
                        sh 'mvn clean deploy -P sonsure'
                    }
                }
            }
        }
    }
}