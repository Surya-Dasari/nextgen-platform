pipeline {
    agent any

    environment {
        DOCKER_REPO   = "docker.io/suryadasari31"
        IMAGE_TAG    = "${BUILD_NUMBER}"
        OCP_SERVER   = "https://api.rm2.thpm.p1.openshiftapps.com:6443"
        OCP_PROJECT  = "suryadasari31-dev"
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                sh '''
                for svc in apiservice authservice userservice
                do
                  cd services/$svc
                  mvn clean package -DskipTests
                  cd -
                done
                '''
            }
        }

        stage('Build Frontend') {
            steps {
                sh '''
                cd services/frontend
                npm install
                npm run build || true
                '''
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                for svc in apiservice authservice userservice frontend
                do
                  docker build -t $DOCKER_REPO/nextgen-$svc:$IMAGE_TAG services/$svc
                done
                '''
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                    echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                    for svc in apiservice authservice userservice frontend
                    do
                      docker push $DOCKER_REPO/nextgen-$svc:$IMAGE_TAG
                    done
                    '''
                }
            }
        }

        stage('Deploy to OpenShift') {
            steps {
                withCredentials([string(
                    credentialsId: 'openshift-token',
                    variable: 'OCP_TOKEN'
                )]) {
                    sh '''
                    oc login --token=$OCP_TOKEN --server=$OCP_SERVER --insecure-skip-tls-verify=true
                    oc project $OCP_PROJECT

                    oc apply -f services/postgres/
                    oc apply -f services/apiservice/
                    oc apply -f services/authservice/
                    oc apply -f services/userservice/
                    oc apply -f services/frontend/

                    oc rollout status deployment/apiservice --timeout=120s
                    oc rollout status deployment/authservice --timeout=120s
                    oc rollout status deployment/userservice --timeout=120s
                    oc rollout status deployment/nextgen-ui --timeout=120s
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "✅ CI/CD SUCCESS"
        }
        failure {
            echo "❌ CI/CD FAILED"
        }
    }
}

