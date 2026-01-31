pipeline {
    agent any

    environment {
        MAVEN_OPTS = "-Dmaven.repo.local=.m2/repository"
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
    }

    post {
        success {
            echo "✅ CI SUCCESS: Backend built, frontend dependencies installed"
        }
        failure {
            echo "❌ CI FAILED: Check logs above"
        }
    }
}

