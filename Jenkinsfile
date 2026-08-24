pipeline {
    agent any

    parameters {
        choice(
            name: 'ACTION',
            choices: ['DEPLOY', 'ROLLBACK'],
            description: 'Choose whether to deploy the new build or rollback'
        )

        string(
            name: 'ROLLBACK_VERSION',
            defaultValue: '1.0',
            description: 'Docker image version to rollback to'
        )
    }

    stages {

        stage('Checkout') {
            when {
                expression {
                    params.ACTION == 'DEPLOY'
                }
            }

            steps {
                checkout scm
            }
        }

        stage('Test') {
            when {
                expression {
                    params.ACTION == 'DEPLOY'
                }
            }

            steps {
                sh 'mvn test'
            }
        }

        stage('Package') {
            when {
                expression {
                    params.ACTION == 'DEPLOY'
                }
            }

            steps {
                sh 'mvn clean package'
            }
        }

        stage('Docker Build') {
            when {
                expression {
                    params.ACTION == 'DEPLOY'
                }
            }

            steps {
                sh 'docker build -t managerleave:${BUILD_NUMBER} .'
            }
        }

        stage('Deploy New Version') {
            when {
                expression {
                    params.ACTION == 'DEPLOY'
                }
            }

            steps {
                sh '''
                    echo "Deploying managerleave:${BUILD_NUMBER}"

                    docker stop managerleave-app || true
                    docker rm managerleave-app || true

                    docker run -d \
                        --name managerleave-app \
                        -p 8094:8093 \
                        managerleave:${BUILD_NUMBER}
                '''
            }
        }

        stage('Health Check') {
            when {
                expression {
                    params.ACTION == 'DEPLOY'
                }
            }

            steps {
                sh '''
                    echo "Waiting for application to start..."

                    for i in {1..12}
                    do
                        if curl -fs http://localhost:8094/actuator/health; then
                            echo ""
                            echo "Application is healthy!"
                            exit 0
                        fi

                        echo "Application not ready yet..."
                        sleep 5
                    done

                    echo "Application failed health check."
                    exit 1
                '''
            }
        }

        stage('Rollback') {
            when {
                expression {
                    params.ACTION == 'ROLLBACK'
                }
            }

            steps {
                sh '''
                    echo "Rolling back to managerleave:${ROLLBACK_VERSION}"

                    docker stop managerleave-app || true
                    docker rm managerleave-app || true

                    docker run -d \
                        --name managerleave-app \
                        -p 8094:8093 \
                        managerleave:${ROLLBACK_VERSION}
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
