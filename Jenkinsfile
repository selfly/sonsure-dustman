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
                        sh 'mvn clean deploy -P sonsure \
                            sonar:sonar \
                            -Dsonar.projectKey=sonsure-dumper \
                            -Dsonar.host.url=http://192.168.50.2:9000 \
                            -Dsonar.login=d521448620605d05e7fdb68ebda4130c4a5a19dd'
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