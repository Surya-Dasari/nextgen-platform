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

        /* =======================
           CHECKOUT
           ======================= */
        stage('Checkout') {
            when { branch 'develop' }
            steps {
                checkout scm
            }
        }

        /* =======================
           VERIFY TOOLS
           ======================= */
        stage('Verify Tools') {
            steps {
                sh '''
                  mvn -v
                  node -v
                  npm -v
                  docker -v
                  oc version --client
                '''
            }
        }

        /* =======================
           CLEAN OPENSHIFT
           ======================= */
        stage('Clean OpenShift Namespace') {
            steps {
                withCredentials([string(
                    credentialsId: 'openshift-token',
                    variable: 'OCP_TOKEN'
                )]) {
                    sh '''
                      echo "===== OpenShift Login ====="
                      oc login --token=$OCP_TOKEN \
                        --server=$OCP_SERVER \
                        --insecure-skip-tls-verify=true

                      oc project $OCP_NAMESPACE

                      echo "===== Cleaning existing resources ====="
                      oc delete deployment apiservice authservice userservice nextgen-ui --ignore-not-found
                      oc delete service apiservice authservice userservice nextgen-ui --ignore-not-found
                      oc delete route apiservice authservice userservice nextgen-ui --ignore-not-found

                      sleep 10
                    '''
                }
            }
        }

        /* =======================
           BUILD BACKEND
           ======================= */
        stage('Build Spring Boot Services') {
            steps {
                sh '''
                  for svc in apiservice authservice userservice
                  do
                    echo "===== Building $svc ====="
                    cd services/$svc
                    mvn clean package -DskipTests
                    cd -
                  done
                '''
            }
        }

        /* =======================
           FRONTEND
           ======================= */
        stage('Prepare Frontend') {
            steps {
                sh '''
                  cd services/frontend
                  npm install
                '''
            }
        }

        /* =======================
           DOCKER BUILD
           ======================= */
        stage('Docker Build Images') {
            steps {
                sh '''
                  docker build -t docker.io/suryadasari31/nextgen-apiservice:latest services/apiservice
                  docker build -t docker.io/suryadasari31/nextgen-authservice:latest services/authservice
                  docker build -t docker.io/suryadasari31/nextgen-userservice:latest services/userservice
                  docker build -t docker.io/suryadasari31/nextgen-frontend:latest services/frontend
                '''
            }
        }

        /* =======================
           DOCKER PUSH
           ======================= */
        stage('Docker Push Images') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                      echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                      docker push docker.io/suryadasari31/nextgen-apiservice:latest
                      docker push docker.io/suryadasari31/nextgen-authservice:latest
                      docker push docker.io/suryadasari31/nextgen-userservice:latest
                      docker push docker.io/suryadasari31/nextgen-frontend:latest
                    '''
                }
            }
        }

        /* =======================
           DEPLOY + FORCE ROLLOUT
           ======================= */
        stage('Deploy to OpenShift') {
            steps {
                withCredentials([string(
                    credentialsId: 'openshift-token',
                    variable: 'OCP_TOKEN'
                )]) {
                    sh '''
                      oc login --token=$OCP_TOKEN \
                        --server=$OCP_SERVER \
                        --insecure-skip-tls-verify=true

                      oc project $OCP_NAMESPACE

                      echo "===== Applying manifests ====="
                      oc apply -f services/apiservice/openshift.yaml
                      oc apply -f services/authservice/openshift.yaml
                      oc apply -f services/userservice/openshift.yaml
                      oc apply -f services/frontend/openshift.yaml

                      echo "===== Force rollout (new revision) ====="
                      oc patch deployment apiservice \
                        -p "{\\"spec\\":{\\"template\\":{\\"metadata\\":{\\"annotations\\":{\\"jenkins-build\\":\\"$BUILD_NUMBER\\"}}}}}"

                      oc patch deployment authservice \
                        -p "{\\"spec\\":{\\"template\\":{\\"metadata\\":{\\"annotations\\":{\\"jenkins-build\\":\\"$BUILD_NUMBER\\"}}}}}"

                      oc patch deployment userservice \
                        -p "{\\"spec\\":{\\"template\\":{\\"metadata\\":{\\"annotations\\":{\\"jenkins-build\\":\\"$BUILD_NUMBER\\"}}}}}"

                      oc patch deployment nextgen-ui \
                        -p "{\\"spec\\":{\\"template\\":{\\"metadata\\":{\\"annotations\\":{\\"jenkins-build\\":\\"$BUILD_NUMBER\\"}}}}}"
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "✅ CI/CD SUCCESS: Clean deploy + forced rollout completed"
        }
        failure {
            echo "❌ CI/CD FAILED: Check Jenkins logs"
        }
        always {
            sh 'docker logout || true'
        }
    }
}

