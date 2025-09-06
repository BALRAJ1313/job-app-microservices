pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'Maven 3.9.6'
        DOCKERHUB_USER = 'balraj1313'
        IMAGE_NAME = 'jobms'
    }

    stages {
        stage('Clone') {
            steps {
                git 'https://github.com/BALRAJ1313/job-app-microservices'
            }
        }

        stage('Build') {
            steps {
                dir('jobms') {
                    bat "${MAVEN_HOME}/bin/mvn clean package -DskipTests"
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                dir('jobms') {
                    bat "docker build -t %DOCKERHUB_USER%/%IMAGE_NAME%:latest ."
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        bat "echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin"
                        bat "docker push %DOCKERHUB_USER%/%IMAGE_NAME%:latest"
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                bat "kubectl set image deployment/job job=%DOCKERHUB_USER%/%IMAGE_NAME%:latest --record"
            }
        }
    }
}
