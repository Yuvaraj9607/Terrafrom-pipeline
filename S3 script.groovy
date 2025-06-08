pipeline {

    agent any

    environment {
        AWS_DEFAULT_REGION = 'us-east-1'
    }

    stages {

        stage('Checkout Terraform Code') {
            steps {
                git branch: 'master', url: 'https://github.com/Yuvaraj9607/Terrafrom-pipeline'
            }
        }

        stage('Setup Terraform') {
            steps {
                sh '''
                  if ! [ -x "$(command -v terraform)" ]; then
                    echo "Terraform not found, installing..."
                    curl -fsSL https://apt.releases.hashicorp.com/gpg | sudo gpg --dearmor -o /usr/share/keyrings/hashicorp-archive-keyring.gpg
                    echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/hashicorp.list
                    sudo apt-get update && sudo apt-get install terraform -y
                  else
                    echo "Terraform is already installed."
                  fi
                  terraform version
                '''
            }
        }

        stage('Terraform Init, Plan & Apply') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'aws-credentials', 
                    usernameVariable: 'AWS_ACCESS_KEY_ID', 
                    passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {

                    sh '''
                      terraform init
                      terraform plan -out=tfplan
                      terraform apply -auto-approve tfplan
                    '''
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Terraform applied successfully.'
        }
        failure {
            echo 'Terraform apply failed.'
        }
    }
}
