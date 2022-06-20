pipeline {
    agent any
    tools {
       maven 'maven'
    }
//     triggers {
//             cron('0 1 * * *')
//     }
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean deploy -P sonsure -X'
            }
        }
    }
}