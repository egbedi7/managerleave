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

        stage('Deploy') {
            steps {
                sh '''
                    docker stop managerleave-app || true
                    docker rm managerleave-app || true
                    docker run -d \
                        --name managerleave-app \
                        -p 8094:8093 \
                        managerleave:1.0
                '''
            }
        }
    }

    post {
        success {
            echo 'CI/CD pipeline completed successfully!'
        }

        failure {
            echo 'Pipeline failed. Check the console output.'
        }
    }
}

