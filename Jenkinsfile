pipeline {
    agent any

    environment {
        MAVEN_OPTS    = "-Dmaven.repo.local=.m2/repository"
        DOCKER_REPO   = "docker.io/suryadasari31"
        IMAGE_TAG     = "latest"

        OCP_SERVER    = "https://api.rm2.thpm.p1.openshiftapps.com:6443"
        OCP_NAMESPACE = "suryadasari31-dev"
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

        stage('Verify Tools') {
            when { branch 'develop' }
            steps {
                sh '''
                set -e
                mvn -v
                node -v
                npm -v
                docker -v
                oc version
                '''
            }
        }

        stage('Build Spring Boot Services') {
            when { branch 'develop' }
            steps {
                sh '''
                set -e
                for svc in apiservice authservice userservice
                do
                  echo "Building $svc"
                  cd services/$svc
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
                set -e
                cd services/frontend
                npm install
                '''
            }
        }

        stage('Docker Build Images') {
            when { branch 'develop' }
            steps {
                sh '''
                set -e
                docker build -t ${DOCKER_REPO}/nextgen-apiservice:${IMAGE_TAG} services/apiservice
                docker build -t ${DOCKER_REPO}/nextgen-authservice:${IMAGE_TAG} services/authservice
                docker build -t ${DOCKER_REPO}/nextgen-userservice:${IMAGE_TAG} services/userservice
                docker build -t ${DOCKER_REPO}/nextgen-ui:${IMAGE_TAG} services/frontend
                '''
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
                    set -e
                    echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                    docker push ${DOCKER_REPO}/nextgen-apiservice:${IMAGE_TAG}
                    docker push ${DOCKER_REPO}/nextgen-authservice:${IMAGE_TAG}
                    docker push ${DOCKER_REPO}/nextgen-userservice:${IMAGE_TAG}
                    docker push ${DOCKER_REPO}/nextgen-ui:${IMAGE_TAG}
                    '''
                }
            }
        }

        stage('Deploy to OpenShift') {
            when { branch 'develop' }
            steps {
                withCredentials([string(
                    credentialsId: 'openshift-token',
                    variable: 'OCP_TOKEN'
                )]) {
                    sh '''
                    set -e
                    oc login --token=$OCP_TOKEN --server=$OCP_SERVER --insecure-skip-tls-verify=true
                    oc project $OCP_NAMESPACE

                    oc apply -f services/apiservice/openshift.yaml
                    oc apply -f services/authservice/openshift.yaml
                    oc apply -f services/userservice/openshift.yaml
                    oc apply -f services/frontend/openshift.yaml

                    oc rollout restart deployment/apiservice
                    oc rollout restart deployment/authservice
                    oc rollout restart deployment/userservice
                    oc rollout restart deployment/nextgen-ui
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "✅ CI/CD SUCCESS: All services built, pushed, and deployed"
        }
        failure {
            echo "❌ CI/CD FAILED: Check Jenkins logs"
        }
    }
}

