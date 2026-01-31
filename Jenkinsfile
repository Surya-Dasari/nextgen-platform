pipeline {
    agent any

    environment {
        MAVEN_OPTS     = "-Dmaven.repo.local=.m2/repository"
        DOCKER_REPO    = "docker.io/<YOUR_DOCKERHUB_USERNAME>"
        IMAGE_TAG      = "${BUILD_NUMBER}"
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            when { branch 'develop' }
            steps {
                checkout scm
            }
        }

        stage('Verify Build Tools') {
            when { branch 'develop' }
            steps {
                sh '''
                echo "=== Tool Versions ==="
                mvn -v
                node -v
                npm -v
                docker -v
                '''
            }
        }

        stage('Build Spring Boot Services') {
            when { branch 'develop' }
            steps {
                sh '''
                for service in apiservice authservice userservice
                do
                  echo "=== Building $service ==="
                  cd services/$service
                  mvn clean package -DskipTests
                  cd -
                done
                '''
            }
        }

        stage('Prepare Frontend (Express)') {
            when { branch 'develop' }
            steps {
                sh '''
                echo "=== Installing frontend dependencies ==="
                cd services/frontend
                npm install
                '''
            }
        }

        stage('Docker Build Images') {
            when { branch 'develop' }
            steps {
                script {
                    def services = [
                        "apiservice",
                        "authservice",
                        "userservice",
                        "frontend"
                    ]

                    for (svc in services) {
                        sh """
                        echo "=== Building Docker image: nextgen-${svc}:${IMAGE_TAG} ==="
                        docker build \
                          -t ${DOCKER_REPO}/nextgen-${svc}:${IMAGE_TAG} \
                          services/${svc}
                        """
                    }
                }
            }
        }

        stage('Docker Push Images') {
            when { branch 'develop' }
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                    echo "=== Logging in to Docker Hub ==="
                    echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                    for img in apiservice authservice userservice frontend
                    do
                      echo "=== Pushing nextgen-$img:${IMAGE_TAG} ==="
                      docker push ${DOCKER_REPO}/nextgen-$img:${IMAGE_TAG}
                    done
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "✅ CI + Docker SUCCESS: Images built and pushed to Docker Hub"
        }
        failure {
            echo "❌ PIPELINE FAILED: Check logs"
        }
    }
}

