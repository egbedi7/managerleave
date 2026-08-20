pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t managerleave:1.0 .'
            }
        }
    }

    post {
        success {
            echo 'Build and Docker image creation successful!'
        }

        failure {
            echo 'Pipeline failed. Check the console output.'
        }
    }
}
