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
                  mvn -v
                  node -v
                  npm -v
                  docker -v
                  oc version --client
                '''
            }
        }

        stage('Build Backend Services') {
            when { branch 'develop' }
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

        stage('Prepare Frontend') {
            when { branch 'develop' }
            steps {
                sh '''
                  cd services/frontend
                  npm install
                '''
            }
        }

        stage('Docker Build Images') {
            when { branch 'develop' }
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

        stage('Docker Push Images') {
            when { branch 'develop' }
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

        stage('Deploy to OpenShift (FORCE ROLLOUT)') {
            when { branch 'develop' }
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

                      echo "===== FORCEFUL ROLLOUT (new revision guaranteed) ====="
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
            echo "✅ CI/CD SUCCESS: Build → Push → Deploy → Force Rollout completed"
        }
        failure {
            echo "❌ CI/CD FAILED: Check Jenkins logs"
        }
        always {
            sh 'docker logout || true'
        }
    }
}

