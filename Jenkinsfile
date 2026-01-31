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
           CLEAN OPENSHIFT FIRST
           ======================= */
        stage('Clean OpenShift Namespace') {
            steps {
                withCredentials([string(
                    credentialsId: 'openshift-token',
                    variable: 'OCP_TOKEN'
                )]) {
                    sh '''
                      echo "===== Logging into OpenShift ====="
                      oc login --token=$OCP_TOKEN \
                        --server=$OCP_SERVER \
                        --insecure-skip-tls-verify=true

                      oc project $OCP_NAMESPACE

                      echo "===== Deleting existing resources ====="
                      oc delete deployment apiservice authservice userservice nextgen-ui --ignore-not-found
                      oc delete service apiservice authservice userservice nextgen-ui --ignore-not-found
                      oc delete route apiservice authservice userservice nextgen-ui --ignore-not-found

                      echo "===== Waiting for cleanup ====="
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
                script {
                    def services = ["apiservice", "authservice", "userservice", "frontend"]
                    for (svc in services) {
                        sh """
                          echo "===== Docker build: ${svc} ====="
                          docker build \
                            -t ${DOCKER_REPO}/nextgen-${svc}:${IMAGE_TAG} \
                            services/${svc}
                        """
                    }
                }
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

                      for svc in apiservice authservice userservice frontend
                      do
                        docker push ${DOCKER_REPO}/nextgen-$svc:${IMAGE_TAG}
                      done
                    '''
                }
            }
        }

        /* =======================
           DEPLOY TO OPENSHIFT
           ======================= */
        stage('Deploy to OpenShift (Fresh)') {
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
                      for d in apiservice authservice userservice nextgen-ui
                      do
                        oc patch deployment $d \
                          -p "{\"spec\":{\"template\":{\"metadata\":{\"annotations\":{\"jenkins-build\":\"${BUILD_NUMBER}\"}}}}}"
                      done
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "✅ CLEAN CI/CD SUCCESS: Fresh deploy completed"
        }
        failure {
            echo "❌ CI/CD FAILED: Check Jenkins logs"
        }
        always {
            sh 'docker logout || true'
        }
    }
}

