pipeline {
    agent any

    environment {
        APP_NAME = 'managerleave-app'
        HOST_PORT = '8094'
        CONTAINER_PORT = '8093'
        IMAGE_NAME = 'managerleave'
    }

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
                script {
                    env.NEW_IMAGE = "${IMAGE_NAME}:${BUILD_NUMBER}"

                    sh """
                        docker build -t ${NEW_IMAGE} .
                    """
                }
            }
        }

        stage('Get Current Version') {
            steps {
                script {
                    def previousImage = sh(
                        script: """
                            docker inspect ${APP_NAME} \
                            --format='{{.Config.Image}}' 2>/dev/null || true
                        """,
                        returnStdout: true
                    ).trim()

                    if (previousImage) {
                        env.PREVIOUS_IMAGE = previousImage
                        echo "Previous image: ${env.PREVIOUS_IMAGE}"
                    } else {
                        env.PREVIOUS_IMAGE = ''
                        echo "No previous container found. This is the first deployment."
                    }
                }
            }
        }

        stage('Deploy New Version') {
            steps {
                sh """
                    echo "Deploying ${NEW_IMAGE}"

                    docker stop ${APP_NAME} || true
                    docker rm ${APP_NAME} || true

                    docker run -d \
                        --name ${APP_NAME} \
                        -p ${HOST_PORT}:${CONTAINER_PORT} \
                        ${NEW_IMAGE}
                """
            }
        }

        stage('Health Check') {
            steps {
                script {
                    sh '''
                        echo "Waiting for application to start..."

                        for i in $(seq 1 12)
                        do
                            echo "Health check attempt $i..."

                            if curl -fs http://localhost:9999/actuator/health; then
                                echo ""
                                echo "Application is healthy!"
                                exit 0
                            fi

                            echo "Application not ready yet..."
                            sleep 5
                        done

                        echo "Health check failed!"
                        exit 1
                    '''
                }
            }
        }

        stage('Rollback') {
            when {
                expression {
                    currentBuild.currentResult == 'FAILURE' &&
                    env.PREVIOUS_IMAGE?.trim()
                }
            }

            steps {
                script {
                    echo "Rolling back to ${env.PREVIOUS_IMAGE}"

                    sh """
                        docker stop ${APP_NAME} || true
                        docker rm ${APP_NAME} || true

                        docker run -d \
                            --name ${APP_NAME} \
                            -p ${HOST_PORT}:${CONTAINER_PORT} \
                            ${PREVIOUS_IMAGE}
                    """

                    sleep 10

                    sh '''
                        curl -fs http://localhost:8094/actuator/health
                    '''

                    echo "Rollback completed successfully!"
                }
            }
        }
    }

    post {
        success {
            echo "CI/CD pipeline completed successfully!"
            echo "Application deployed using image: ${env.NEW_IMAGE}"
        }

        failure {
            echo "Pipeline failed."
            echo "Previous image was: ${env.PREVIOUS_IMAGE}"
        }
    }
}
