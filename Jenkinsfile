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
    post{
        failure{
            emailext to: "selfly@foxmail.com",
            subject: "Jenkins build:${currentBuild.currentResult}: ${env.JOB_NAME}",
            body: "${currentBuild.currentResult}: Job ${env.JOB_NAME}\nMore Info can be found here: ${env.BUILD_URL}"
        }
    }
}