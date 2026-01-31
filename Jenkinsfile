pipeline {
    agent any

    environment {
        NEXUS_URL        = "http://localhost:8082"
        NEXUS_MAVEN_REPO = "nextgen-maven-releases"
        MAVEN_OPTS       = "-Dmaven.repo.local=.m2/repository"
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
                mvn -v
                node -v
                npm -v
                '''
            }
        }

        stage('Build Backend Services') {
            when { branch 'develop' }
            steps {
                sh '''
                for service in apiservice authservice userservice
                do
                  echo "Building $service"
                  cd services/$service
                  mvn clean package -DskipTests
                  cd -
                done
                '''
            }
        }

        stage('Build Frontend') {
            when { branch 'develop' }
            steps {
                sh '''
                cd services/frontend
                npm install
                npm run build
                '''
            }
        }
    }

    post {
        success {
            echo "CI completed successfully"
        }
        failure {
            echo "CI failed — check logs"
        }
    }
}

