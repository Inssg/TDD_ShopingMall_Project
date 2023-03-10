pipeline {
    agent any

    environment{
        REGION = 'ap-northeast-2'
        EKS_API = 'https://F0FA06DD385AA5980C9CA060719A94D5.gr7.ap-northeast-2.eks.amazonaws.com'
        EKS_CLUSTER_NAME = 'tddshoppingmall-cluster'
       EKS_JENKINS_CREDENTIAL_ID = 'kubectl-deploy-credentials'
       ECR_PATH = '828222123266.dkr.ecr.ap-northeast-2.amazonaws.com'
       ECR_IMAGE = 'tdd_shoppingmall_project'
       AWS_CREDENTIAL_ID = 'AWS_CREDENTIAL_ID'

    }
    stages {
        stage('Clone Repository'){
            steps {
            checkout scm
            }
        }
        stage('Build Jar wiht Gradle'){
            steps {
            sh 'cd /backend'
            sh './gradlew clean build'
            }
        }
        stage('Docker Build'){
            steps{
                script{
        docker.withRegistry("https://${ECR_PATH}", "ecr:${REGION}:${AWS_CREDENTIAL_ID}"){
            image = docker.build("${ECR_PATH}/${ECR_IMAGE}")
            }
            }
           }
        }
        stage('Push to ECR'){
            steps {
                script{
            docker.withRegistry("https://{ECR_PATH}", "ecr:${REGION}:${AWS_CREDENTIAL_ID}"){
                image.push("v${env.BUILD_NUMBER}")
            }
            }
           }
        }
        stage('CleanUp Images'){
            steps{
            sh"""
            docker rmi ${ECR_PATH}/${ECR_IMAGE}:v$BUILD_NUMBER
            docker rmi ${ECR_PATH}/${ECR_IMAGE}:latest
            """
            }
        }
        stage('Deploy to k8s'){
            steps{
        withKubeConfig([credentialsId: "{EKS_JENKINS_CREDENTIAL_ID}",
                        serverUrl: "${EKS_API}",
                        clusterName: "${EKS_CLUSTER_NAME}"]){
            sh "sed 's/IMAGE_VERSION/v${env.BUILD_ID}/g' service.yaml > output.yaml"
            sh "aws eks --region ${REGION} update-kubeconfig --name ${EKS_CLUSTER_NAME}"
            sh "kubectl apply -f output.yaml"
            sh "rm output.yaml"
             }
            }
        }
    }
}
