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
                sh 'mvn clean deploy -P sonsure -X'
            }
        }
    }
}